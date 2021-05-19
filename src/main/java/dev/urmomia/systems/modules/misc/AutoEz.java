package dev.urmomia.systems.modules.misc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.game.GameJoinedEvent;
import dev.urmomia.events.packets.PacketEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.StringSetting;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import java.util.UUID;

public class AutoEz extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> ezed = sgGeneral.add(new StringSetting.Builder()
            .name("text")
            .description("The text you want to send when you ez someone.")
            .defaultValue("Ez! {victim}, {player} owns you and all!")
            .build()
    );

    private int timer;

    private final Object2IntMap<UUID> totemPops = new Object2IntOpenHashMap<>();
    private final Object2IntMap<UUID> chatIds = new Object2IntOpenHashMap<>();

    public AutoEz() {
        super(Categories.Misc, "auto-ez", "Sends a chat message when a player dies near you.");
    }

    @Override
    public void onActivate() {
        timer = 0;
        totemPops.clear();
        chatIds.clear();
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        totemPops.clear();
        chatIds.clear();
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityStatusS2CPacket)) return;

        EntityStatusS2CPacket p = (EntityStatusS2CPacket) event.packet;
        if (p.getStatus() != 35) return;

        Entity entity = p.getEntity(mc.world);

        synchronized (totemPops) {
            int pops = totemPops.getOrDefault(entity.getUuid(), 0);
            totemPops.put(entity.getUuid(), ++pops);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {

        if (timer <= 0) {
            timer = 20;
        } else {
            timer--;
            return;
        }

        synchronized (totemPops) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (!totemPops.containsKey(player.getUuid())) continue;
                if (player.distanceTo(mc.player) < 4) continue;

                if (player.deathTime > 0 || player.getHealth() <= 0) {
                    String fart = farte(ezed, player);
                    mc.player.sendChatMessage(fart);
                    chatIds.removeInt(player.getUuid());
                }
            }
        }
    }
    private String farte(Setting<String> line, PlayerEntity player) {
        if (line.get().length() > 0) return line.get().replace("{player}", getName()).replace("{victim}", player.getGameProfile().getName());
        else return null;
    }

    private String getName(){
        return mc.player.getGameProfile().getName();
    }

    //ignore trash code :troll:
} 