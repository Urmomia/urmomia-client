/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.misc;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import dev.urmomia.events.packets.PacketEvent;
import dev.urmomia.settings.PacketBoolSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.network.PacketUtils;
import net.minecraft.network.Packet;

public class PacketCanceller extends Module {
    public static Object2BooleanMap<Class<? extends Packet<?>>> S2C_PACKETS = new Object2BooleanArrayMap<>();
    public static Object2BooleanMap<Class<? extends Packet<?>>> C2S_PACKETS = new Object2BooleanArrayMap<>();
    
    static {
        for (Class<? extends Packet<?>> packet : PacketUtils.getS2CPackets()) S2C_PACKETS.put(packet, false);
        for (Class<? extends Packet<?>> packet : PacketUtils.getC2SPackets()) C2S_PACKETS.put(packet, false);
    }
    
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<Object2BooleanMap<Class<? extends Packet<?>>>> s2cPackets = sgGeneral.add(new PacketBoolSetting.Builder()
            .name("S2C-packets")
            .description("Server-to-client packets to cancel.")
            .defaultValue(S2C_PACKETS)
            .build()
    );

    private final Setting<Object2BooleanMap<Class<? extends Packet<?>>>> c2sPackets = sgGeneral.add(new PacketBoolSetting.Builder()
            .name("C2S-packets")
            .description("Client-to-server packets to cancel.")
            .defaultValue(C2S_PACKETS)
            .build()
    );

    public PacketCanceller() {
        super(Categories.Misc, "packet-canceller", "Allows you to cancel certain packets.");
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onReceivePacket(PacketEvent.Receive event) {
        if (s2cPackets.get().getBoolean(event.packet.getClass())) event.cancel();
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onSendPacket(PacketEvent.Send event) {
        if (c2sPackets.get().getBoolean(event.packet.getClass())) event.cancel();
    }
}
