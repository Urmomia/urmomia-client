package dev.urmomia.systems.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.BlockListSetting;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.EnumSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;


public class Auto32K extends Module {
    public enum Mode{
        Hopper,
        Dispenser
    }

    public Auto32K(){
        super(Categories.Combat, "auto-32k", "Automatically attacks other players with a 32k weapon.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("mode")
            .description("The bypass mode used.")
            .defaultValue(Mode.Dispenser)
            .build()
    );

    private final Setting<Double> placeRange = sgGeneral.add(new DoubleSetting.Builder()
            .name("place-range")
            .description("The distance in a single direction the shulker is placed.")
            .defaultValue(3)
            .min(0)
            .sliderMax(5)
            .build()
    );

    private final Setting<Boolean> fillHopper = sgGeneral.add(new BoolSetting.Builder()
            .name("fill-hopper")
            .description("Fills all slots of the hopper except one for the 32k.")
            .defaultValue(true)
            .build()
    );

    private final Setting<List<Block>> throwawayItems = sgGeneral.add(new BlockListSetting.Builder()
            .name("throwaway-blocks")
            .description("Whitelisted blocks to use to fill the hopper.")
            .defaultValue(setDefaultBlocks())
            .build()
    );

    private final Setting<Boolean> autoMove = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-move")
            .description("Moves the 32K into your inventory automatically.")
            .defaultValue(true)
            .build()
    );

    private int x;
    private int z;
    private int phase = 0;
    private BlockPos bestBlock;

    @Override
    public void onDeactivate() {
        phase = 0;
    }

    @Override
    public void onActivate() {
        bestBlock = findValidBlocksDispenser();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (phase <= 7) {
            if (mode.get() == Mode.Hopper) {
                int shulkerSlot = InvUtils.findItemWithCount(Items.SHULKER_BOX).slot;
                int hopperSlot = InvUtils.findItemWithCount(Items.HOPPER).slot;
                if (isValidSlot(shulkerSlot) || isValidSlot(hopperSlot)) return;
                List<BlockPos> sortedBlocks = findValidBlocksHopper();
                sortedBlocks.sort(Comparator.comparingDouble(value -> mc.player.squaredDistanceTo(value.getX(), value.getY(), value.getZ())));
                Iterator<BlockPos> sortedIterator = sortedBlocks.iterator();
                BlockPos bestBlock = null;
                if(sortedIterator.hasNext()) bestBlock = sortedIterator.next();

                if (bestBlock != null) {
                    mc.player.inventory.selectedSlot = hopperSlot;
                    while (!PlayerUtils.placeBlock(bestBlock, Hand.MAIN_HAND)) {
                        if(sortedIterator.hasNext()) {
                            bestBlock = sortedIterator.next().up();
                        }else break;
                    }
                    mc.player.setSneaking(true);
                    mc.player.inventory.selectedSlot = shulkerSlot;
                    if (!PlayerUtils.placeBlock(bestBlock.up(), Hand.MAIN_HAND)) {
                        error("Failed to place.");
                        this.toggle();
                        return;
                    }
                    mc.player.setSneaking(false);
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), mc.player.getHorizontalFacing(), bestBlock.up(), false));
                    phase = 8;
                }
            } else if (mode.get() == Mode.Dispenser) {
                int shulkerSlot = InvUtils.findItemWithCount(Items.SHULKER_BOX).slot;
                int hopperSlot = InvUtils.findItemWithCount(Items.HOPPER).slot;
                int dispenserSlot = InvUtils.findItemWithCount(Items.DISPENSER).slot;
                int redstoneSlot = InvUtils.findItemWithCount(Items.REDSTONE_BLOCK).slot;
                if ((isValidSlot(shulkerSlot) && mode.get() == Mode.Hopper) || isValidSlot(hopperSlot) || isValidSlot(dispenserSlot) || isValidSlot(redstoneSlot))
                    return;
                if (phase == 0) {
                    bestBlock = findValidBlocksDispenser();
                    mc.player.inventory.selectedSlot = hopperSlot;
                    if(bestBlock == null) return;
                    if (!PlayerUtils.placeBlock(bestBlock.add(x, 0, z), Hand.MAIN_HAND)) {
                        error("Failed to place.");
                        this.toggle();
                        return;
                    }
                    phase += 1;
                } else if (phase == 1) {
                    mc.player.inventory.selectedSlot = dispenserSlot;
                    if (x == -1) {
                        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(-90f, mc.player.pitch, mc.player.isOnGround()));
                    } else if (x == 1) {
                        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(90f, mc.player.pitch, mc.player.isOnGround()));
                    } else if (z == -1) {
                        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(1f, mc.player.pitch, mc.player.isOnGround()));
                    } else if (z == 1) {
                        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(179f, mc.player.pitch, mc.player.isOnGround()));
                    }
                    phase += 1;
                } else if (phase == 2) {
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.UP, bestBlock, false));
                    phase += 1;
                } else if (phase == 3) {
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), mc.player.getHorizontalFacing().getOpposite(), bestBlock.up(), false));
                    phase += 1;
                }else if (phase == 4 && mc.currentScreen instanceof Generic3x3ContainerScreen) {
                    mc.player.getSpeed();
                    InvUtils.move().from(shulkerSlot).toId(4);
                    phase += 1;
                }else if (phase == 5 && mc.currentScreen instanceof Generic3x3ContainerScreen) {
                    mc.player.closeHandledScreen();
                    phase += 1;
                }else if (phase == 6) {
                    mc.player.inventory.selectedSlot = redstoneSlot;
                    mc.player.setSneaking(true);
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), mc.player.getHorizontalFacing().getOpposite(), bestBlock.up(2), false));
                    mc.player.setSneaking(false);
                    phase += 1;
                }else if (phase == 7){
                    mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), mc.player.getHorizontalFacing().getOpposite(), bestBlock.add(x, 0, z), false));
                    phase += 1;
                }
            }
        }else if(phase == 8) {
            if (mc.currentScreen instanceof HopperScreen) {
                if (fillHopper.get() && !throwawayItems.get().isEmpty()) {
                    int slot = -1;
                    int count = 0;
                    Iterator<Block> blocks = throwawayItems.get().iterator();
                    for (Item item = blocks.next().asItem(); blocks.hasNext(); item = blocks.next().asItem()) {
                        for (int i = 5; i <= 40; i++) {
                            ItemStack stack = mc.player.inventory.getStack(i);
                            if (stack.getItem() == item && stack.getCount() >= 4) {
                                slot = i;
                                count = stack.getCount();
                                break;
                            }
                        }
                        if (count >= 4) break;
                    }
                    for (int i = 1; i < 5; i++) {
                        if (mc.player.currentScreenHandler.getSlot(i).getStack().getItem() instanceof AirBlockItem) {
                            InvUtils.move().from(slot - 4).toId(i);
                        }
                    }
                }
                boolean manage = true;
                int slot = -1;
                int dropSlot = -1;
                for (int i = 32; i < 41; i++) {
                    if (EnchantmentHelper.getLevel(Enchantments.SHARPNESS, mc.player.currentScreenHandler.getSlot(i).getStack()) > 5) {
                        manage = false;
                        slot = i;
                        break;
                    }else if (mc.player.currentScreenHandler.getSlot(i).getStack().getItem() instanceof SwordItem
                            && EnchantmentHelper.getLevel(Enchantments.SHARPNESS, mc.player.currentScreenHandler.getSlot(i).getStack()) <= 5) {
                        dropSlot = i;
                    }
                }
                if (dropSlot != -1) InvUtils.drop().slot(dropSlot);
                if(autoMove.get() && manage){
                    int slot2 = mc.player.inventory.getEmptySlot();
                    if (slot2 < 9 && slot2 != -1 && EnchantmentHelper.getLevel(Enchantments.SHARPNESS, mc.player.currentScreenHandler.getSlot(0).getStack()) > 5) {
                        InvUtils.move().fromId(0).to(slot2 - 4);
                    } else if (EnchantmentHelper.getLevel(Enchantments.SHARPNESS, mc.player.currentScreenHandler.getSlot(0).getStack()) <= 5 && mc.player.currentScreenHandler.getSlot(0).getStack().getItem() != Items.AIR) {
                        InvUtils.drop().slotId(0);
                    }
                }
                if(slot != -1) {
                    mc.player.inventory.selectedSlot = slot - 32;
                }
            }else this.toggle();
        }
    }

    private List<BlockPos> findValidBlocksHopper(){
        Iterator<BlockPos> allBlocks = getRange(mc.player.getBlockPos(), placeRange.get()).iterator();
        List<BlockPos> validBlocks = new ArrayList<>();
        for(BlockPos i = null; allBlocks.hasNext(); i = allBlocks.next()){
            if(i == null) continue;
            if(!mc.world.getBlockState(i).getMaterial().isReplaceable()
                    && (mc.world.getBlockState(i.up()).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.up().getX(), i.up().getY(), i.up().getZ(), i.up().getX() + 1.0D, i.up().getY() + 2.0D, i.up().getZ() + 1.0D)).isEmpty())
                    && mc.world.getBlockState(i.up(2)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.up(2).getX(), i.up(2).getY(), i.up(2).getZ(), i.up(2).getX() + 1.0D, i.up(2).getY() + 2.0D, i.up(2).getZ() + 1.0D)).isEmpty()){
                validBlocks.add(i);
            }
        }
        return validBlocks;
    }

    private BlockPos findValidBlocksDispenser(){
        List<BlockPos> allBlocksNotSorted = getRange(mc.player.getBlockPos(), placeRange.get());
        allBlocksNotSorted.sort(Comparator.comparingDouble(value -> mc.player.squaredDistanceTo(value.getX(), value.getY(), value.getZ())));
        Iterator<BlockPos> allBlocks = allBlocksNotSorted.iterator();
        for(BlockPos i = null; allBlocks.hasNext(); i = allBlocks.next()){
            if(i == null) continue;
            if(!mc.world.getBlockState(i).getMaterial().isReplaceable()
                    && (mc.world.getBlockState(i.up()).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.up().getX(), i.up().getY(), i.up().getZ(), i.up().getX() + 1.0D, i.up().getY() + 2.0D, i.up().getZ() + 1.0D)).isEmpty())
                    && (mc.world.getBlockState(i.up(2)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.up(2).getX(), i.up(2).getY(), i.up(2).getZ(), i.up(2).getX() + 1.0D, i.up(2).getY() + 2.0D, i.up(2).getZ() + 1.0D)).isEmpty())
                    && (mc.world.getBlockState(i.up(3)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.up(3).getX(), i.up(3).getY(), i.up(3).getZ(), i.up(2).getX() + 1.0D, i.up(2).getY() + 2.0D, i.up(2).getZ() + 1.0D)).isEmpty())){
                if (mc.world.getBlockState(i.add(-1, 1, 0)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(-1, 1, 0).getX(), i.add(-1, 1, 0).getY(), i.add(-1, 1, 0).getZ(), i.add(-1, 1, 0).getX() + 1.0D, i.add(-1, 1, 0).getY() + 2.0D, i.add(-1, 1, 0).getZ() + 1.0D)).isEmpty()
                        && mc.world.getBlockState(i.add(-1, 0, 0)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(-1, 0, 0).getX(), i.add(-1, 0, 0).getY(), i.add(-1, 0, 0).getZ(), i.add(-1, 0, 0).getX() + 1.0D, i.add(-1, 0, 0).getY() + 2.0D, i.add(-1, 0, 0).getZ() + 1.0D)).isEmpty()) {
                    x = -1;
                    z = 0;
                    return i;
                }else if (mc.world.getBlockState(i.add(1, 1, 0)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(1, 1, 0).getX(), i.add(1, 1, 0).getY(), i.add(1, 1, 0).getZ(), i.add(1, 1, 0).getX() + 1.0D, i.add(1, 1, 0).getY() + 2.0D, i.add(1, 1, 0).getZ() + 1.0D)).isEmpty()
                        && mc.world.getBlockState(i.add(1, 0, 0)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(1, 0, 0).getX(), i.add(1, 0, 0).getY(), i.add(1, 0, 0).getZ(), i.add(1, 0, 0).getX() + 1.0D, i.add(1, 0, 0).getY() + 2.0D, i.add(1, 0, 0).getZ() + 1.0D)).isEmpty()) {
                    x = 1;
                    z = 0;
                    return i;
                }else if (mc.world.getBlockState(i.add(0, 1, -1)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(0, 1, -1).getX(), i.add(0, 1, -1).getY(), i.add(0, 1, -1).getZ(), i.add(0, 1, -1).getX() + 1.0D, i.add(0, 1, -1).getY() + 2.0D, i.add(0, 1, -1).getZ() + 1.0D)).isEmpty()
                        && mc.world.getBlockState(i.add(0, 0, -1)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(0, 0, -1).getX(), i.add(0, 0, -1).getY(), i.add(0, 0, -1).getZ(), i.add(0, 0, -1).getX() + 1.0D, i.add(0, 0, -1).getY() + 2.0D, i.add(0, 0, -1).getZ() + 1.0D)).isEmpty()) {
                    x = 0;
                    z = -1;
                    return i;
                }else if (mc.world.getBlockState(i.add(0, 1, 1)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(0, 1, 1).getX(), i.add(0, 1, 1).getY(), i.add(0, 1, 1).getZ(), i.add(0, 1, 1).getX() + 1.0D, i.add(0, 1, 1).getY() + 2.0D, i.add(0, 1, 1).getZ() + 1.0D)).isEmpty()
                        && mc.world.getBlockState(i.add(0, 0, 1)).getBlock() == Blocks.AIR && mc.world.getOtherEntities(null, new Box(i.add(0, 0, 1).getX(), i.add(0, 0, 1).getY(), i.add(0, 0, 1).getZ(), i.add(0, 0, 1).getX() + 1.0D, i.add(0, 0, 1).getY() + 2.0D, i.add(0, 0, 1).getZ() + 1.0D)).isEmpty()) {
                    x = 0;
                    z = 1;
                    return i;
                }
            }
        }
        return null;
    }

    private List<BlockPos> getRange(BlockPos player, double range){
        List<BlockPos> allBlocks = new ArrayList<>();
        for(double i = player.getX() - range; i < player.getX() + range; i++){
            for(double j = player.getZ() - range; j < player.getZ() + range; j++){
                for(int k = player.getY() - 3; k < player.getY() + 3; k++){
                    BlockPos x = new BlockPos(i, k, j);
                    allBlocks.add(x);
                }
            }
        }
        return allBlocks;
    }

    private boolean isValidSlot(int slot){
        return slot == -1 || slot >= 9;
    }

    private List<Block> setDefaultBlocks(){
        List<Block> list = new ArrayList<>();
        list.add(Blocks.OBSIDIAN);
        list.add(Blocks.COBBLESTONE);
        return list;
    }
} 