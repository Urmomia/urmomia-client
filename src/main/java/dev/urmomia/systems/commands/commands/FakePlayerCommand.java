package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.player.FakePlayer;
import dev.urmomia.utils.entity.FakePlayerUtils;
import dev.urmomia.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class FakePlayerCommand extends Command {
    public FakePlayerCommand(){
        super("fake-player", "Manages fake players that you can use for testing.");
    }

    public static FakePlayer fakePlayer = Modules.get().get(FakePlayer.class);

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("spawn").executes(context -> {
            if (active()) FakePlayerUtils.spawnFakePlayer();
            return SINGLE_SUCCESS;
        })).then(literal("remove").then(argument("id", IntegerArgumentType.integer()).executes(context -> {
            int id = context.getArgument("id", Integer.class);
            if (active()) FakePlayerUtils.removeFakePlayer(id);
            return SINGLE_SUCCESS;
        }))).then(literal("clear").executes(context -> {
            if (active()) FakePlayerUtils.clearFakePlayers();
            return SINGLE_SUCCESS;
        }));
    }

    private boolean active() {
        if (!Modules.get().isActive(FakePlayer.class)) {
            ChatUtils.moduleError(Modules.get().get(FakePlayer.class),"The FakePlayer module must be enabled to use this command.");
            return false;
        }
        else return true;
    }
}