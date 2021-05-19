/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.movement.speed.modes;

import dev.urmomia.events.entity.player.PlayerMoveEvent;
import dev.urmomia.mixininterface.IVec3d;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.movement.Anchor;
import dev.urmomia.systems.modules.movement.AutoJump;
import dev.urmomia.systems.modules.movement.speed.SpeedMode;
import dev.urmomia.systems.modules.movement.speed.SpeedModes;
import dev.urmomia.utils.player.PlayerUtils;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;

public class Vanilla extends SpeedMode {
    public Vanilla() {
        super(SpeedModes.Vanilla);
    }

    @Override
    public void onMove(PlayerMoveEvent event) {
        Vec3d vel = PlayerUtils.getHorizontalVelocity(settings.speed.get());
        double velX = vel.getX();
        double velZ = vel.getZ();

        if (settings.applySpeedPotions.get() && mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            double value = (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1) * 0.205;
            velX += velX * value;
            velZ += velZ * value;
        }

        Anchor anchor = Modules.get().get(Anchor.class);
        if (anchor.isActive() && anchor.controlMovement) {
            velX = anchor.deltaX;
            velZ = anchor.deltaZ;
        }

        ((IVec3d) event.movement).set(velX, event.movement.y, velZ);
    }

    @Override
    public void onTick() {
        if (settings.jump.get()) {
            if (!mc.player.isOnGround() || mc.player.isSneaking() || !jump()) return;
            if (settings.jumpMode.get() == AutoJump.Mode.Jump) mc.player.jump();
            else ((IVec3d) mc.player.getVelocity()).setY(settings.hopHeight.get());
        }
    }

    private boolean jump() {
        switch (settings.jumpIf.get()) {
            case Sprinting: return PlayerUtils.isSprinting();
            case Walking:   return PlayerUtils.isMoving();
            case Always:    return true;
            default:        return false;
        }
    }
}