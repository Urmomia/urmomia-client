/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.world;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.EnumSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.entity.Target;
import dev.urmomia.utils.player.Rotations;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.Vec3d;

public class EndermanLook extends Module {
    public enum Mode{
        LookAt,
        LookAway
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> lookMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("look-mode")
            .description("How this module behaves.")
            .defaultValue(Mode.LookAway)
            .build()
    );

    public EndermanLook() {
        super(Categories.World, "enderman-look", "Either looks at all Endermen or prevents you from looking at Endermen.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (lookMode.get() == Mode.LookAway) {
            if (mc.player.abilities.creativeMode || !shouldLook()) return;

            Rotations.rotate(mc.player.yaw, 90, -75, null);
        }
        else {
            for (Entity entity : mc.world.getEntities()) {
                if (!(entity instanceof EndermanEntity)) continue;
                EndermanEntity enderman = (EndermanEntity) entity;

                if (enderman.isAngry() || !enderman.isAlive() || !mc.player.canSee(enderman)) continue;

                Rotations.rotate(Rotations.getYaw(enderman), Rotations.getPitch(enderman, Target.Head), -75, null);
                break;
            }
        }
    }

    private boolean shouldLook() {
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EndermanEntity)) continue;

            if (entity.isAlive() && angleCheck(entity)) return true;
        }

        return false;
    }

    private boolean angleCheck(Entity entity) {
        Vec3d vec3d = mc.player.getRotationVec(1.0F).normalize();
        Vec3d vec3d2 = new Vec3d(entity.getX() - mc.player.getX(), entity.getEyeY() - mc.player.getEyeY(), entity.getZ() - mc.player.getZ());

        double d = vec3d2.length();
        vec3d2 = vec3d2.normalize();
        double e = vec3d.dotProduct(vec3d2);

        return e > 1.0D - 0.025D / d && mc.player.canSee(entity);
    }
}
