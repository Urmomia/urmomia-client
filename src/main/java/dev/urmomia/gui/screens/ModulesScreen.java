package dev.urmomia.gui.screens;

import dev.urmomia.systems.config.Config;
import dev.urmomia.MainClient;
import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.tabs.TabScreen;
import dev.urmomia.gui.tabs.Tabs;
import dev.urmomia.gui.utils.Cell;
import dev.urmomia.gui.widgets.containers.WContainer;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.containers.WSection;
import dev.urmomia.gui.widgets.containers.WVerticalList;
import dev.urmomia.gui.widgets.containers.WWindow;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.systems.modules.Category;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

import static dev.urmomia.utils.Utils.getWindowHeight;
import static dev.urmomia.utils.Utils.getWindowWidth;

public class ModulesScreen extends TabScreen {

    private static final Identifier LOGO = new Identifier("urmomia-client", "textures/hud/client-logo.png");
    private static final Identifier SEARCH = new Identifier("urmomia-client", "textures/icons/category/search.png");

    public ModulesScreen(GuiTheme theme) {
        super(theme, Tabs.get().get(0));

        add(createCategoryContainer());

        // Help
        WVerticalList help = add(theme.verticalList()).pad(4).bottom().right().widget();
        help.add(theme.label("Left click - Toggle module"));
        help.add(theme.label("Right click - Open module settings"));

        MainClient.mc.getTextureManager().bindTexture(LOGO);
        //don't forget to bindtexture later and spend 72 hours trying something else
        WHorizontalList watermark = add(theme.horizontalList()).pad(4).top().widget();
        watermark.add(theme.texture(256 * 0.2, 256 * 0.2, 0, MainClient.mc.getTextureManager().getTexture(LOGO)));
        watermark.add(theme.label(Config.version.getOriginalString()));

    }

    protected WCategoryController createCategoryContainer() {
        return new WCategoryController();
    }

    // Category

    protected void createCategory(WContainer c, Category category) {
        WWindow w = theme.window(category.name);
        w.id = category.name;
        w.padding = 0;
        w.spacing = 0;

        if (theme.categoryIcons()) {
            MainClient.mc.getTextureManager().bindTexture(category.icon);
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.texture(32, 32, 0, MainClient.mc.getTextureManager().getTexture(category.icon))).pad(2);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.spacing = 0;

        for (Module module : Modules.get().getGroup(category)) {
            w.add(theme.module(module)).expandX().widget().tooltip = module.description;
        }
    }

    // Search

    protected void createSearchW(WContainer w, String text) {
        if (!text.isEmpty()) {
            // Titles
            List<Pair<Module, Integer>> modules = Modules.get().searchTitles(text);

            if (modules.size() > 0) {
                WSection section = w.add(theme.section("Modules")).expandX().widget();
                section.spacing = 0;

                for (Pair<Module, Integer> pair : modules) {
                    section.add(theme.module(pair.getLeft())).expandX();
                }
            }

            // Settings
            modules = Modules.get().searchSettingTitles(text);

            if (modules.size() > 0) {
                WSection section = w.add(theme.section("Settings")).expandX().widget();
                section.spacing = 0;

                for (Pair<Module, Integer> pair : modules) {
                    section.add(theme.module(pair.getLeft())).expandX();
                }
            }
        }
    }

    protected void createSearch(WContainer c) {
        WWindow w = theme.window("Search");
        w.id = "search";

        if (theme.categoryIcons()) {
            MainClient.mc.getTextureManager().bindTexture(SEARCH);
            w.beforeHeaderInit = wContainer -> wContainer.add(theme.texture(32, 32, 0, MainClient.mc.getTextureManager().getTexture(SEARCH))).pad(2);
        }

        c.add(w);
        w.view.scrollOnlyWhenMouseOver = true;
        w.view.hasScrollBar = false;
        w.view.maxHeight -= 20;

        WVerticalList l = theme.verticalList();

        WTextBox text = w.add(theme.textBox("")).minWidth(140).expandX().widget();
        text.setFocused(true);
        text.action = () -> {
            l.clear();
            createSearchW(l, text.get());
        };

        w.add(l).expandX();
        createSearchW(l, text.get());
    }

    // Stuff

    protected class WCategoryController extends WContainer {
        @Override
        public void init() {
            for (Category category : Modules.loopCategories()) {
                createCategory(this, category);
            }

            createSearch(this);
        }

        @Override
        protected void onCalculateWidgetPositions() {
            double pad = theme.scale(4);
            double h = theme.scale(40);

            double x = this.x + pad;
            double y = this.y;

            for (Cell<?> cell : cells) {
                double windowWidth = getWindowWidth();
                double windowHeight = getWindowHeight();

                if (x + cell.width > windowWidth) {
                    x = x + pad;
                    y += h;
                }

                if (x > windowWidth) {
                    x = windowWidth / 2.0 - cell.width / 2.0;
                    if (x < 0) x = 0;
                }
                if (y > windowHeight) {
                    y = windowHeight / 2.0 - cell.height / 2.0;
                    if (y < 0) y = 0;
                }

                cell.x = x;
                cell.y = y;

                cell.width = cell.widget().width;
                cell.height = cell.widget().height;

                cell.alignWidget();

                x += cell.width + pad;
            }
        }
    }
}
