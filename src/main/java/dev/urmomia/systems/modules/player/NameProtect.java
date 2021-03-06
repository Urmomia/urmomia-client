package dev.urmomia.systems.modules.player;

import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.StringSetting;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class NameProtect extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
            .name("name")
            .description("Name to be replaced with.")
            .defaultValue("noobmaster69")
            .build()
    );

    private String username = "If you see this, something is wrong.";

    public NameProtect() {
        super(Categories.Player, "name-protect", "Hides your name client-side.");
    }

    @Override
    public void onActivate() {
        username = mc.getSession().getUsername();
    }

    public String replaceName(String string) {
        if (string.contains(username) && name.get().length() > 0 && isActive()) {
            return string.replace(username, name.get());
        } else return string;
    }

    public String getName(String original) {
        if (name.get().length() > 0 && isActive()) return name.get();
        else return original;
    }
}