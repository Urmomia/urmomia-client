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
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class AutoDrop extends Module {
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

    public AutoDrop() {
        super(Categories.Player, "auto-drop", "Automatically drops specified items.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.currentScreen instanceof HandledScreen<?>) return;

        for (int i = excludeHotbar.get() ? 9 : 0; i < mc.player.inventory.size(); i++) {
            if (items.get().contains(mc.player.inventory.getStack(i).getItem())) {
                InvUtils.drop().slot(i);
            }
        }
    }
}
