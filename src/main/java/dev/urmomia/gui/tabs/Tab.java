package dev.urmomia.gui.tabs;

import dev.urmomia.gui.GuiTheme;
import net.minecraft.client.gui.screen.Screen;

import static dev.urmomia.utils.Utils.mc;

public abstract class Tab {
    public final String name;

    public Tab(String name) {
        this.name = name;
    }

    public void openScreen(GuiTheme theme) {
        TabScreen screen = this.createScreen(theme);
        screen.addDirect(theme.topBar()).top().right();

        mc.openScreen(screen);
    }

    protected abstract TabScreen createScreen(GuiTheme theme);

    public abstract boolean isScreen(Screen screen);
}
