package dev.urmomia.gui.themes.urmomia.widgets.pressable;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.renderer.packer.GuiTexture;
import dev.urmomia.gui.themes.urmomia.ThemeGuiTheme;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.pressable.WButton;

public class WThemeButton extends WButton implements ThemeWidget {
    public WThemeButton(String text, GuiTexture texture) {
        super(text, texture);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        ThemeGuiTheme theme = theme();
        double pad = pad();

        renderBackground(renderer, this, pressed, mouseOver);

        if (text != null) {
            renderer.text(text, x + width / 2 - textWidth / 2, y + pad, theme.textColor.get(), false);
        }
        else {
            double ts = theme.textHeight();
            renderer.quad(x + width / 2 - ts / 2, y + pad, ts, ts, texture, theme.textColor.get());
        }
    }
}
