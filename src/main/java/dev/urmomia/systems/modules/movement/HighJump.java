/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.movement;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.entity.player.JumpVelocityMultiplierEvent;
import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class HighJump extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
            .name("jump-multiplier")
            .description("Jump height multiplier.")
            .defaultValue(1)
            .min(0)
            .build()
    );

    public HighJump() {
        super(Categories.Movement, "high-jump", "Makes you jump higher than normal.");
    }

    @EventHandler
    private void onJumpVelocityMultiplier(JumpVelocityMultiplierEvent event) {
        event.multiplier *= multiplier.get();
    }
}
