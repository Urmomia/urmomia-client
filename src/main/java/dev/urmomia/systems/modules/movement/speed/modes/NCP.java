/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.movement.speed.modes;

import dev.urmomia.events.entity.player.PlayerMoveEvent;
import dev.urmomia.mixininterface.IVec3d;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.movement.Anchor;
import dev.urmomia.systems.modules.movement.speed.SpeedMode;
import dev.urmomia.systems.modules.movement.speed.SpeedModes;
import dev.urmomia.utils.misc.Vector2;
import dev.urmomia.utils.player.PlayerUtils;

public class NCP extends SpeedMode {

    public NCP() {
        super(SpeedModes.NCP);
    }

    private long timer = 0L;

    @Override
    public void onMove(PlayerMoveEvent event) {
        switch (stage) {
            case 0: //Reset
                if (PlayerUtils.isMoving()) {
                    stage++;
                    speed = 1.18f * getDefaultSpeed() - 0.01;
                }
            case 1: //Jump
                if (!PlayerUtils.isMoving() || !mc.player.isOnGround()) break;

                ((IVec3d) event.movement).setY(getHop(0.40123128));
                speed *= settings.ncpSpeed.get();
                stage++;
                break;
            case 2: speed = distance - 0.76 * (distance - getDefaultSpeed()); stage++; break; //Slowdown after jump
            case 3: //Reset on collision or predict and update speed
                if (!mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0)) || mc.player.verticalCollision && stage > 0) {
                    stage = 0;
                }
                speed = distance - (distance / 159.0);
                break;
        }

        speed = Math.max(speed, getDefaultSpeed());

        if (settings.ncpSpeedLimit.get()) {
            if (System.currentTimeMillis() - timer > 2500L) {
                timer = System.currentTimeMillis();
            }

            speed = Math.min(speed, System.currentTimeMillis() - timer > 1250L ? 0.44D : 0.43D);
        }

        Vector2 change = PlayerUtils.transformStrafe(speed);

        double velX = change.x;
        double velZ = change.y;

        Anchor anchor = Modules.get().get(Anchor.class);
        if (anchor.isActive() && anchor.controlMovement) {
            velX = anchor.deltaX;
            velZ = anchor.deltaZ;
        }

        ((IVec3d) event.movement).setXZ(velX, velZ);
    }

    @Override
    public void onTick() {
        distance = Math.sqrt((mc.player.getX() - mc.player.prevX) * (mc.player.getX() - mc.player.prevX) + (mc.player.getZ() - mc.player.prevZ) * (mc.player.getZ() - mc.player.prevZ));
    }
}
