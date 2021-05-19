/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.movement;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.mixininterface.IVec3d;
import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.EnumSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class AutoJump extends Module {
    public enum JumpWhen {
        Sprinting,
        Walking,
        Always
    }

    public enum Mode {
        Jump,
        LowHop
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("The method of jumping.")
            .defaultValue(Mode.Jump)
            .build()
    );

    private final Setting<JumpWhen> jumpIf = sgGeneral.add(new EnumSetting.Builder<JumpWhen>()
            .name("jump-if")
            .description("Jump if.")
            .defaultValue(JumpWhen.Always)
            .build()
    );

    private final Setting<Double> velocityHeight = sgGeneral.add(new DoubleSetting.Builder()
            .name("velocity-height")
            .description("The distance that velocity mode moves you.")
            .defaultValue(0.25)
            .min(0)
            .sliderMax(2)
            .build()
    );

    public AutoJump() {
        super(Categories.Movement, "auto-jump", "Automatically jumps.");
    }

    private boolean jump() {
        switch (jumpIf.get()) {
            case Sprinting: return mc.player.isSprinting() && (mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0);
            case Walking:   return mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0;
            case Always:    return true;
            default:        return false;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!mc.player.isOnGround() || mc.player.isSneaking() || !jump()) return;

        if (mode.get() == Mode.Jump) mc.player.jump();
        else ((IVec3d) mc.player.getVelocity()).setY(velocityHeight.get());
    }
}
