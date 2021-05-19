package dev.urmomia.gui.tabs;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.gui.utils.Cell;
import dev.urmomia.gui.widgets.WWidget;

public class TabScreen extends WidgetScreen {
    public final Tab tab;

    public TabScreen(GuiTheme theme, Tab tab) {
        super(theme, tab.name);
        this.tab = tab;
    }

    public <T extends WWidget> Cell<T> addDirect(T widget) {
        return super.add(widget);
    }
}
