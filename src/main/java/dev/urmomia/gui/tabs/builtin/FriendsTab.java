package dev.urmomia.gui.tabs.builtin;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.tabs.Tab;
import dev.urmomia.gui.tabs.TabScreen;
import dev.urmomia.gui.tabs.WindowTabScreen;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.containers.WSection;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WDropdown;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WMinus;
import dev.urmomia.gui.widgets.pressable.WPlus;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.ColorSetting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.Settings;
import dev.urmomia.systems.config.Config;
import dev.urmomia.systems.friends.Friend;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.utils.entity.FriendType;
import dev.urmomia.utils.render.color.SettingColor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import static dev.urmomia.utils.Utils.mc;

public class FriendsTab extends Tab {
    public FriendsTab() {
        super("Friends");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new FriendsScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof FriendsScreen;
    }

    private static class FriendsScreen extends WindowTabScreen {
        private static final Identifier LOGO = new Identifier("urmomia-client", "textures/hud/client-logo.png");

        public FriendsScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            mc.getTextureManager().bindTexture(LOGO);
            WHorizontalList watermark = add(theme.horizontalList()).pad(4).top().widget();
            watermark.add(theme.texture(256 * 0.2, 256 * 0.2, 0, mc.getTextureManager().getTexture(LOGO)));
            watermark.add(theme.label(Config.version.getOriginalString()));

            Settings s = new Settings();

            SettingGroup sgEnemy = s.createGroup("Retards");
            SettingGroup sgNeutral = s.createGroup("Neutral");
            SettingGroup sgTrusted = s.createGroup("Trusted");

            // Enemies

            sgEnemy.add(new BoolSetting.Builder()
                    .name("show-in-tracers")
                    .description("Whether to show retards in tracers.")
                    .defaultValue(true)
                    .onChanged(aBoolean -> Friends.get().showEnemies = aBoolean)
                    .onModuleActivated(booleanSetting -> booleanSetting.set(Friends.get().showEnemies))
                    .build()
            );

            sgEnemy.add(new ColorSetting.Builder()
                    .name("color")
                    .description("The color used to show retards in ESP and Tracers.")
                    .defaultValue(new SettingColor(204, 0, 0))
                    .onChanged(Friends.get().enemyColor::set)
                    .onModuleActivated(colorSetting -> colorSetting.set(Friends.get().enemyColor))
                    .build()
            );

            // Neutral

            sgNeutral.add(new BoolSetting.Builder()
                    .name("show-in-tracers")
                    .description("Whether to show neutrals in tracers.")
                    .defaultValue(true)
                    .onChanged(aBoolean -> Friends.get().showNeutral = aBoolean)
                    .onModuleActivated(booleanSetting -> booleanSetting.set(Friends.get().showNeutral))
                    .build()
            );

            sgNeutral.add(new ColorSetting.Builder()
                    .name("color")
                    .description("The color used to show neutrals in ESP and Tracers.")
                    .defaultValue(new SettingColor(60, 240,240))
                    .onChanged(Friends.get().neutralColor::set)
                    .onModuleActivated(colorSetting -> colorSetting.set(Friends.get().neutralColor))
                    .build()
            );

            sgNeutral.add(new BoolSetting.Builder()
                    .name("attack")
                    .description("Whether to attack neutrals.")
                    .defaultValue(false)
                    .onChanged(aBoolean -> Friends.get().attackNeutral = aBoolean)
                    .onModuleActivated(booleanSetting -> booleanSetting.set(Friends.get().attackNeutral))
                    .build()
            );

            // Trusted

            sgTrusted.add(new BoolSetting.Builder()
                    .name("show-in-tracers")
                    .description("Whether to show trusted in tracers.")
                    .defaultValue(true)
                    .onChanged(aBoolean -> Friends.get().showTrusted = aBoolean)
                    .onModuleActivated(booleanSetting -> booleanSetting.set(Friends.get().showTrusted))
                    .build()
            );

            sgTrusted.add(new ColorSetting.Builder()
                    .name("color")
                    .description("The color used to show trusted in ESP and Tracers.")
                    .defaultValue(new SettingColor(57, 247, 47))
                    .onChanged(Friends.get().trustedColor::set)
                    .onModuleActivated(colorSetting -> colorSetting.set(Friends.get().trustedColor))
                    .build()
            );

            s.onActivated();
            add(theme.settings(s)).expandX();

            // Friends
            WSection friends = add(theme.section("Friends")).expandX().widget();
            WTable table = friends.add(theme.table()).expandX().widget();

            fillTable(table);

            // New
            WHorizontalList list = friends.add(theme.horizontalList()).expandX().widget();

            WTextBox nameW = list.add(theme.textBox("")).minWidth(400).expandX().widget();
            nameW.setFocused(true);

            WPlus add = list.add(theme.plus()).widget();
            add.action = () -> {
                String name = nameW.get().trim();

                if (Friends.get().add(new Friend(name))) {
                    nameW.set("");

                    table.clear();
                    fillTable(table);
                }
            };
        }

        private void fillTable(WTable table) {
            for (Friend friend : Friends.get()) {
                table.add(theme.label(friend.name));

                WDropdown<FriendType> type = table.add(theme.dropdown(friend.type)).widget();
                type.action = () -> friend.type = type.get();

                WMinus remove = table.add(theme.minus()).expandCellX().right().widget();
                remove.action = () -> {
                    Friends.get().remove(friend);

                    table.clear();
                    fillTable(table);
                };

                table.row();
            }
        }
    }
}
