package dev.urmomia.gui.renderer;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.renderer.operations.TextOperation;
import dev.urmomia.gui.renderer.packer.GuiTexture;
import dev.urmomia.gui.renderer.packer.TexturePacker;
import dev.urmomia.gui.renderer.packer.TextureRegion;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.rendering.DrawMode;
import dev.urmomia.rendering.MeshBuilder;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.misc.Pool;
import dev.urmomia.utils.render.ByteTexture;
import dev.urmomia.utils.render.color.Color;
import dev.urmomia.utils.render.color.SettingColor;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static dev.urmomia.utils.Utils.getWindowHeight;
import static dev.urmomia.utils.Utils.getWindowWidth;
import static org.lwjgl.opengl.GL11.*;

public class GuiRenderer {
    private static final Color WHITE = new Color(255, 255, 255);

    private static final TexturePacker TEXTURE_PACKER = new TexturePacker();
    private static ByteTexture TEXTURE;

    public static GuiTexture CIRCLE;
    public static GuiTexture TRIANGLE;
    public static GuiTexture EDIT;
    public static GuiTexture RESET;
    public static GuiTexture MENUCLOSE;
    public static GuiTexture MENUOPEN;

    public GuiTheme theme;

    private final MeshBuilder mb = new MeshBuilder();
    private final MeshBuilder mbTex = new MeshBuilder();

    private final Pool<Scissor> scissorPool = new Pool<>(Scissor::new);
    private final Stack<Scissor> scissorStack = new Stack<>();

    private final Pool<TextOperation> textPool = new Pool<>(TextOperation::new);
    private final List<TextOperation> texts = new ArrayList<>();

    private final List<Runnable> postTasks = new ArrayList<>();

    public String tooltip, lastTooltip;
    public WWidget tooltipWidget;
    private double tooltipAnimProgress;

    public static GuiTexture addTexture(Identifier id) {
        return TEXTURE_PACKER.add(id);
    }

    public static void init() {
        CIRCLE = addTexture(new Identifier("urmomia-client", "textures/icons/gui/circle.png"));
        TRIANGLE = addTexture(new Identifier("urmomia-client", "textures/icons/gui/triangle.png"));
        EDIT = addTexture(new Identifier("urmomia-client", "textures/icons/gui/edit.png"));
        RESET = addTexture(new Identifier("urmomia-client", "textures/icons/gui/reset.png"));
        MENUCLOSE = addTexture(new Identifier("urmomia-client", "textures/icons/gui/menuclose.png"));
        MENUOPEN = addTexture(new Identifier("urmomia-client", "textures/icons/gui/menuopen.png"));

        TEXTURE = TEXTURE_PACKER.pack();
    }

    public void begin() {
        glEnable(GL_SCISSOR_TEST);
        scissorStart(0, 0, getWindowWidth(), getWindowHeight());
    }

    public void end() {
        scissorEnd();

        for (Runnable task : postTasks) task.run();
        postTasks.clear();

        glDisable(GL_SCISSOR_TEST);
    }

    private void beginRender() {
        mb.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
        mbTex.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);
    }

    private void endRender() {
        mb.end();
        TEXTURE.bindTexture();
        mbTex.texture = true;
        mbTex.end();

        // Normal text
        theme.textRenderer().begin(theme.scale(1));
        for (TextOperation text : texts) {
            if (!text.title) text.run(textPool);
        }
        theme.textRenderer().end();

        // Title text
        theme.textRenderer().begin(theme.scale(1.25));
        for (TextOperation text : texts) {
            if (text.title) text.run(textPool);
        }
        theme.textRenderer().end();

        texts.clear();
    }

    public void scissorStart(double x, double y, double width, double height) {
        if (!scissorStack.isEmpty()) {
            Scissor parent = scissorStack.peek();

            if (x < parent.x) x = parent.x;
            else if (x + width > parent.x + parent.width) width -= (x + width) - (parent.x + parent.width);

            if (y < parent.y) y = parent.y;
            else if (y + height > parent.y + parent.height) height -= (y + height) - (parent.y + parent.height);

            parent.apply();
            endRender();
        }

        scissorStack.push(scissorPool.get().set(x, y, width, height));
        beginRender();
    }

    public void scissorEnd() {
        Scissor scissor = scissorStack.pop();

        scissor.apply();
        endRender();
        for (Runnable task : scissor.postTasks) task.run();
        if (!scissorStack.isEmpty()) beginRender();

        scissorPool.free(scissor);
    }

    public boolean renderTooltip(double mouseX, double mouseY, double delta) {
        tooltipAnimProgress += (tooltip != null ? 1 : -1) * delta * 14;
        tooltipAnimProgress = Utils.clamp(tooltipAnimProgress, 0, 1);

        boolean toReturn = false;

        if (tooltipAnimProgress > 0) {
            if (tooltip != null && !tooltip.equals(lastTooltip)) {
                tooltipWidget = theme.tooltip(tooltip);
                tooltipWidget.init();
            }

            tooltipWidget.move(-tooltipWidget.x + mouseX + 12, -tooltipWidget.y + mouseY + 12);

            setAlpha(tooltipAnimProgress);

            begin();
            tooltipWidget.render(this, mouseX, mouseY, delta);
            end();

            setAlpha(1);

            lastTooltip = tooltip;
            toReturn = true;
        }

        tooltip = null;
        return toReturn;
    }

    public void setAlpha(double a) {
        mb.alpha = a;
        mbTex.alpha = a;

        theme.textRenderer().setAlpha(a);
    }

    public void tooltip(String text) {
        tooltip = text;
    }

    public void quad(double x, double y, double width, double height, Color cTopLeft, Color cTopRight, Color cBottomRight, Color cBottomLeft) {
        mb.quad(x, y, width, height, cTopLeft, cTopRight, cBottomRight, cBottomLeft);
    }
    public void quad(double x, double y, double width, double height, Color colorLeft, Color colorRight) {
        quad(x, y, width, height, colorLeft, colorLeft, colorRight, colorLeft);
    }
    public void quad(double x, double y, double width, double height, Color color) {
        quad(x, y, width, height, color, color);
    }
    public void quad(WWidget widget, Color color) {
        quad(widget.x, widget.y, widget.width, widget.height, color);
    }

    public void boxEdges(double x, double y, double width, double height, Color color) {
        mb.boxEdges(x, y, 0, x + width, y + height, 0, color, 0);
    }

    public void boxEdges(WWidget widget, Color color) {
        boxEdges(widget.x, widget.y, widget.width, widget.height, color);
    }

    public void guiBorder(double x, double y, double width, double height, Color color) {
        mb.guiBorder(x, y, width, height, color);
    }

    public void guiBorder(WWidget widget, Color color) {
        guiBorder(widget.x, widget.y, widget.width, widget.height, color);
    }

    public void horizontalGradientQuad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color left, Color right) {
        mb.quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, left, right, right, left);
    }

    public void horizontalGradientQuad(double x, double y, double width, double height, Color left, Color right) {
        horizontalGradientQuad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, left, right);
    }

    public void horizontalGradientQuad(WWidget widget, Color left, Color right) {
        horizontalGradientQuad(widget.x, widget.y, widget.width, widget.height, left, right);
    }

    public void horizontalGradientGuiBorder(double x, double y, double width, double height, Color left, Color right) {
        mb.horizontalGradientGuiBorder(x, y, width, height, left, right);
    }

    public void horizontalGradientGuiBorder(WWidget widget, Color left, Color right) {
        horizontalGradientGuiBorder(widget.x, widget.y, widget.width, widget.height, left, right);
    }

    public void verticalGradientGuiBorder(double x, double y, double width, double height, Color top, Color bottom) {
        mb.verticalGradientGuiBorder(x, y, width, height, top, bottom);
    }

    public void verticalGradientGuiBorder(WWidget widget, Color top, Color bottom) {
        verticalGradientGuiBorder(widget.x, widget.y, widget.width, widget.height, top, bottom);
    }

    public void diagonalGradientGuiBorder(double x, double y, double width, double height, Color color1, Color color2) {
        mb.diagonalGradientGuiBorder(x, y, width, height, color1, color2);
    }

    public void diagonalGradientGuiBorder(WWidget widget, Color color1, Color color2) {
        diagonalGradientGuiBorder(widget.x, widget.y, widget.width, widget.height, color1, color2);
    }
    
    public void quad(double x, double y, double width, double height, GuiTexture texture, Color color) {
        mbTex.texQuad(x, y, width, height, texture.get(width, height), color);
    }

    public void rotatedQuad(double x, double y, double width, double height, double rotation, GuiTexture texture, Color color) {
        TextureRegion region = texture.get(width, height);

        double rad = Math.toRadians(rotation);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double oX = x + width / 2;
        double oY = y + height / 2;

        double _x1 = ((x - oX) * cos) - ((y - oY) * sin) + oX;
        double _y1 = ((y - oY) * cos) + ((x - oX) * sin) + oY;
        mbTex.pos(_x1, _y1, 0).color(color).texture(region.x1, region.y1).endVertex();

        double _x = ((x + width - oX) * cos) - ((y - oY) * sin) + oX;
        double _y = ((y - oY) * cos) + ((x + width - oX) * sin) + oY;
        mbTex.pos(_x, _y, 0).color(color).texture(region.x2, region.y1).endVertex();

        double _x2 = ((x + width - oX) * cos) - ((y + height - oY) * sin) + oX;
        double _y2 = ((y + height - oY) * cos) + ((x + width - oX) * sin) + oY;
        mbTex.pos(_x2, _y2, 0).color(color).texture(region.x2, region.y2).endVertex();

        mbTex.pos(_x1, _y1, 0).color(color).texture(region.x1, region.y1).endVertex();

        mbTex.pos(_x2, _y2, 0).color(color).texture(region.x2, region.y2).endVertex();

        _x = ((x - oX) * cos) - ((y + height - oY) * sin) + oX;
        _y = ((y + height - oY) * cos) + ((x - oX) * sin) + oY;
        mbTex.pos(_x, _y, 0).color(color).texture(region.x1, region.y2).endVertex();
    }

    public void text(String text, double x, double y, Color color, boolean title) {
        texts.add(getOp(textPool, x, y, color).set(text, theme.textRenderer(), title));
    }

    public void texture(double x, double y, double width, double height, double rotation, AbstractTexture texture) {
        post(() -> {
            mbTex.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR_TEXTURE);

            mbTex.pos(x, y, 0).color(WHITE).texture(0, 0).endVertex();
            mbTex.pos(x + width, y, 0).color(WHITE).texture(1, 0).endVertex();
            mbTex.pos(x + width, y + height, 0).color(WHITE).texture(1, 1).endVertex();
            mbTex.pos(x, y, 0).color(WHITE).texture(0, 0).endVertex();
            mbTex.pos(x + width, y + height, 0).color(WHITE).texture(1, 1).endVertex();
            mbTex.pos(x, y + height, 0).color(WHITE).texture(0, 1).endVertex();

            texture.bindTexture();
            GL11.glPushMatrix();
            GL11.glTranslated(x + width / 2, y + height / 2, 0);
            GL11.glRotated(rotation, 0, 0, 1);
            GL11.glTranslated(-x - width / 2, -y - height / 2, 0);
            mbTex.end();
            GL11.glPopMatrix();
        });
    }

    public void post(Runnable task) {
        scissorStack.peek().postTasks.add(task);
    }

    public void absolutePost(Runnable task) {
        postTasks.add(task);
    }

    private <T extends GuiRenderOperation<T>> T getOp(Pool<T> pool, double x, double y, Color color) {
        T op = pool.get();
        op.set(x, y, color);
        return op;
    }
}
