/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.render;

import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class CameraClip extends Module {
    public CameraClip() {
        super(Categories.Render, "camera-clip", "Allows your third person camera to clip through blocks.");
    }
}
