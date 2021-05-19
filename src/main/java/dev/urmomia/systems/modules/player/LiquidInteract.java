/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.player;

import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class LiquidInteract extends Module {
    public LiquidInteract() {
        super(Categories.Player, "liquid-interact", "Allows you to interact with liquids.");
    }
}
