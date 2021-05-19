/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.player;

import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class Reach extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<Double> reach = sg.add(new DoubleSetting.Builder()
            .name("reach")
            .description("Your reach modifier.")
            .defaultValue(5)
            .min(0)
            .sliderMax(6)
            .build()
    );

    public Reach() {
        super(Categories.Player, "reach", "Gives you super long arms.");
    }

    public float getReach() {
        return reach.get().floatValue();
    }
}
