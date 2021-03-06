/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.render;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.IntSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class CustomFOV extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> fov = sgGeneral.add(new IntSetting.Builder()
            .name("fOV") // not typo, just makes it show FOV instead of Fov moment.
            .description("Your custom FOV.")
            .defaultValue(120)
            .sliderMin(1)
            .sliderMax(179)
            .build()
    );

    private float _fov;

    public CustomFOV() {
        super(Categories.Render, "custom-fov", "Allows your FOV to be more customizable.");
    }

    @Override
    public void onActivate() {
        _fov = (float) mc.options.fov;
        mc.options.fov = fov.get();
    }

    public void getFOV() {
        mc.options.fov = fov.get();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (fov.get() != mc.options.fov) {
            getFOV();
        }
    }

    @Override
    public void onDeactivate() {
     mc.options.fov = _fov;
    }
}
