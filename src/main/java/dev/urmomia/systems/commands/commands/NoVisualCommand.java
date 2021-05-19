package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.Module;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class NoVisualCommand extends Command {
    public NoVisualCommand() {
        super("no-visual", "Disables all render modules, used for screenshots.", "novis");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            new ArrayList<>(Modules.get().getGroup(Categories.Render)).forEach(NoVisualCommand::bub);

            return SINGLE_SUCCESS;
        });
    }

    public static void bub(Module m) {
        if(m.isActive()) m.toggle();
    }
}
