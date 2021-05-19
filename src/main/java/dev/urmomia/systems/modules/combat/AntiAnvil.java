/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */
package dev.urmomia.systems.modules.combat;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AntiAnvil extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Forces you to rotate upwards when placing obsidian above you.")
            .defaultValue(true)
            .build()
    );

    public AntiAnvil(){
        super(Categories.Combat, "anti-anvil", "Automatically prevents Auto Anvil by placing obsidian above you.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        for (int i = 2; i <= mc.interactionManager.getReachDistance() + 2; i++){
            if (mc.world.getBlockState(mc.player.getBlockPos().add(0, i, 0)).getBlock() == Blocks.ANVIL && mc.world.getBlockState(mc.player.getBlockPos().add(0, i - 1, 0)).isAir()){
                int slot = InvUtils.findItemWithCount(Items.OBSIDIAN).slot;
                boolean stop = false;

                if (slot != 1 && slot < 9) {
                    place(i, slot);
                    stop = true;
                }
                else if (mc.player.getOffHandStack().getItem() == Items.OBSIDIAN){
                    place(i, -1);
                    stop = true;
                }

                if (stop) break;
            }
        }
    }

    private void place(int i, int slot) {
        BlockUtils.place(mc.player.getBlockPos().add(0, i - 2, 0), Hand.MAIN_HAND, slot == -1 ? 0 : slot, rotate.get(), 15, true, true, slot != -1, slot != -1);
    }
}
