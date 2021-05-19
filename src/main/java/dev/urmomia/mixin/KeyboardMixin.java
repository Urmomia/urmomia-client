/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.mixin;

import dev.urmomia.MainClient;
import dev.urmomia.events.meteor.CharTypedEvent;
import dev.urmomia.events.meteor.KeyEvent;
import dev.urmomia.gui.GuiKeyEvents;
import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.misc.input.Input;
import dev.urmomia.utils.misc.input.KeyAction;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int i, int j, CallbackInfo info) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            if (client.currentScreen instanceof WidgetScreen && i == GLFW.GLFW_REPEAT) {
                ((WidgetScreen) client.currentScreen).keyRepeated(key, j);
            }

            if (GuiKeyEvents.canUseKeys) {
                Input.setKeyState(key, i != GLFW.GLFW_RELEASE);

                KeyEvent event = MainClient.EVENT_BUS.post(KeyEvent.get(key, KeyAction.get(i)));

                if (event.isCancelled()) info.cancel();
            }
        }
    }

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void onChar(long window, int i, int j, CallbackInfo info) {
        if (Utils.canUpdate() && !client.isPaused() && (client.currentScreen == null || client.currentScreen instanceof WidgetScreen)) {
            CharTypedEvent event = MainClient.EVENT_BUS.post(CharTypedEvent.get((char) i));

            if (event.isCancelled()) info.cancel();
        }
    }
}
