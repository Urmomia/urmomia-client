package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.systems.commands.Command;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class VClipCommand extends Command {
    public VClipCommand() {
        super("vclip", "Lets you clip through blocks vertically.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {
            ClientPlayerEntity player = mc.getInstance().player;
            assert player != null;

            double blocks = context.getArgument("blocks", Double.class);
            player.updatePosition(player.getX(), player.getY() + blocks, player.getZ());

            return SINGLE_SUCCESS;
        }));
    }
}