package dev.urmomia.gui.tabs.builtin;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.tabs.Tab;
import dev.urmomia.gui.tabs.TabScreen;
import dev.urmomia.gui.tabs.WindowTabScreen;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.gui.widgets.pressable.WCheckbox;
import dev.urmomia.gui.widgets.pressable.WMinus;
import dev.urmomia.gui.widgets.pressable.WPlus;
import dev.urmomia.systems.accounts.Accounts;
import dev.urmomia.systems.config.Config;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.macros.Macros;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.profiles.Profile;
import dev.urmomia.systems.profiles.Profiles;
import dev.urmomia.systems.waypoints.Waypoints;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import static dev.urmomia.utils.Utils.mc;

public class ProfilesTab extends Tab {

    public ProfilesTab() {
        super("Profiles");
    }

    @Override
    protected TabScreen createScreen(GuiTheme theme) {
        return new ProfilesScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof ProfilesScreen;
    }

    private static class ProfilesScreen extends WindowTabScreen {

        private static final Identifier LOGO = new Identifier("urmomia-client", "textures/hud/client-logo.png");

        public ProfilesScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            mc.getTextureManager().bindTexture(LOGO);
            WHorizontalList watermark = add(theme.horizontalList()).pad(4).top().widget();
            watermark.add(theme.texture(256 * 0.2, 256 * 0.2, 0, mc.getTextureManager().getTexture(LOGO)));
            watermark.add(theme.label(Config.version.getOriginalString()));
    
        }

        @Override
        protected void init() {
            super.init();

            initWidget();
        }

        private void initWidget() {
            clear();

            WTable table = add(theme.table()).expandX().minWidth(300).widget();

            // Waypoints
            for (Profile profile : Profiles.get()) {
                // Name
                table.add(theme.label(profile.name)).expandCellX();

                // Save
                WButton save = table.add(theme.button("Save")).widget();
                save.action = profile::save;

                // Load
                WButton load = table.add(theme.button("Load")).widget();
                load.action = profile::load;

                // Edit
                WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
                edit.action = () -> mc.openScreen(new EditProfileScreen(theme, profile, this::initWidget));

                // Remove
                WMinus remove = table.add(theme.minus()).widget();
                remove.action = () -> {
                    Profiles.get().remove(profile);
                    initWidget();
                };

                table.row();
            }

            table.add(theme.horizontalSeparator()).expandX();
            table.row();

            // Create
            WButton create = table.add(theme.button("Create")).expandX().widget();
            create.action = () -> mc.openScreen(new EditProfileScreen(theme, null, this::initWidget));
        }
    }

    private static class EditProfileScreen extends WindowScreen {
        private final Profile profile;
        private final boolean newProfile;
        private final Runnable action;

        public EditProfileScreen(GuiTheme theme, Profile profile, Runnable action) {
            super(theme, profile == null ? "New Profile" : "Edit Profile");

            this.newProfile = profile == null;
            this.profile = newProfile ? new Profile() : profile;
            this.action = action;

            initWidgets();
        }

        public void initWidgets() {
            WTable table = add(theme.table()).expandX().widget();

            // Name
            table.add(theme.label("Name:"));
            WTextBox name = table.add(theme.textBox(newProfile ? "" : profile.name)).minWidth(400).expandX().widget();
            name.action = () -> profile.name = name.get().trim().replaceAll("/", "-");
            table.row();

            table.add(theme.horizontalSeparator()).expandX();
            table.row();

            // On Launch
            table.add(theme.label("Load on Launch:"));
            WCheckbox onLaunch = table.add(theme.checkbox(profile.onLaunch)).widget();
            onLaunch.action = () -> profile.onLaunch = onLaunch.checked;
            table.row();

            // On Server Join
            table.add(theme.label("Load when Joining:"));
            WTable ips = table.add(theme.table()).widget();
            fillTable(ips);
            table.row();

            table.add(theme.horizontalSeparator()).expandX();
            table.row();

            // Accounts
            table.add(theme.label("Accounts:"));
            WCheckbox accounts = table.add(theme.checkbox(profile.accounts)).widget();
            accounts.action = () -> {
                profile.accounts = accounts.checked;

                if (newProfile) return;
                if (profile.accounts) profile.save(Accounts.get());
                else profile.delete(Accounts.get());
            };
            table.row();

            // Config
            table.add(theme.label("Config:"));
            WCheckbox config = table.add(theme.checkbox(profile.config)).widget();
            config.action = () -> {
                profile.config = config.checked;

                if (newProfile) return;
                if (profile.config) profile.save(Config.get());
                else profile.delete(Config.get());
            };
            table.row();

            // Friends
            table.add(theme.label("Friends:"));
            WCheckbox friends = table.add(theme.checkbox(profile.friends)).widget();
            friends.action = () -> {
                profile.friends = friends.checked;

                if (newProfile) return;
                if (profile.friends) profile.save(Friends.get());
                else profile.delete(Friends.get());
            };
            table.row();

            // Macros
            table.add(theme.label("Macros:"));
            WCheckbox macros = table.add(theme.checkbox(profile.macros)).widget();
            macros.action = () -> {
                profile.macros = macros.checked;

                if (newProfile) return;
                if (profile.macros) profile.save(Macros.get());
                else profile.delete(Macros.get());
            };
            table.row();

            // Modules
            table.add(theme.label("Modules:"));
            WCheckbox modules = table.add(theme.checkbox(profile.modules)).widget();
            modules.action = () -> {
                profile.modules = modules.checked;

                if (newProfile) return;
                if (profile.modules) profile.save(Modules.get());
                else profile.delete(Modules.get());
            };
            table.row();

            // Waypoints
            table.add(theme.label("Waypoints:"));
            WCheckbox waypoints = table.add(theme.checkbox(profile.waypoints)).widget();
            waypoints.action = () -> {
                profile.waypoints = waypoints.checked;

                if (newProfile) return;
                if (profile.waypoints) profile.save(Waypoints.get());
                else profile.delete(Waypoints.get());
            };
            table.row();

            table.add(theme.horizontalSeparator()).expandX();
            table.row();

            // Save
            table.add(theme.button("Save")).expandX().widget().action = () -> {
                if (profile.name == null || profile.name.isEmpty()) return;

                for (Profile p : Profiles.get()) {
                    if (profile == p) continue;
                    if (profile.name.equalsIgnoreCase(p.name)) return;
                }

                if (newProfile) {
                    Profiles.get().add(profile);
                } else {
                    Profiles.get().save();
                }

                onClose();
            };
        }

        private void fillTable(WTable table) {
            if (profile.loadOnJoinIps.isEmpty()) profile.loadOnJoinIps.add("");

            for (int i = 0; i < profile.loadOnJoinIps.size(); i++) {
                int ii = i;

                WTextBox line = table.add(theme.textBox(profile.loadOnJoinIps.get(ii))).minWidth(400).expandX().widget();
                line.action = () -> {
                    String ip = line.get().trim();
                    if (StringUtils.containsWhitespace(ip) || !ip.contains(".")) return;

                    profile.loadOnJoinIps.set(ii, ip);
                };

                if (ii != profile.loadOnJoinIps.size() - 1) {
                    WMinus remove = table.add(theme.minus()).widget();
                    remove.action = () -> {
                        profile.loadOnJoinIps.remove(ii);

                        clear();
                        initWidgets();
                    };
                } else {
                    WPlus add = table.add(theme.plus()).widget();
                    add.action = () -> {
                        profile.loadOnJoinIps.add("");

                        clear();
                        initWidgets();
                    };
                }

                table.row();
            }
        }

        @Override
        protected void onClosed() {
            if (action != null) action.run();
        }
    }
}
