package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ModulesCommand extends Command {
    public ModulesCommand() {
        super("modules", "Displays a list of all modules.", "features");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.info("--- Modules (highlight)%d(default) ---", Modules.get().getCount());

            BaseText modules = new LiteralText("");

            for (Module module : Modules.get().getList()) {
                BaseText tooltip = new LiteralText("");

                tooltip.append(new LiteralText(module.title).formatted(Formatting.BLUE, Formatting.BOLD)).append("\n");
                tooltip.append(new LiteralText(module.name).formatted(Formatting.GRAY)).append("\n\n");
                tooltip.append(new LiteralText(module.description).formatted(Formatting.WHITE));

                BaseText finalModule = new LiteralText(module.title);
                if (module != Modules.get().getList().get(Modules.get().getAll().size() - 1)) modules.append(new LiteralText(", ").formatted(Formatting.GRAY));
                finalModule.setStyle(finalModule.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)));

                modules.append(finalModule);
            }

            ChatUtils.sendMsg(modules);

            return SINGLE_SUCCESS;
        });
    }

}
