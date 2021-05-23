package dev.urmomia.systems.modules.combat;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.*;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.entity.SortPriority;
import dev.urmomia.utils.entity.TargetUtils;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.world.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class AutoWeb extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
            .name("target-range")
            .description("The maximum distance to target players.")
            .defaultValue(4)
            .min(0)
            .build()
    );

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
            .name("target-priority")
            .description("How to select the player to target.")
            .defaultValue(SortPriority.LowestDistance)
            .build()
    );

    private final Setting<Boolean> doubles = sgGeneral.add(new BoolSetting.Builder()
            .name("doubles")
            .description("Places webs in the target's upper hitbox as well as the lower hitbox.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates towards the webs when placing.")
            .defaultValue(true)
            .build()
    );

    public AutoWeb() {
        super(Categories.Combat, "auto-web", "Automatically places webs on other players.");
    }

    private PlayerEntity target = null;

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (TargetUtils.isBadTarget(target, range.get())) {
            target = TargetUtils.getPlayerTarget(range.get(), priority.get());
        }
        if (TargetUtils.isBadTarget(target, range.get())) return;

        BlockUtils.place(target.getBlockPos(), Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.COBWEB), rotate.get(), 0, false);
        if (doubles.get()) BlockUtils.place(target.getBlockPos().add(0, 1, 0), Hand.MAIN_HAND, InvUtils.findItemInHotbar(Items.COBWEB), rotate.get(), 0, false);
    }
}