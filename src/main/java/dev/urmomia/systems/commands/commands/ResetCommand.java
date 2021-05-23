package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.gui.GuiThemes;
import dev.urmomia.settings.Setting;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.systems.commands.arguments.ModuleArgumentType;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ResetCommand extends Command {

    public ResetCommand() {
        super("reset", "Resets specified settings.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("settings")
                .then(argument("module", ModuleArgumentType.module()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);
                    module.settings.forEach(group -> group.forEach(Setting::reset));
                    return SINGLE_SUCCESS;
                }))
                .then(literal("all").executes(context -> {
                    Modules.get().getAll().forEach(module -> module.settings.forEach(group -> group.forEach(Setting::reset)));
                    return SINGLE_SUCCESS;
                }))
        ).then(literal("gui").executes(context -> {
            GuiThemes.get().clearWindowConfigs();
            ChatUtils.info("The ClickGUI positioning has been reset.");
            return SINGLE_SUCCESS;
        })).then(literal("bind")
                .then(argument("module", ModuleArgumentType.module()).executes(context -> {
                    Module module = context.getArgument("module", Module.class);

                    module.keybind.set(true, -1);
                    info("This bind has been reset.");

                    return SINGLE_SUCCESS;
                }))
                .then(literal("all").executes(context -> {
                    Modules.get().getAll().forEach(module -> module.keybind.set(true, -1));
                    return SINGLE_SUCCESS;
                }))
        ).then(literal("hud").executes(context -> {
            Modules.get().get(HUD.class).reset.run();
            return SINGLE_SUCCESS;
        }));
    }
}