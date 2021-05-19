package dev.urmomia.gui.themes.urmomia;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.utils.BaseWidget;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.utils.render.color.Color;

public interface ThemeWidget extends BaseWidget {
    default ThemeGuiTheme theme() {
        return (ThemeGuiTheme) getTheme();
    }

    default void renderBackground(GuiRenderer renderer, WWidget widget, boolean pressed, boolean mouseOver) {
        ThemeGuiTheme theme = theme();
        double s = theme.scale(2);

        renderer.quad(widget.x + s, widget.y + s, widget.width - s * 2, widget.height - s * 2, theme.backgroundColor.get(pressed, mouseOver));

        Color outlineColor = theme.outlineColor.get(pressed, mouseOver);
        renderer.quad(widget.x, widget.y, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + widget.height - s, widget.width, s, outlineColor);
        renderer.quad(widget.x, widget.y + s, s, widget.height - s * 2, outlineColor);
        renderer.quad(widget.x + widget.width - s, widget.y + s, s, widget.height - s * 2, outlineColor);
    }
}
