/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.mixin;

import dev.urmomia.MainClient;
import dev.urmomia.events.meteor.MouseButtonEvent;
import dev.urmomia.events.meteor.MouseScrollEvent;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.render.FreeRotate;
import dev.urmomia.systems.modules.render.Freecam;
import dev.urmomia.utils.misc.input.Input;
import dev.urmomia.utils.misc.input.KeyAction;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
        Input.setButtonState(button, action != GLFW_RELEASE);

        if (MainClient.EVENT_BUS.post(MouseButtonEvent.get(button, KeyAction.get(action))).isCancelled()) info.cancel();
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        if (MainClient.EVENT_BUS.post(MouseScrollEvent.get(vertical)).isCancelled()) info.cancel();
    }

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void updateMouseChangeLookDirection(ClientPlayerEntity player, double cursorDeltaX, double cursorDeltaY) {
        Freecam freecam = Modules.get().get(Freecam.class);
        FreeRotate freeRotate = Modules.get().get(FreeRotate.class);

        if (freecam.isActive()) freecam.changeLookDirection(cursorDeltaX * 0.15, cursorDeltaY * 0.15);
        else if (!freeRotate.cameraMode()) player.changeLookDirection(cursorDeltaX, cursorDeltaY);
    }

    @Inject(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/tutorial/TutorialManager;onUpdateMouse(DD)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void perspectiveUpdatePitchYaw(CallbackInfo info, double adjustedSens, double x, double y, int invert) {
        FreeRotate freeRotate = Modules.get().get(FreeRotate.class);
        if (freeRotate.cameraMode()) {
            freeRotate.cameraYaw += x / freeRotate.sensitivity.get().floatValue();
            freeRotate.cameraPitch += (y * invert) / freeRotate.sensitivity.get().floatValue();
            if (Math.abs(freeRotate.cameraPitch) > 90.0F) freeRotate.cameraPitch = freeRotate.cameraPitch > 0.0F ? 90.0F : -90.0F;
        }
    }
}
