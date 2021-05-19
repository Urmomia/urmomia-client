/*

 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui;

import dev.urmomia.gui.utils.Cell;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.gui.widgets.containers.WWindow;

public abstract class WindowScreen extends WidgetScreen {
    private final WWindow window;

    public WindowScreen(GuiTheme theme, String title) {
        super(theme, title);

        window = super.add(theme.window(title)).center().widget();
    }

    @Override
    public <W extends WWidget> Cell<W> add(W widget) {
        return window.add(widget);
    }

    @Override
    public void clear() {
        window.clear();
    }
}
