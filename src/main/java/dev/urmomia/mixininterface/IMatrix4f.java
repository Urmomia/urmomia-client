/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.mixininterface;

import dev.urmomia.utils.misc.Vec4;

public interface IMatrix4f {
    void multiplyMatrix(Vec4 v, Vec4 out);
}
