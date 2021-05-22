package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.themes.urmomia.ThemeGuiTheme;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.utils.AlignmentX;
import dev.urmomia.gui.widgets.pressable.WPressable;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.Utils;

import static dev.urmomia.utils.Utils.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class WThemeModule extends WPressable implements ThemeWidget {
    private final Module module;

    private double titleWidth;

    private double animationProgress1;

    private double animationProgress2;

    private double f;
    private double p;

    public WThemeModule(Module module) {
        this.module = module;

        if (module.isActive()) {
            animationProgress1 = 1;
            animationProgress2 = 1;
        } else {
            animationProgress1 = 0;
            animationProgress2 = 0;
        }
    }

    @Override
    public double pad() {
        return theme.scale(4);
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();

        if (titleWidth == 0) titleWidth = theme.textWidth(module.title);

        width = pad + titleWidth + pad;
        height = pad + theme.textHeight() + pad;
    }

    @Override
    protected void onPressed(int button) {
        if (button == GLFW_MOUSE_BUTTON_LEFT) module.doAction(Utils.canUpdate());
        else if (button == GLFW_MOUSE_BUTTON_RIGHT) mc.openScreen(theme.moduleScreen(module));
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        ThemeGuiTheme theme = theme();
        double pad = pad();

        floppa(theme);

        animationProgress1 += delta * 4 * ((module.isActive() || mouseOver) ? 1 : -1);
        animationProgress1 = Utils.clamp(animationProgress1, 0, 1);

        animationProgress2 += delta * 6 * (module.isActive() ? 1 : -1);
        animationProgress2 = Utils.clamp(animationProgress2, 0, 1);

        if (animationProgress1 > 0) {
            renderer.quad(x + (f), y, (width - (f + f)) * animationProgress1, height, theme.moduleBackground.get());
        }
        if (animationProgress2 > 0) {
            renderer.quad(x + (f), y + height * (1 - animationProgress2), theme.scale(p), height * animationProgress2, theme.accentColor.get());
        }

        double x = this.x + pad;
        double w = width - pad * 2;

        if (theme.moduleAlignment.get() == AlignmentX.Center) {
            x += w / 2 - titleWidth / 2;
        }
        else if (theme.moduleAlignment.get() == AlignmentX.Right) {
            x += w - titleWidth;
        }

        renderer.text(module.title, x, y + pad, theme.textColor.get(), false);
    }

    public void floppa(ThemeGuiTheme theme) {
        switch(theme.borderGui.get()) {
            case None: f = 0; p = 2; break;
            case Horizontal:
            case Vertical:
            case Diagonal:
            case Solid: f = 2; p = 0; break;
        }
    }
}
