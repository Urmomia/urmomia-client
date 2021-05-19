package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.WTopBar;
import dev.urmomia.utils.render.color.Color;

public class WThemeTopBar extends WTopBar implements ThemeWidget {
    @Override
    protected Color getButtonColor(boolean pressed, boolean hovered) {
        return theme().topbarColor.get(pressed, hovered);
    }

    @Override
    protected Color getNameColor() {
        return theme().textColor.get();
    }
}
