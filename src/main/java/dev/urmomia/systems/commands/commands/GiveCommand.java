package dev.urmomia.systems.commands.commands;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import dev.urmomia.systems.commands.Command;
import dev.urmomia.utils.player.SlotUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.LiteralText;

public class GiveCommand extends Command {
    private final static SimpleCommandExceptionType NOT_IN_CREATIVE = new SimpleCommandExceptionType(new LiteralText("You must be in creative mode to use this."));

    public GiveCommand() {
        super("give", "Gives you any item. REQUIRES Creative mode.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("item", ItemStackArgumentType.itemStack()).then(argument("number", IntegerArgumentType.integer()).executes(context -> {
            if (!mc.player.isCreative()) throw NOT_IN_CREATIVE.create();

            ItemStack item = new ItemStack(context.getArgument("item", Item.class),context.getArgument("number", int.class));
            addItem(item);

            return SINGLE_SUCCESS;
        })));
        builder.then(argument("item", ItemStackArgumentType.itemStack()).executes(context -> {
            if (!mc.player.isCreative()) throw NOT_IN_CREATIVE.create();

            ItemStack item = new ItemStack(context.getArgument("item", Item.class),1);
            addItem(item);

            return SINGLE_SUCCESS;
        }));
    }
    
    public static void addItem(ItemStack item) {
		for(int i = 0; i < 36; i++)
		{
			if(!mc.player.inventory.getStack(SlotUtils.indexToId(i)).isEmpty()) continue;
			mc.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(SlotUtils.indexToId(i), item));
		}
    }
}