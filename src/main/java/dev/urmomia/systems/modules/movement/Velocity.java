package dev.urmomia.systems.modules.movement;

import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class Velocity extends Module {
    private final SettingGroup sgDefault = settings.getDefaultGroup();

    public final Setting<Boolean> entities = sgDefault.add(new BoolSetting.Builder()
            .name("entities")
            .description("Modifies the amount of knockback you take from entities and attacks.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> explosions = sgDefault.add(new BoolSetting.Builder()
            .name("explosions")
            .description("Modifies your knockback from explosions.")
            .defaultValue(true)
            .build()
    );

    public final Setting<Boolean> liquids = sgDefault.add(new BoolSetting.Builder()
            .name("liquids")
            .description("Modifies the amount you are pushed by flowing liquids.")
            .defaultValue(false)
            .build()
    );

    public final Setting<Boolean> noPush = sgDefault.add(new BoolSetting.Builder()
            .name("no-push")
            .description("Attempts to stop getting pushed out of blocks.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Double> horizontal = sgDefault.add(new DoubleSetting.Builder()
            .name("horizontal-multiplier")
            .description("How much velocity you will take horizontally.")
            .defaultValue(0)
            .sliderMin(0)
            .sliderMax(1)
            .build()
    );

    private final Setting<Double> vertical = sgDefault.add(new DoubleSetting.Builder()
            .name("vertical-multiplier")
            .description("How much velocity you will take vertically.")
            .defaultValue(0)
            .sliderMin(0)
            .sliderMax(1)
            .build()
    );

    public Velocity() {
        super(Categories.Movement, "velocity", "Prevents you from being moved by external forces.");
    }

    public double getHorizontal() {
        return isActive() ? horizontal.get() : 1;
    }

    public double getVertical() {
        return isActive() ? vertical.get() : 1;
    }
}