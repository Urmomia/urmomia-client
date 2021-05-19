/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.movement;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.meteor.KeyEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.render.Freecam;
import dev.urmomia.utils.misc.input.KeyAction;

public class AirJump extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> maintainY = sgGeneral.add(new BoolSetting.Builder()
            .name("maintain-level")
            .description("Maintains your current Y level.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> onHold = sgGeneral.add(new BoolSetting.Builder()
            .name("on-hold")
            .description("Whether or not to air jump if you hold down the space bar.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> onGround = sgGeneral.add(new BoolSetting.Builder()
            .name("on-ground")
            .description("Whether to airjump if you are on the ground.")
            .defaultValue(false)
            .build()
    );

    public AirJump() {
        super(Categories.Movement, "air-jump", "Lets you jump in the air.");
    }

    private int level = 0;

    @EventHandler
    private void onKey(KeyEvent event) {
        if (Modules.get().isActive(Freecam.class) || mc.currentScreen != null || (!onGround.get() && mc.player.isOnGround())) return;
        if ((event.action == KeyAction.Press || (event.action == KeyAction.Repeat && onHold.get())) && mc.options.keyJump.matchesKey(event.key, 0)) {
            mc.player.jump();
            level = mc.player.getBlockPos().getY();
        }
        if ((event.action == KeyAction.Press || (event.action == KeyAction.Repeat && onHold.get())) && mc.options.keySneak.matchesKey(event.key, 0)){
            level -= 1;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (Modules.get().isActive(Freecam.class) || (!onGround.get() && mc.player.isOnGround())) return;
        if (maintainY.get() && mc.player.getBlockPos().getY() == level){
            mc.player.jump();
        }
    }
}
