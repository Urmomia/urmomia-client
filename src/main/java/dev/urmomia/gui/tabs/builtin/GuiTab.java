package dev.urmomia.gui.tabs.builtin;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.GuiThemes;
import dev.urmomia.gui.tabs.Tab;
import dev.urmomia.gui.tabs.TabScreen;
import dev.urmomia.gui.tabs.WindowTabScreen;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WDropdown;
import dev.urmomia.systems.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import static dev.urmomia.utils.Utils.mc;

public class GuiTab extends Tab {
    public GuiTab() {
        super("GUI");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new GuiScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof GuiScreen;
    }

    private static class GuiScreen extends WindowTabScreen {

        private static final Identifier LOGO = new Identifier("urmomia-client", "textures/hud/client-logo.png");

        public GuiScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            mc.getTextureManager().bindTexture(LOGO);
            WHorizontalList watermark = add(theme.horizontalList()).pad(4).top().widget();
            watermark.add(theme.texture(256 * 0.2, 256 * 0.2, 0, mc.getTextureManager().getTexture(LOGO)));
            watermark.add(theme.label(Config.version.getOriginalString()));

            WTable table = add(theme.table()).expandX().widget();

            table.add(theme.label("Theme:"));
            WDropdown<String> themeW = table.add(theme.dropdown(GuiThemes.getNames(), GuiThemes.get().name)).widget();
            themeW.action = () -> {
                GuiThemes.select(themeW.get());

                mc.openScreen(null);
                tab.openScreen(GuiThemes.get());
            };

            theme.settings.onActivated();
            add(theme.settings(theme.settings)).expandX();
        }
    }
}
