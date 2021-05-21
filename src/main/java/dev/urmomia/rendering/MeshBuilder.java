package dev.urmomia.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.urmomia.events.render.RenderEvent;
import dev.urmomia.gui.renderer.packer.TextureRegion;
import dev.urmomia.utils.render.color.Color;
import dev.urmomia.utils.world.Dir;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;

import static org.lwjgl.opengl.GL11.*;

public class MeshBuilder {
    private final BufferBuilder buffer;
    private double offsetX, offsetY, offsetZ;

    public double alpha = 1;

    public boolean depthTest = false;
    public boolean texture = false;

    private int count;

    public MeshBuilder(int initialCapacity) {
        buffer = new BufferBuilder(initialCapacity);
    }

    public MeshBuilder() {
        buffer = new BufferBuilder(2097152);
    }

    public void begin(RenderEvent event, DrawMode drawMode, VertexFormat format) {
        if (event != null) {
            offsetX = -event.offsetX;
            offsetY = -event.offsetY;
            offsetZ = -event.offsetZ;
        } else {
            offsetX = 0;
            offsetY = 0;
            offsetZ = 0;
        }

        buffer.begin(drawMode.toOpenGl(), format);
        count = 0;
    }

    public void end() {
        buffer.end();

        //if (count > 0) {
            glPushMatrix();
            RenderSystem.multMatrix(Matrices.getTop());

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            if (depthTest) RenderSystem.enableDepthTest();
            else RenderSystem.disableDepthTest();
            RenderSystem.disableAlphaTest();
            if (texture) RenderSystem.enableTexture();
            else RenderSystem.disableTexture();
            RenderSystem.disableLighting();
            RenderSystem.disableCull();
            glEnable(GL_LINE_SMOOTH);
            RenderSystem.lineWidth(1);
            RenderSystem.color4f(1, 1, 1, 1);
            GlStateManager.shadeModel(GL_SMOOTH);

            BufferRenderer.draw(buffer);

            RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            RenderSystem.enableTexture();
            glDisable(GL_LINE_SMOOTH);

            glPopMatrix();
        //}
    }

    public boolean isBuilding() {
        return buffer.isBuilding();
    }

    public MeshBuilder pos(double x, double y, double z) {
        buffer.vertex(x + offsetX, y + offsetY, z + offsetZ);
        return this;
    }

    public MeshBuilder texture(double x, double y) {
        buffer.texture((float) x, (float) y);
        return this;
    }

    public MeshBuilder color(Color color) {
        buffer.color(color.r / 255f, color.g / 255f, color.b / 255f, color.a / 255f * (float) alpha);
        return this;
    }

    public MeshBuilder color(int color) {
        buffer.color(Color.toRGBAR(color) / 255f, Color.toRGBAG(color) / 255f, Color.toRGBAB(color) / 255f, Color.toRGBAA(color) / 255f * (float) alpha);
        return this;
    }

    public void endVertex() {
        buffer.next();
    }

    // Quads, 2 dimensional, top left to bottom right

    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {
        pos(x1, y1, z1).color(topLeft).endVertex();
        pos(x2, y2, z2).color(topRight).endVertex();
        pos(x3, y3, z3).color(bottomRight).endVertex();
        pos(x1, y1, z1).color(topLeft).endVertex();
        pos(x3, y3, z3).color(bottomRight).endVertex();
        pos(x4, y4, z4).color(bottomLeft).endVertex();
    }

    public void quad(double x, double y, double width, double height, Color topLeft, Color topRight, Color bottomRight, Color bottomLeft) {
        quad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, topLeft, topRight, bottomRight, bottomLeft);
    }

    public void verticalGradientQuad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color top, Color bottom) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, top, top, bottom, bottom);
    }

    public void verticalGradientQuad(double x, double y, double width, double height, Color top, Color bottom) {
        verticalGradientQuad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, top, bottom);
    }

    public void horizontalGradientQuad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color left, Color right) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, left, right, right, left);
    }

    public void horizontalGradientQuad(double x, double y, double width, double height, Color left, Color right) {
        horizontalGradientQuad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, left, right);
    }

    public void quad(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color color) {
        quad(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, color, color, color, color);
    }

    public void quad(double x, double y, double width, double height, Color color) {
        quad(x, y, 0, x + width, y, 0, x + width, y + height, 0, x, y + height, 0, color);
    }

    public void horizontalQuad(double x1, double z1, double x2, double z2, double y, Color color) {
        quad(x1, y, z1, x1, y, z2, x2, y, z2, x2, y, z1, color);
    }

    public void verticalQuad(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        quad(x1, y1, z1, x1, y2, z1, x2, y2, z2, x2, y1, z2, color);
    }

    public void texQuad(double x, double y, double width, double height, TextureRegion tex, Color color) {
        pos(x, y, 0).color(color).texture(tex.x1, tex.y1).endVertex();
        pos(x + width, y, 0).color(color).texture(tex.x2, tex.y1).endVertex();
        pos(x + width, y + height, 0).color(color).texture(tex.x2, tex.y2).endVertex();

        pos(x, y, 0).color(color).texture(tex.x1, tex.y1).endVertex();
        pos(x + width, y + height, 0).color(color).texture(tex.x2, tex.y2).endVertex();
        pos(x, y + height, 0).color(color).texture(tex.x1, tex.y2).endVertex();
    }

    public void boxSides(double x1, double y1, double z1, double x2, double y2, double z2, Color color, int excludeDir) {
        if (Dir.is(excludeDir, Dir.DOWN)) quad(x1, y1, z1, x1, y1, z2, x2, y1, z2, x2, y1, z1, color); // Bottom
        if (Dir.is(excludeDir, Dir.UP)) quad(x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, color); // Top

        if (Dir.is(excludeDir, Dir.NORTH)) quad(x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, color); // Front
        if (Dir.is(excludeDir, Dir.SOUTH)) quad(x1, y1, z2, x1, y2, z2, x2, y2, z2, x2, y1, z2, color); // Back

        if (Dir.is(excludeDir, Dir.WEST)) quad(x1, y1, z1, x1, y2, z1, x1, y2, z2, x1, y1, z2, color); // Left
        if (Dir.is(excludeDir, Dir.EAST)) quad(x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, color); // Right
    }

    // LINES

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color startColor, Color endColor) {
        pos(x1, y1, z1).color(startColor).endVertex();
        pos(x2, y2, z2).color(endColor).endVertex();
    }

    public void line(double x1, double y1, double x2, double y2, Color startColor, Color endColor) {
        line(x1, y1, 0, x2, y2, 0, startColor, endColor);
    }

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        line(x1, y1, z1, x2, y2, z2, color, color);
    }

    public void line(double x1, double y1, double x2, double y2, Color color) {
        line(x1, y1, 0, x2, y2, 0, color);
    }

    public void boxEdges(double x1, double y1, double z1, double x2, double y2, double z2, Color color, int excludeDir) {
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.NORTH)) line(x1, y1, z1, x1, y2, z1, color);
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.SOUTH)) line(x1, y1, z2, x1, y2, z2, color);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.NORTH)) line(x2, y1, z1, x2, y2, z1, color);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.SOUTH)) line(x2, y1, z2, x2, y2, z2, color);

        if (Dir.is(excludeDir, Dir.NORTH)) line(x1, y1, z1, x2, y1, z1, color);
        if (Dir.is(excludeDir, Dir.NORTH)) line(x1, y2, z1, x2, y2, z1, color);
        if (Dir.is(excludeDir, Dir.SOUTH)) line(x1, y1, z2, x2, y1, z2, color);
        if (Dir.is(excludeDir, Dir.SOUTH)) line(x1, y2, z2, x2, y2, z2, color);

        if (Dir.is(excludeDir, Dir.WEST)) line(x1, y1, z1, x1, y1, z2, color);
        if (Dir.is(excludeDir, Dir.WEST)) line(x1, y2, z1, x1, y2, z2, color);
        if (Dir.is(excludeDir, Dir.EAST)) line(x2, y1, z1, x2, y1, z2, color);
        if (Dir.is(excludeDir, Dir.EAST)) line(x2, y2, z1, x2, y2, z2, color);
    }

    public void boxEdges(double x, double y, double width, double height, Color color) {
        boxEdges(x, y, 0, x + width, y + height, 0, color, 0);
    }

    public void gradientLine(double x1, double y1, double x2, double y2, Color left, Color right, Boolean vertical) {
        if (!(vertical)) horizontalGradientQuad(x1, y1, 0, x2, y1, 0, x2, y2, 0, x1, y2, 0, left, right);
        if (vertical) verticalGradientQuad(x1, y1, 0, x2, y1, 0, x2, y2, 0, x1, y2, 0, left, right);
    }

    public void quadLine(double x1, double y1, double x2, double y2, Color color) {
        quad(x1, y1, 0, x2, y1, 0, x2, y2, 0, x1, y2, 0, color);
    }

    public void horizontalGradientLine(double x1, double y1, double x2, double y2, Color left, Color right) {
        horizontalGradientQuad(x1, y1 + 2, 0, x2, y1 + 2, 0, x2, y2 + 2, 0, x1, y2 + 2, 0, left, right);
    }

    public void guiBorder(double x1, double y1, double x2, double y2, Color color, int excludeDir) {
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.NORTH)) quadLine(x1 + 2, y1, x1, y2, color);
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.SOUTH)) quadLine(x1 + 2, y1, x1, y2, color);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.NORTH)) quadLine(x2 - 2, y1, x2, y2, color);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.SOUTH)) quadLine(x2 - 2, y1, x2, y2, color);

        if (Dir.is(excludeDir, Dir.NORTH)) quadLine(x1, y1, x2, y1, color);
        if (Dir.is(excludeDir, Dir.NORTH)) quadLine(x1, y2 - 2, x2, y2, color);
        if (Dir.is(excludeDir, Dir.SOUTH)) quadLine(x1, y1, x2, y1, color);
        if (Dir.is(excludeDir, Dir.SOUTH)) quadLine(x1, y2 - 2, x2, y2, color);

        if (Dir.is(excludeDir, Dir.WEST)) quadLine(x1, y1, x1, y1, color);
        if (Dir.is(excludeDir, Dir.WEST)) quadLine(x1, y2, x1, y2, color);
        if (Dir.is(excludeDir, Dir.EAST)) quadLine(x2, y1, x2, y1, color);
        if (Dir.is(excludeDir, Dir.EAST)) quadLine(x2, y2, x2, y2, color);
    }

    public void guiBorder(double x, double y, double width, double height, Color color) {
        guiBorder(x, y, x + width, y + height, color, 0);
    }

    public void gradientBoxEdges(double x1, double y1, double z1, double x2, double y2, double z2, Color leftColor1, Color leftColor2, Color upColor1, Color upColor2, Color rightColor1, Color rightColor2, Color downColor1, Color downColor2, boolean vertical, int excludeDir) {
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.NORTH)) gradientLine(x1 + 2, y1, x1, y2, leftColor1, leftColor2, vertical);
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x1 + 2, y1, x1, y2, leftColor1, leftColor2, vertical);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.NORTH)) gradientLine(x2 - 2, y1, x2, y2, rightColor1, rightColor2, vertical);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x2 - 2, y1, x2, y2, rightColor1, rightColor2, vertical);

        if (Dir.is(excludeDir, Dir.NORTH)) gradientLine(x1, y1 + 2, x2, y1 - 2, upColor1, upColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.NORTH)) gradientLine(x1, y2 - 2, x2, y2 + 2, downColor1, downColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x1, y1 + 2, x2, y1 - 2, upColor1, upColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x1, y2 - 2, x2, y2 + 2, downColor1, downColor2, !(vertical));

        if (Dir.is(excludeDir, Dir.WEST)) gradientLine(x1, y1, x1, y1, leftColor1, leftColor1, vertical);
        if (Dir.is(excludeDir, Dir.WEST)) gradientLine(x1, y2, x1, y2, rightColor1, rightColor1, vertical);
        if (Dir.is(excludeDir, Dir.EAST)) gradientLine(x2, y1, x2, y1, upColor2, upColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.EAST)) gradientLine(x2, y2, x2, y2, downColor2, downColor2, !(vertical));
    }

    public void gradientGuiBorder(double x1, double y1, double z1, double x2, double y2, double z2, Color leftColor1, Color leftColor2, Color upColor1, Color upColor2, Color rightColor1, Color rightColor2, Color downColor1, Color downColor2, boolean vertical, int excludeDir) {
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.NORTH)) gradientLine(x1 + 2, y1, x1, y2, leftColor1, leftColor2, vertical);
        if (Dir.is(excludeDir, Dir.WEST) && Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x1 + 2, y1, x1, y2, leftColor1, leftColor2, vertical);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.NORTH)) gradientLine(x2 - 2, y1, x2, y2, rightColor1, rightColor2, vertical);
        if (Dir.is(excludeDir, Dir.EAST) && Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x2 - 2, y1, x2, y2, rightColor1, rightColor2, vertical);

        if (Dir.is(excludeDir, Dir.NORTH)) gradientLine(x1, y1, x2, y1, upColor1, upColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.NORTH)) gradientLine(x1, y2 - 2, x2, y2, downColor1, downColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x1, y1, x2, y1, upColor1, upColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.SOUTH)) gradientLine(x1, y2 - 2, x2, y2, downColor1, downColor2, !(vertical));

        if (Dir.is(excludeDir, Dir.WEST)) gradientLine(x1, y1, x1, y1, leftColor1, leftColor1, vertical);
        if (Dir.is(excludeDir, Dir.WEST)) gradientLine(x1, y2, x1, y2, rightColor1, rightColor1, vertical);
        if (Dir.is(excludeDir, Dir.EAST)) gradientLine(x2, y1, x2, y1, upColor2, upColor2, !(vertical));
        if (Dir.is(excludeDir, Dir.EAST)) gradientLine(x2, y2, x2, y2, downColor2, downColor2, !(vertical));
    }

    public void horizontalGradientGuiBorder(double x, double y, double width, double height, Color color1, Color color2) {
        gradientGuiBorder(x, y, 0, x + width, y + height, 0, color1, color1, color1, color2, color2, color2, color1, color2, true, 0);
    }

    public void diagonalGradientGuiBorder(double x, double y, double width, double height, Color color1, Color color2) {
        gradientGuiBorder(x, y, 0, x + width, y + height, 0, color1, color2, color1, color2, color2, color1, color2, color1, true, 0);
    }

    public void verticalGradientGuiBorder(double x, double y, double width, double height, Color color1, Color color2) {
        gradientGuiBorder(x, y, 0, x + width, y + height, 0, color1, color2, color1, color1, color1, color2, color2, color2, true, 0);
    }

    public void horizontalGradientBoxEdges(double x, double y, double width, double height, Color color1, Color color2) {
        gradientBoxEdges(x, y, 0, x + width, y + height, 0, color1, color1, color1, color2, color2, color2, color1, color2, true, 0);
    }

    public void verticalGradientBoxEdges(double x, double y, double width, double height, Color color1, Color color2) {
        gradientBoxEdges(x, y, 0, x + width, y + height, 0, color1, color2, color1, color1, color1, color2, color2, color2, true, 0);
    }
    //why did i even make booleans to indicate if it should be vertical or not if it just breaks it even more
}