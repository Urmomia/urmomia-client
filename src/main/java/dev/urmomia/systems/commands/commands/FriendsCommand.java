package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.systems.friends.Friend;
import dev.urmomia.systems.friends.Friends;
import net.minecraft.command.CommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static net.minecraft.command.CommandSource.suggestMatching;

public class FriendsCommand extends Command {

    public FriendsCommand() {
        super("friends", "Manages friends.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

        builder.then(literal("add").then(argument("friend", FriendArgumentType.friend())
                        .executes(context -> {
                            Friend friend = FriendArgumentType.getFriend(context, "friend");

                            if (Friends.get().add(friend)) info("Added (highlight)%s (default)to friends.", friend.name);
                            else error("That person is already your friend.");

                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("remove").then(argument("friend", FriendArgumentType.friend())
                        .executes(context -> {
                            Friend friend = FriendArgumentType.getFriend(context, "friend");

                            if (Friends.get().remove(friend)) info("Removed (highlight)%s (default)from friends.", friend.name);
                            else error("That person is not your friend.");

                            return SINGLE_SUCCESS;
                        })
                )
        );

        builder.then(literal("list").executes(context -> {
                    info("(highlight)%d(default) friends:", Friends.get().count());
                    Friends.get().forEach(friend-> info(" - (highlight)%s", friend.name));
                    return SINGLE_SUCCESS;
                })
        );
    }

    private static class FriendArgumentType implements ArgumentType<Friend> {

        public static FriendArgumentType friend() {
            return new FriendArgumentType();
        }

        @Override
        public Friend parse(StringReader reader) throws CommandSyntaxException {
            return new Friend(reader.readString());
        }

        public static Friend getFriend(CommandContext<?> context, String name) {
            return context.getArgument(name, Friend.class);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return suggestMatching(mc.getNetworkHandler().getPlayerList().stream()
                    .map(entry -> entry.getProfile().getName()).collect(Collectors.toList()), builder);
        }

        @Override
        public Collection<String> getExamples() {
            return Arrays.asList("viceroy", "Stanima68");
        }
    }

}