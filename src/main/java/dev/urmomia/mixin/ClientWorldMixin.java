/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.mixin;

import dev.urmomia.MainClient;
import dev.urmomia.events.entity.EntityAddedEvent;
import dev.urmomia.events.entity.EntityRemovedEvent;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.world.Ambience;
import net.minecraft.client.render.SkyProperties;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Unique
    private final SkyProperties endSky = new SkyProperties.End();
    @Unique
    private final SkyProperties customSky = new Ambience.Custom();

    @Inject(method = "addEntityPrivate", at = @At("TAIL"))
    private void onAddEntityPrivate(int id, Entity entity, CallbackInfo info) {
        MainClient.EVENT_BUS.post(EntityAddedEvent.get(entity));
    }

    @Inject(method = "finishRemovingEntity", at = @At("TAIL"))
    private void onFinishRemovingEntity(Entity entity, CallbackInfo info) {
        MainClient.EVENT_BUS.post(EntityRemovedEvent.get(entity));
    }

    /**
     * @author Walaryne
     */
    @Inject(method = "method_23777", at = @At("HEAD"), cancellable = true)
    private void onGetSkyColor(BlockPos blockPos, float tickDelta, CallbackInfoReturnable<Vec3d> info) {
        Ambience ambience = Modules.get().get(Ambience.class);

        if (ambience.isActive() && ambience.changeSkyColor.get()) {
            info.setReturnValue(ambience.skyColor.get().getVec3d());
        }
    }

    /**
     * @author Walaryne
     */
    @Inject(method = "getSkyProperties", at = @At("HEAD"), cancellable = true)
    private void onGetSkyProperties(CallbackInfoReturnable<SkyProperties> info) {
        Ambience ambience = Modules.get().get(Ambience.class);

        if (ambience.isActive() && ambience.enderMode.get()) {
            info.setReturnValue(ambience.enderCustomSkyColor.get() ? customSky : endSky);
        }
    }

    /**
     * @author Walaryne
     */
    @Inject(method = "getCloudsColor", at = @At("HEAD"), cancellable = true)
    private void onGetCloudsColor(float tickDelta, CallbackInfoReturnable<Vec3d> info) {
        Ambience ambience = Modules.get().get(Ambience.class);

        if (ambience.isActive() && ambience.changeCloudColor.get()) {
            info.setReturnValue(ambience.cloudColor.get().getVec3d());
        }
    }
}
