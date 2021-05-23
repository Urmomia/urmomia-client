package dev.urmomia.gui;

import static dev.urmomia.utils.Utils.getWindowHeight;
import static dev.urmomia.utils.Utils.getWindowWidth;
import static dev.urmomia.utils.Utils.mc;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SUPER;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import dev.urmomia.MainClient;
import dev.urmomia.gui.renderer.GuiDebugRenderer;
import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.tabs.TabScreen;
import dev.urmomia.gui.tabs.builtin.HudTab;
import dev.urmomia.gui.utils.Cell;
import dev.urmomia.gui.widgets.WRoot;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.gui.widgets.containers.WContainer;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.rendering.Matrices;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.misc.CursorStyle;
import dev.urmomia.utils.misc.input.Input;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public abstract class WidgetScreen extends Screen {
    private static final GuiRenderer RENDERER = new GuiRenderer();
    private static final GuiDebugRenderer DEBUG_RENDERER = new GuiDebugRenderer();

    public Runnable taskAfterRender;
    protected Runnable enterAction;

    protected Screen parent;
    private final WContainer root;

    protected final GuiTheme theme;

    public boolean locked;
    private boolean closed;
    private boolean onClose;
    private boolean debug;

    private double lastMouseX, lastMouseY;

    public double animProgress;

    private List<Runnable> onClosed;

    public WidgetScreen(GuiTheme theme, String title) {
        super(new LiteralText(title));

        this.parent = mc.currentScreen;
        this.root = new WFullScreenRoot();
        this.theme = theme;

        root.theme = theme;

        if (parent != null) {
            animProgress = 1;

            if (this instanceof TabScreen && parent instanceof TabScreen && !(this instanceof HudTab.HudScreen)) {
                Modules.get().get(HUD.class).toggle();
                parent = ((TabScreen) parent).parent;
            }
        }
    }

    public <W extends WWidget> Cell<W> add(W widget) {
        return root.add(widget);
    }

    public void clear() {
        root.clear();
    }

    public void invalidate() {
        root.invalidate();
    }

    @Override
    protected void init() {
        MainClient.EVENT_BUS.subscribe(this);

        closed = false;
    }

    public void onClosed(Runnable action) {
        if (onClosed == null) onClosed = new ArrayList<>(2);
        onClosed.add(action);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (locked) return false;

        double s = mc.getWindow().getScaleFactor();
        mouseX *= s;
        mouseY *= s;

        return root.mouseClicked(mouseX, mouseY, button, false);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (locked) return false;

        double s = mc.getWindow().getScaleFactor();
        mouseX *= s;
        mouseY *= s;

        return root.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (locked) return;

        double s = mc.getWindow().getScaleFactor();
        mouseX *= s;
        mouseY *= s;

        root.mouseMoved(mouseX, mouseY, lastMouseX, lastMouseY);

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (locked) return false;

        root.mouseScrolled(amount);

        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (locked) return false;

        if ((modifiers == GLFW_MOD_CONTROL || modifiers == GLFW_MOD_SUPER) && keyCode == GLFW_KEY_9) {
            debug = !debug;
            return true;
        }

        if ((keyCode == GLFW_KEY_ENTER || keyCode == GLFW_KEY_KP_ENTER) && enterAction != null) {
            enterAction.run();
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (locked) return false;

        return root.keyPressed(keyCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void keyRepeated(int key, int mods) {
        if (locked) return;

        root.keyRepeated(key, mods);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (locked) return false;

        return root.charTyped(chr);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!Utils.canUpdate()) renderBackground(matrices);

        double s = mc.getWindow().getScaleFactor();
        mouseX *= s;
        mouseY *= s;

        animProgress += delta / 20 * 14;
        animProgress = Utils.clamp(animProgress, 0, 1);

        GuiKeyEvents.canUseKeys = true;

        // Apply projection without scaling
        Utils.unscaledProjection();
        Matrices.begin(new MatrixStack());

        onRenderBefore(delta);

        RENDERER.theme = theme;
        theme.beforeRender();

        RENDERER.begin();
        RENDERER.setAlpha(animProgress);
        root.render(RENDERER, mouseX, mouseY, delta / 20);
        RENDERER.setAlpha(1);
        RENDERER.end();

        boolean tooltip = RENDERER.renderTooltip(mouseX, mouseY, delta / 20);

        if (debug) {
            DEBUG_RENDERER.render(root);
            if (tooltip) DEBUG_RENDERER.render(RENDERER.tooltipWidget);
        }

        Utils.scaledProjection();

        if (taskAfterRender != null) {
            taskAfterRender.run();
            taskAfterRender = null;
        }
    }

    protected void onRenderBefore(float delta) {}

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        root.invalidate();
    }

    @Override
    public void onClose() {
        if (!locked) {
            boolean preOnClose = onClose;
            onClose = true;

            removed();

            onClose = preOnClose;
        }
    }

    @Override
    public void removed() {
        if (!closed) {
            closed = true;
            onClosed();

            Input.setCursorStyle(CursorStyle.Default);

            loopWidgets(root, widget -> {
                if (widget instanceof WTextBox) {
                    WTextBox textBox = (WTextBox) widget;

                    if (textBox.isFocused()) textBox.setFocused(false);
                }
            });

            if (MainClient.hudWasOn == true && !(Modules.get().get(HUD.class).isActive())) {
                Modules.get().get(HUD.class).toggle();
            }

            MainClient.EVENT_BUS.unsubscribe(this);
            GuiKeyEvents.canUseKeys = true;

            if (onClosed != null) {
                for (Runnable action : onClosed) action.run();
            }

            if (onClose) mc.openScreen(parent);
        }
    }

    private void loopWidgets(WWidget widget, Consumer<WWidget> action) {
        action.accept(widget);

        if (widget instanceof WContainer) {
            for (Cell<?> cell : ((WContainer) widget).cells) loopWidgets(cell.widget(), action);
        }
    }

    protected void onClosed() {}

    @Override
    public boolean shouldCloseOnEsc() {
        return !locked;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class WFullScreenRoot extends WContainer implements WRoot {
        private boolean valid;

        @Override
        public void invalidate() {
            valid = false;
        }

        @Override
        protected void onCalculateSize() {
            width = getWindowWidth();
            height = getWindowHeight();
        }

        @Override
        protected void onCalculateWidgetPositions() {
            for (Cell<?> cell : cells) {
                cell.x = 0;
                cell.y = 0;

                cell.width = width;
                cell.height = height;

                cell.alignWidget();
            }
        }

        @Override
        public boolean render(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (!valid) {
                calculateSize();
                calculateWidgetPositions();

                valid = true;
                mouseMoved(mc.mouse.getX(), mc.mouse.getY(), mc.mouse.getX(), mc.mouse.getY());
            }

            return super.render(renderer, mouseX, mouseY, delta);
        }
    }
}
