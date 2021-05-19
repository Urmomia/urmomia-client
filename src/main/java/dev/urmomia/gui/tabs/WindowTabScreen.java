package dev.urmomia.gui.tabs;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.utils.Cell;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.gui.widgets.containers.WWindow;

public class WindowTabScreen extends TabScreen {
    private final WWindow window;

    public WindowTabScreen(GuiTheme theme, Tab tab) {
        super(theme, tab);

        window = super.add(theme.window(tab.name)).right().widget();
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
