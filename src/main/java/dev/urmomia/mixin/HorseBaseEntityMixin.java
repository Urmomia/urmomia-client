/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.mixin;

import dev.urmomia.mixininterface.IHorseBaseEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HorseBaseEntity.class)
public abstract class HorseBaseEntityMixin implements IHorseBaseEntity {
    @Shadow protected abstract void setHorseFlag(int bitmask, boolean flag);

    @Override
    public void setSaddled(boolean saddled) {
        setHorseFlag(4, saddled);
    }
}
