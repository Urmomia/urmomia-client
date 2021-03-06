package dev.urmomia.systems.modules.player;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.ItemListSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.player.InvUtils;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class MatrixDupeAssist extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
            .name("items")
            .description("Items to drop.")
            .defaultValue(new ArrayList<>(0))
            .build()
    );

    private final Setting<Boolean> delay = sgGeneral.add(new BoolSetting.Builder()
            .name("delay")
            .description("Helps you dupe items properly if you're laggy.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> excludeHotbar = sgGeneral.add(new BoolSetting.Builder()
            .name("exclude-hotbar")
            .description("Whether or not to drop items from your hotbar.")
            .defaultValue(false)
            .build()
    );

    private int timer;

    public MatrixDupeAssist() {
        super(Categories.Player, "matrix-dupe-assist", "Used to assist you in Matrix Dupe, Only works for llamas.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }
    
    @EventHandler
    private void onTick(TickEvent.Post event) {
            if (delay.get() && timer <= 0) {
                timer = 20;
            } else {
                timer--;
                return;
            }

        if (mc.player.hasVehicle() && !(mc.player.getVehicle() instanceof LlamaEntity)) {
            error("The entity you're riding isn't a Llama.");
            mc.player.closeHandledScreen();
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
            mc.player.setSneaking(true);
            toggle();
        }

            if (mc.player.getVehicle() instanceof LlamaEntity) {
                for (int i = excludeHotbar.get() ? 9 : 0; i < mc.player.inventory.size(); i++) {
                    /*if (mc.currentScreen instanceof HandledScreen<?> && openInventory.get()) {
                        int slot = InvUtils.findItemInHotbar(Items.WHITE_CARPET) + (2 + 27 + (getLlamaSize(mc.player.getVehicle())));
                        InvUtils.dropSingle().slot(slot);
                        mc.player.closeHandledScreen();
                        continue;
                    }*/
                    if (mc.currentScreen instanceof HandledScreen<?>) mc.player.closeHandledScreen();
                    if (items.get().contains(mc.player.inventory.getStack(i).getItem())) {
                        InvUtils.drop().slot(i);
                    }
                }
            }
        }
}
