/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules;

import net.minecraft.util.Identifier;

public class Categories {
    public static final Category Combat = new Category("Combat", new Identifier("urmomia-client", "textures/icons/category/combat.png"));
    public static final Category Player = new Category("Player", new Identifier("urmomia-client", "textures/icons/category/player.png"));
    public static final Category Movement = new Category("Movement", new Identifier("urmomia-client", "textures/icons/category/movement.png"));
    public static final Category Render = new Category("Render", new Identifier("urmomia-client", "textures/icons/category/render.png"));
    public static final Category World = new Category("World", new Identifier("urmomia-client", "textures/icons/category/world.png"));
    public static final Category Misc = new Category("Misc", new Identifier("urmomia-client", "textures/icons/category/misc.png"));

    public static void register() {
        Modules.registerCategory(Combat);
        Modules.registerCategory(Player);
        Modules.registerCategory(Movement);
        Modules.registerCategory(Render);
        Modules.registerCategory(World);
        Modules.registerCategory(Misc);
    }
}
