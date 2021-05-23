package dev.urmomia.systems.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.orbit.EventHandler;
import dev.urmomia.MainClient;
import dev.urmomia.events.packets.PacketEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.gui.GuiThemes;
import dev.urmomia.gui.screens.NotebotHelpScreen;
import dev.urmomia.systems.commands.Command;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.misc.Notebot;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class NotebotCommand extends Command {

    int ticks = -1;
    List<List<Integer>> song = new ArrayList<>();

    public NotebotCommand() {
        super("notebot", "Allows you load notebot files");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("help").executes(ctx -> {
            MainClient.INSTANCE.screenToOpen = new NotebotHelpScreen(GuiThemes.get());
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("status").executes(ctx -> {
            Notebot notebot = Modules.get().get(Notebot.class);
            notebot.printStatus();
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("pause").executes(ctx -> {
            Notebot notebot = Modules.get().get(Notebot.class);
            notebot.Pause();
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("resume").executes(ctx -> {
            Notebot notebot = Modules.get().get(Notebot.class);
            notebot.Pause();
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("stop").executes(ctx -> {
            Notebot notebot = Modules.get().get(Notebot.class);
            notebot.Stop();
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("play").then(argument("name", StringArgumentType.greedyString()).executes(ctx -> {
            Notebot notebot = Modules.get().get(Notebot.class);
            String name = ctx.getArgument("name", String.class);
            if (name == null || name.equals("")) {
                error("Invalid name");
            }
            Path path = MainClient.FOLDER.toPath().resolve(String.format("notebot/%s.txt",name));
            if (!path.toFile().exists()) {
                path = MainClient.FOLDER.toPath().resolve(String.format("notebot/%s.nbs",name));
            }
            notebot.loadSong(path.toFile());
            return SINGLE_SUCCESS;
        })));
        builder.then(literal("preview").then(argument("name", StringArgumentType.greedyString()).executes(ctx -> {
            Notebot notebot = Modules.get().get(Notebot.class);
            String name = ctx.getArgument("name", String.class);
            if (name == null || name == "") {
                error("Invalid name");
            }
            Path path = MainClient.FOLDER.toPath().resolve(String.format("notebot/%s.txt",name));
            if (!path.toFile().exists()) {
                path = MainClient.FOLDER.toPath().resolve(String.format("notebot/%s.nbs",name));
            }
            notebot.previewSong(path.toFile());
            return  SINGLE_SUCCESS;
        })));
        builder.then(literal("record").then(literal("start").executes(ctx -> {
            ticks = -1;
            song.clear();
            MainClient.EVENT_BUS.subscribe(this);
            info("Recording started");
            return  SINGLE_SUCCESS;
        })));
        builder.then(literal("record").then(literal("cancel").executes(ctx -> {
            MainClient.EVENT_BUS.unsubscribe(this);
            info("Recording cancelled");
            return  SINGLE_SUCCESS;
        })));
        builder.then(literal("record").then(literal("save").then(argument("name",StringArgumentType.greedyString()).executes(ctx -> {
            String name = ctx.getArgument("name", String.class);
            if (name == null || name == "") {
                error("Invalid name");
            }
            Path path = MainClient.FOLDER.toPath().resolve(String.format("notebot/%s.txt",name));
            saveRecording(path);
            return  SINGLE_SUCCESS;
        }))));
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (ticks==-1) return;
        ticks++;
    }

    @EventHandler
    public void onReadPacket(PacketEvent.Receive event) {
        if (event.packet instanceof PlaySoundS2CPacket) {
            PlaySoundS2CPacket sound = (PlaySoundS2CPacket) event.packet;
            if (sound.getSound() == SoundEvents.BLOCK_NOTE_BLOCK_HARP) {
                if (ticks == -1) ticks = 0;
                song.add(Arrays.asList(ticks,getNote(sound.getPitch())));
            }
        }
    }

    private void saveRecording(Path path) {
        if (song.size()<1) {
            MainClient.EVENT_BUS.unsubscribe(this);
            return;
        }
        try {
            FileWriter file = new FileWriter(path.toFile());
            for (int i = 0; i < song.size()-1; i++) {
                List<Integer> note = song.get(i);
                file.write(String.format("%d:%d\n",
                        note.get(0), note.get(1)
                ));
            }
            List<Integer> note = song.get(song.size()-1);
            file.write(String.format("%d:%d",
                    note.get(0), note.get(1)
            ));
            file.close();
            info(String.format("Song saved. Length: (highlight)%d(default).",note.get(0)));
            MainClient.EVENT_BUS.unsubscribe(this);
        } catch (IOException e) {
            error("Couldn't create the file.");
            MainClient.EVENT_BUS.unsubscribe(this);
        }

    }

    private int getNote(float pitch) {
        for (int n = 0; n < 25; n++) {
            if ((float) Math.pow(2.0D, (n - 12) / 12.0D) - 0.01 < pitch &&
                    (float) Math.pow(2.0D, (n - 12) / 12.0D) + 0.01 > pitch) {
                return n;
            }
        }
        return 0;
    }
}