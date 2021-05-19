package dev.urmomia.gui.themes.urmomia.widgets.pressable;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.themes.urmomia.ThemeGuiTheme;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.pressable.WCheckbox;
import dev.urmomia.utils.Utils;

public class WThemeCheckbox extends WCheckbox implements ThemeWidget {
    private double animProgress;

    public WThemeCheckbox(boolean checked) {
        super(checked);
        animProgress = checked ? 1 : 0;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        ThemeGuiTheme theme = theme();

        animProgress += (checked ? 1 : -1) * delta * 14;
        animProgress = Utils.clamp(animProgress, 0, 1);

        renderBackground(renderer, this, pressed, mouseOver);

        if (animProgress > 0) {
            double cs = (width - theme.scale(2)) / 1.75 * animProgress;
            renderer.quad(x + (width - cs) / 2, y + (height - cs) / 2, cs, cs, theme.checkboxColor.get());
        }
    }
}
