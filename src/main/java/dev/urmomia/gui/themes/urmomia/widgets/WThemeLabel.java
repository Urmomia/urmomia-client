package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.WLabel;

public class WThemeLabel extends WLabel implements ThemeWidget {
    public WThemeLabel(String text, boolean title) {
        super(text, title);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (!text.isEmpty()) {
            renderer.text(text, x, y, color != null ? color : (title ? theme().titleTextColor.get() : theme().textColor.get()), title);
        }
    }
}
