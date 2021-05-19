/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.settings.Setting;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.systems.commands.arguments.ModuleArgumentType;
import dev.urmomia.systems.commands.arguments.SettingArgumentType;
import dev.urmomia.systems.commands.arguments.SettingValueArgumentType;
import dev.urmomia.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static dev.urmomia.systems.commands.arguments.SettingArgumentType.getSetting;

public class SettingCommand extends Command {
    public SettingCommand() {
        super("settings", "Allows you to view and change module settings.", "s");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
                argument("module", ModuleArgumentType.module())
                .then(
                        argument("setting", SettingArgumentType.setting())
                        .executes(context -> {
                            // Get setting value
                            Setting<?> setting = getSetting(context);

                            ChatUtils.info("Setting (highlight)%s(default) is (highlight)%s(default).", setting.title, setting.get());

                            return SINGLE_SUCCESS;
                        })
                        .then(
                                argument("value", SettingValueArgumentType.value())
                                .executes(context -> {
                                    // Set setting value
                                    Setting<?> setting = getSetting(context);
                                    String value = context.getArgument("value", String.class);

                                    if (setting.parse(value)) {
                                        ChatUtils.info("Setting (highlight)%s(default) changed to (highlight)%s(default).", setting.title, value);
                                    }

                                    return SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }
}
