package dev.urmomia.systems.modules.player;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import dev.urmomia.events.game.GameLeftEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.ItemListSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class AutoMatrixDupe extends Module {
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

    public AutoMatrixDupe() {
        super(Categories.Player, "auto-matrix-dupe", "Automatically does the Matrix Dupe for you.");
    }

    private int ticks;
    private int part;

    @Override
    public void onActivate() {
        ticks = 0;
    }

    @Override
    public void onDeactivate() {
        ticks = 0;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        ticks = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            toggle();
            mc.player.closeHandledScreen();
            return;
        }

        if (!(mc.player.getVehicle() instanceof LlamaEntity)) {
            error("Not riding a Llama, disabling...");
            this.toggle();
            return;
        }
        else if (!(InvUtils.findItemInHotbar(Items.WHITE_CARPET) != -1)) {
            error("Cannot find a White Carpet in Hotbar, disabling...");
            this.toggle();
            return;
        }

        for (;!(ticks >= 40);) {
            ticks++;
            if (ticks == 5) part = 1;
            if (ticks == 15) part = 2;
            if (ticks == 25) part = 3;
            if (ticks == 30) part = 4;
            if (ticks >= 50) part = 0; break;
        }

        if (part == 1 && !(mc.currentScreen instanceof HorseScreen)) mc.player.openRidingInventory();
        if (part == 2 && InvUtils.findItemInHotbar(Items.WHITE_CARPET) != -1) {
            int slot = InvUtils.findItemInHotbar(Items.WHITE_CARPET) + (2 + 27 + (getLlamaSize(mc.player.getVehicle())));
            InvUtils.dropSingle().slot(slot);
        }
        if (part == 3 && (mc.currentScreen instanceof HorseScreen)) mc.player.closeHandledScreen();
        if (part == 4) {
            for (int i = excludeHotbar.get() ? 9 : 0; i < mc.player.inventory.size(); i++) {
                if (items.get().contains(mc.player.inventory.getStack(i).getItem())) {
                    InvUtils.drop().slot(i);
                }
            }
        }
    }
    
    private int getLlamaSize(Entity e) {
        return 3 * ((LlamaEntity) e).getStrength();
    }
}
