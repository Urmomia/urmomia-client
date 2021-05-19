package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class HonsCommand extends Command {

    public HonsCommand() {
        super("hons", "hons结晶度qq");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.info("结晶度qqam hons 结晶度qq and hons is hons结晶度qq yes hons结晶度qq");
            return SINGLE_SUCCESS;
        });
    }
}

