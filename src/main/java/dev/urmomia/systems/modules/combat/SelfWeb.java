package dev.urmomia.systems.modules.combat;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.*;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.entity.TargetUtils;
import dev.urmomia.utils.entity.SortPriority;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.world.BlockUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class SelfWeb extends Module {

    public enum Mode {
        Normal,
        Smart
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("The mode to use for selfweb.")
            .defaultValue(Mode.Normal)
            .build()
    );

    private final Setting<Boolean> doubles = sgGeneral.add(new BoolSetting.Builder()
            .name("double-place")
            .description("Places webs in your upper hitbox as well.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> turnOff = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-toggle")
            .description("Toggles off after placing the webs.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .description("How far away the player has to be from you to place webs. Requires Mode to Smart.")
            .defaultValue(3)
            .min(1)
            .sliderMax(7)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Forces you to rotate downwards when placing webs.")
            .defaultValue(true)
            .build()
    );

    public SelfWeb() {
        super(Categories.Combat, "self-web", "Automatically places webs on you.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        switch (mode.get()) {
            case Normal:
                placeWeb();
                break;
            case Smart:
                if (TargetUtils.getPlayerTarget(range.get(), SortPriority.LowestDistance) != null) placeWeb();
                break;
        }
    }

    private void placeWeb() {
        int slot = InvUtils.findItemInHotbar(Items.COBWEB);

        BlockPos blockPos = mc.player.getBlockPos();
        BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 0, false);

        if (doubles.get()) {
            blockPos = mc.player.getBlockPos().add(0, 1, 0);
            BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 0, false);
        }

        if (turnOff.get()) toggle();
    }
}
