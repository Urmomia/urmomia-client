package dev.urmomia.systems.modules;

import java.util.Objects;

import dev.urmomia.MainClient;
import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.settings.Settings;
import dev.urmomia.systems.config.Config;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.misc.ISerializable;
import dev.urmomia.utils.misc.Keybind;
import dev.urmomia.utils.player.ChatUtils;
import dev.urmomia.utils.render.color.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class Module implements ISerializable<Module> {
    protected final MinecraftClient mc;

    public final Category category;
    public final String name;
    public final String title;
    public final String description;
    public final Color color;

    public final Settings settings = new Settings();

    private boolean active;
    private boolean visible = true;

    public boolean serialize = true;

    public final Keybind keybind = Keybind.fromKey(-1);
    public boolean toggleOnBindRelease = false;

    public Module(Category category, String name, String description) {
        this.mc = MinecraftClient.getInstance();
        this.category = category;
        this.name = name;
        this.title = Utils.nameToTitle(name);
        this.description = description;
        this.color = Color.fromHsv(Utils.random(0.0, 360.0), 0.35, 1);
    }

    public WWidget getWidget(GuiTheme theme) {
        return null;
    }

    public void onActivate() {}
    public void onDeactivate() {}

    public void toggle(boolean onToggle) {
        if (!active) {
            active = true;
            Modules.get().addActive(this);

            settings.onActivated();

            if (onToggle) {
                MainClient.EVENT_BUS.subscribe(this);
                onActivate();
            }
        }
        else {
            if (onToggle) {
                MainClient.EVENT_BUS.unsubscribe(this);
                onDeactivate();
            }

            active = false;
            Modules.get().removeActive(this);
        }
    }

    public void toggle() {
        toggle(true);
    }

    public void sendToggledMsg() {
        if (Config.get().chatCommandsInfo) ChatUtils.sendMsg(this.hashCode(), Formatting.GRAY, "Toggled (highlight)%s(default) %s(default).", title, isActive() ? Formatting.GREEN + "on" : Formatting.RED + "off");
    }

    public void info(Text message) {
        ChatUtils.sendMsg(title, message);
    }

    public void info(String message, Object... args) {
        ChatUtils.info(title, message, args);
    }

    public void warning(String message, Object... args) {
        ChatUtils.warning(title, message, args);
    }

    public void error(String message, Object... args) {
        ChatUtils.error(title, message, args);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isActive() {
        return active;
    }

    public String getInfoString() {
        return null;
    }

    @Override
    public CompoundTag toTag() {
        if (!serialize) return null;
        CompoundTag tag = new CompoundTag();

        tag.putString("name", name);
        tag.put("keybind", keybind.toTag());
        tag.putBoolean("toggleOnKeyRelease", toggleOnBindRelease);
        tag.put("settings", settings.toTag());

        tag.putBoolean("active", active);
        tag.putBoolean("visible", visible);

        return tag;
    }

    @Override
    public Module fromTag(CompoundTag tag) {
        // General
        if (tag.contains("key")) keybind.set(true, tag.getInt("key"));
        else keybind.fromTag(tag.getCompound("keybind"));

        toggleOnBindRelease = tag.getBoolean("toggleOnKeyRelease");

        // Settings
        Tag settingsTag = tag.get("settings");
        if (settingsTag instanceof CompoundTag) settings.fromTag((CompoundTag) settingsTag);

        boolean active = tag.getBoolean("active");
        if (active != isActive()) toggle(Utils.canUpdate());
        setVisible(tag.getBoolean("visible"));

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return Objects.equals(name, module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}