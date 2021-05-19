package dev.urmomia.utils.render.color;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.MainClient;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.gui.GuiThemes;
import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.settings.ColorSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.waypoints.Waypoint;
import dev.urmomia.systems.waypoints.Waypoints;
import dev.urmomia.utils.misc.UnorderedArrayList;

import java.util.List;

import static dev.urmomia.utils.Utils.mc;

public class RainbowColors {

    public static final RainbowColor GLOBAL = new RainbowColor();

    private static final List<Setting<SettingColor>> colorSettings = new UnorderedArrayList<>();
    private static final List<SettingColor> colors = new UnorderedArrayList<>();
    private static final List<Runnable> listeners = new UnorderedArrayList<>();

    public static void init() {
        MainClient.EVENT_BUS.subscribe(RainbowColors.class);
    }

    public static void addSetting(Setting<SettingColor> setting) {
        colorSettings.add(setting);
    }

    public static void removeSetting(Setting<SettingColor> setting) {
        colorSettings.remove(setting);
    }

    public static void add(SettingColor color) {
        colors.add(color);
    }

    public static void register(Runnable runnable) {
        listeners.add(runnable);
    }

    @EventHandler
    private static void onTick(TickEvent.Post event) {
        GLOBAL.getNext();

        for (Setting<SettingColor> setting : colorSettings) {
            if (setting.module == null || setting.module.isActive()) setting.get().update();
        }

        for (SettingColor color : colors) {
            color.update();
        }

        for (Waypoint waypoint : Waypoints.get()) {
            waypoint.color.update();
        }

        if (mc.currentScreen instanceof WidgetScreen) {
            for (SettingGroup group : GuiThemes.get().settings) {
                for (Setting<?> setting : group) {
                    if (setting instanceof ColorSetting) ((SettingColor) setting.get()).update();
                }
            }
        }

        for (Runnable listener : listeners) listener.run();
    }
}