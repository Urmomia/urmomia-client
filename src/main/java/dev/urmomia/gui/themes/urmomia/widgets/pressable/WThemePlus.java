package dev.urmomia.gui.themes.urmomia.widgets.pressable;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.themes.urmomia.ThemeGuiTheme;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.pressable.WPlus;

public class WThemePlus extends WPlus implements ThemeWidget {
    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        ThemeGuiTheme theme = theme();
        double pad = pad();
        double s = theme.scale(3);

        renderBackground(renderer, this, pressed, mouseOver);
        renderer.quad(x + pad, y + height / 2 - s / 2, width - pad * 2, s, theme.plusColor.get());
        renderer.quad(x + width / 2 - s / 2, y + pad, s, height - pad * 2, theme.plusColor.get());
    }
}
