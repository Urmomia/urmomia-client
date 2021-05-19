package dev.urmomia.systems.modules.misc;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.entity.LivingDeathEvent;
import dev.urmomia.events.packets.PacketEvent;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.StringSetting;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class AutoEz2 extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> ezed = sgGeneral.add(new StringSetting.Builder()
            .name("text")
            .description("The text you want to send when you ez someone.")
            .defaultValue("Ez! {victim}, {player} owns you and all!")
            .build()
    );

    private int hasBeenCombat;
    private PlayerEntity target;

    public AutoEz2() {
        super(Categories.Misc, "auto-ez", "Sends a chat message when you kill a player.");
    }

    @EventHandler
    public void packetSentEvent(PacketEvent.Sent event) {
        if(event.packet instanceof PlayerInteractEntityC2SPacket) {
            PlayerInteractEntityC2SPacket packet = (PlayerInteractEntityC2SPacket) event.packet;

            if(packet.getType() == PlayerInteractEntityC2SPacket.InteractionType.ATTACK) {
                Entity e = packet.getEntity(mc.world);
                if(e instanceof PlayerEntity) {
                    target = (PlayerEntity) e;
                    hasBeenCombat = 500;
                }

                if(e instanceof EndCrystalEntity) {
                    PlayerEntity newTarget = null;
                    for(PlayerEntity entityPlayer: mc.world.getPlayers()) {
                        if(entityPlayer.isDead()) continue;
                        if((newTarget == null && entityPlayer.distanceTo(e) < 4) ||
                                (newTarget != null && mc.player.distanceTo(entityPlayer) < mc.player.distanceTo(newTarget))) newTarget = entityPlayer;
                    }

                    if(newTarget != null) {
                        target = newTarget;
                        hasBeenCombat = 40;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void deathEvent(LivingDeathEvent event) {
            if(event.getEntity() instanceof PlayerEntity) {
                    String fart = farte(ezed, target);
                    if(hasBeenCombat > 0 && (target.getHealth() <= 0 || target.isDead() || !mc.world.getPlayers().contains(target))) mc.player.sendChatMessage(fart);

                hasBeenCombat = 0;
            }  
    }

    private int sinceLastMessage = 0;

    @EventHandler
    private void onTick() {
            if(mc.player.isDead()) hasBeenCombat = 0;

            if(sinceLastMessage == 0 && hasBeenCombat > 0 && (target.getHealth() <= 0 || target.isDead())) {
                String fart = farte(ezed, target);
                mc.player.sendChatMessage(fart);
                sinceLastMessage = 80;
                hasBeenCombat = 0;
            }
    
            if(sinceLastMessage > 0) sinceLastMessage--;
    
            hasBeenCombat--;
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