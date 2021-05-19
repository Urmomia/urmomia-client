package dev.urmomia.gui.tabs.builtin;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.GuiThemes;
import dev.urmomia.gui.tabs.Tab;
import dev.urmomia.gui.tabs.TabScreen;
import net.minecraft.client.gui.screen.Screen;

public class ModulesTab extends Tab {
    public ModulesTab() {
        super("Modules");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return theme.modulesScreen();
    }

    @Override
    public boolean isScreen(Screen screen) {
        return GuiThemes.get().isModulesScreen(screen);
    }
}
