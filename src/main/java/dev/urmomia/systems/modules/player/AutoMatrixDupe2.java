package dev.urmomia.systems.modules.player;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.IntSetting;
import dev.urmomia.settings.ItemListSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.player.ChatUtils;
//import dev.urmomia.systems.modules.movement.GUIMove;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Hand;

public class AutoMatrixDupe2 extends Module{
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
            .name("items")
            .description("Items to drop.")
            .defaultValue(new ArrayList<>(0))
            .build()
    );

    private final Setting<Boolean> excludeHotbar = sgGeneral.add(new BoolSetting.Builder()
            .name("exclude-hotbar")
            .description("Whether or not to drop items from your hotbar.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay in ticks between actions.")
            .defaultValue(15)
            .min(10)
            .max(120)
            .sliderMin(10)
            .sliderMax(120)
            .build()
    );

    private int timer;
    private AbstractDonkeyEntity entity;

    public AutoMatrixDupe2() {
        super(Categories.Player, "auto-matrix-dupe", "Does the Matrix Dupe for you, only works for llamas. Press ESC to stop dupe.");
    }

    @Override
    public void onActivate() {
        timer = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            ChatUtils.info("ESC key pressed, disabling dupe...");
            toggle();
            mc.player.closeHandledScreen();
            return;
        }

        if (timer <= 0) {
            timer = delay.get();
        } else {
            timer--;
            return;
        }

        if (!(InvUtils.findItemInHotbar(Items.WHITE_CARPET) != -1)) {
            ChatUtils.moduleError(this, "No white carpet was found in hotbar, disabling...");
            toggle();
        }

        else {
            //if(isDupeTime()) {
                if (mc.player.hasVehicle() && !(mc.player.getVehicle() instanceof LlamaEntity)) {
                    ChatUtils.moduleError(this, "The entity you're riding isn't a Llama, disabling...");
                    mc.player.closeHandledScreen();
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                    mc.player.setSneaking(true);
                    toggle();
                }
                else if (mc.player.hasVehicle()) {
                        if (!(mc.currentScreen instanceof HorseScreen)) {
                            for (int i = excludeHotbar.get() ? 9 : 0; i < mc.player.inventory.size(); i++) {
                                if (items.get().contains(mc.player.inventory.getStack(i).getItem())) {
                                    InvUtils.drop().slot(i);
                                }
                            mc.player.openRidingInventory();
                        }
                        if (mc.currentScreen instanceof HandledScreen<?>) {
                            mc.player.closeHandledScreen();
                            mc.player.openRidingInventory();
                            mc.player.closeHandledScreen();
                            int slot = InvUtils.findItemInHotbar(Items.WHITE_CARPET) + (2 + 27 + (getLlamaSize(mc.player.getVehicle())));
                            InvUtils.dropSingle().slot(slot);
                            //ChatUtils.info("Slot "+ slot + ", HotbarGetSlot " + InvUtils.findItemInHotbar(Items.WHITE_CARPET) + ", LlamaSizeSlot " + getLlamaSize(mc.player.getVehicle()));
                        }
                    }
                }
                else if (!(mc.player.hasVehicle())) {
                    for (Entity entity : mc.world.getEntities()){
                        if (mc.player.distanceTo(entity) > 4) continue;
                        if (mc.player.getMainHandStack().getItem() instanceof SpawnEggItem) return;
                        if (entity instanceof LlamaEntity) {
                            interact(entity);
                        }
                    }
                }
            //}
        }
    }

    /*private boolean isDupeTime() {
        if (mc.player.getVehicle() != entity || entity.hasChest() || mc.player.currentScreenHandler.getStacks().size() == 46) {
            return false;
        }

        if (mc.player.currentScreenHandler.getStacks().size() > 38) {
            for (int i = 2; i < getDupeSize() + 1; i++) {
                if (mc.player.currentScreenHandler.getSlot(i).hasStack()) {
                    return true;
                }
            }
        }

        return false;
    }*/
    //im a lazy ass

    /*private int getDupeSize() {
        if (mc.player.getVehicle() != entity || entity.hasChest() || mc.player.currentScreenHandler.getStacks().size() == 46) {
            return 0;
        }

        return mc.player.currentScreenHandler.getStacks().size() - 38;
    }*/

    private void interact(Entity entity) {
        Rotations.rotate(Rotations.getYaw(entity), Rotations.getPitch(entity), -100, () -> mc.interactionManager.interactEntity(mc.player, entity, Hand.MAIN_HAND));
    }

    private int getLlamaSize(Entity e) {
        return 3 * ((LlamaEntity) e).getStrength();
    }

    //hi
}
