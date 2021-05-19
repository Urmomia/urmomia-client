/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.events.meteor;

import dev.urmomia.systems.modules.Module;

public class ModuleVisibilityChangedEvent {
    private static final ModuleVisibilityChangedEvent INSTANCE = new ModuleVisibilityChangedEvent();

    public Module module;

    public static ModuleVisibilityChangedEvent get(Module module) {
        INSTANCE.module = module;
        return INSTANCE;
    }
}
