package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.widgets.WQuad;
import dev.urmomia.utils.render.color.Color;

public class WThemeQuad extends WQuad {
    public WThemeQuad(Color color) {
        super(color);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(x, y, width, height, color);
    }
}
