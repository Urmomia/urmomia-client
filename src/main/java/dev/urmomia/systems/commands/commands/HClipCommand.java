package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.systems.commands.Command;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class HClipCommand extends Command {
    public HClipCommand() {
        super("hclip", "Lets you clip through blocks horizontally.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {
            ClientPlayerEntity player = mc.getInstance().player;
            assert player != null;

            double blocks = context.getArgument("blocks", Double.class);
            Vec3d forward = Vec3d.fromPolar(0, player.yaw).normalize();
            player.updatePosition(player.getX() + forward.x * blocks, player.getY(), player.getZ() + forward.z * blocks);

            return SINGLE_SUCCESS;
        }));
    }
}