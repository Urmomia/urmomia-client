/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.mixin;

import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.player.PotionSaver;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {

    @Shadow private int duration;

    @Inject(method = "updateDuration", at = @At("HEAD"), cancellable = true)
    private void tick(CallbackInfoReturnable<Integer> info) {
        if (Modules.get().get(PotionSaver.class).shouldFreeze()) info.setReturnValue(duration);
    }

}
