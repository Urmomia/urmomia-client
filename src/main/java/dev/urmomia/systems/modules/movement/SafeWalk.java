/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.movement;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.entity.player.ClipAtLedgeEvent;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class SafeWalk extends Module {
    public SafeWalk() {
        super(Categories.Movement, "safe-walk", "Prevents you from walking off blocks. Useful over a void.");
    }

    @EventHandler
    private void onClipAtLedge(ClipAtLedgeEvent event) {
        event.setClip(true);
    }
}
