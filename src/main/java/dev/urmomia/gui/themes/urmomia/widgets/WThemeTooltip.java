package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.WTooltip;

public class WThemeTooltip extends WTooltip implements ThemeWidget {
    public WThemeTooltip(String text) {
        super(text);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.quad(this, theme().backgroundColor.get());
    }
}
