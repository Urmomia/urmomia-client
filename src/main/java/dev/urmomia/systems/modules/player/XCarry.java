/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.player;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.packets.PacketEvent;
import dev.urmomia.mixin.CloseHandledScreenC2SPacketAccessor;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class XCarry extends Module {
    private boolean invOpened;

    public XCarry() {
        super(Categories.Player, "XCarry", "Allows you to store four extra items in your crafting grid.");
    }

    @Override
    public void onActivate() {
        invOpened = false;
    }

    @Override
    public void onDeactivate() {
        if (invOpened) {
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
        }
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!(event.packet instanceof CloseHandledScreenC2SPacket)) return;

        if (((CloseHandledScreenC2SPacketAccessor) event.packet).getSyncId() == mc.player.playerScreenHandler.syncId) {
            invOpened = true;
            event.cancel();
        }
    }
}
