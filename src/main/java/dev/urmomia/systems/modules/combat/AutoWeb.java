/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.combat;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.entity.FakePlayerEntity;
import dev.urmomia.utils.entity.FakePlayerUtils;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.world.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class AutoWeb extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
            .name("range")
            .description("The maximum distance to be able to place webs.")
            .defaultValue(4)
            .min(0)
            .build()
    );

    private final Setting<Boolean> doubles = sgGeneral.add(new BoolSetting.Builder()
            .name("doubles")
            .description("Places webs in the target's upper hitbox as well as the lower hitbox.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates towards the webs when placing.")
            .defaultValue(true)
            .build()
    );

    public AutoWeb() {
        super(Categories.Combat, "auto-web", "Automatically places webs on other players.");
    }

    private PlayerEntity target = null;

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        int slot = InvUtils.findItemInHotbar(Items.COBWEB);
        if (slot == -1) return;

        if (target != null) {
            if (mc.player.distanceTo(target) > range.get() || !target.isAlive()) target = null;
        }

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !Friends.get().attack(player) || !player.isAlive() || mc.player.distanceTo(player) > range.get()) continue;

            if (target == null) {
                target = player;
            } else if (mc.player.distanceTo(target) > mc.player.distanceTo(player)) {
                target = player;
            }
        }

        if (target == null) {
            for (FakePlayerEntity fakeTarget : FakePlayerUtils.getPlayers().keySet()) {
                if (fakeTarget.getHealth() <= 0 || !Friends.get().attack(fakeTarget) || !fakeTarget.isAlive()) continue;

                if (target == null) {
                    target = fakeTarget;
                    continue;
                }

                if (mc.player.distanceTo(fakeTarget) < mc.player.distanceTo(target)) target = fakeTarget;
            }
        }

        if (target != null) {
            BlockPos targetPos = target.getBlockPos();
            BlockUtils.place(targetPos, Hand.MAIN_HAND, slot, rotate.get(), 0, false);

            if (doubles.get()) {
                targetPos = targetPos.add(0, 1, 0);
                BlockUtils.place(targetPos, Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.COBWEB), rotate.get(), 0, false);
            }
        }
    }
}