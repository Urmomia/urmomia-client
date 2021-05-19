package dev.urmomia.systems.modules.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.StringSetting;
import dev.urmomia.systems.config.Config;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.Utils;

public class DiscordPresence extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> line1 = sgGeneral.add(new StringSetting.Builder()
            .name("line-1")
            .description("The text it displays on line 1 of the RPC.")
            .defaultValue("{player} // {server}")
            .onChanged(booleanSetting -> updateDetails())
            .build()
    );

    private final Setting<String> line2 = sgGeneral.add(new StringSetting.Builder()
            .name("line-2")
            .description("The text it displays on line 2 of the RPC.")
            .defaultValue("urmomia strong, urmomia client {version}")
            .onChanged(booleanSetting -> updateDetails())
            .build()
    );

    /*private final Setting<DiscordPresence.Smode> microwave = sgGeneral.add(new EnumSetting.Builder<DiscordPresence.Smode>()
            .name("small-icon-set")
            .description("Changes the RPC Small Image icons.")
            .defaultValue(Smode.Urmomia)
            .onChanged(booleanSetting -> updateDetails())
            .build()
    );*/

    public DiscordPresence() {
        super(Categories.Misc, "discord-presence", "Displays a RPC for you on Discord to show that you're playing Urmomia Client");
    }

    private static final DiscordRichPresence rpc = new DiscordRichPresence();
    private static final DiscordRPC instance = DiscordRPC.INSTANCE;
    private SmallImage currentSmallImage;
    private int ticks;

    @Override
    public void onActivate() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        instance.Discord_Initialize("812502455853383731", handlers, true, null);

        rpc.startTimestamp = System.currentTimeMillis() / 1000L;
        rpc.largeImageKey = "urmomiac";
        String largeText = "Urmomia Client " + Config.version.getOriginalString();
        rpc.largeImageText = largeText;
        currentSmallImage = SmallImage.Urmomia2;
        updateDetails();

        instance.Discord_UpdatePresence(rpc);
        instance.Discord_RunCallbacks();
    }

    @Override
    public void onDeactivate() {
        instance.Discord_ClearPresence();
        instance.Discord_Shutdown();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!Utils.canUpdate()) return;
        ticks++;

        if (ticks >= 200) {
            currentSmallImage = currentSmallImage.next();
            currentSmallImage.apply();
            instance.Discord_UpdatePresence(rpc);

            ticks = 0;
        }

        updateDetails();
        instance.Discord_RunCallbacks();
    }

    private String getLine(Setting<String> line) {
        if (line.get().length() > 0) return line.get().replace("{player}", getName()).replace("{server}", getServer()).replace("{version}", getVersion());
        else return null;
    }

    private String getServer(){
        if (mc.isInSingleplayer()) return "Singleplayer";
        else return Utils.getWorldName();
    }

    private String getName(){
        return mc.player.getGameProfile().getName();
    }

    private String getVersion(){
        return Config.version.getOriginalString();
    }

    private void updateDetails() {
        if (isActive() && Utils.canUpdate()) {
            rpc.details = getLine(line1);
            rpc.state = getLine(line2);
            instance.Discord_UpdatePresence(rpc);
        }
    }

    private enum SmallImage {
        Urmomia2("rpc2-a", "honsda"),
        Urmomia3("rpc1-b", "Codex1729"),
        Urmomia4("rpc2-b", "ChompChompDead"),
        Urmomia5("rpc2-c", "ProfKambing"),
        Default("urmomiac", "Urmomia Client");

        private final String key, text;

        SmallImage(String key, String text) {
            this.key = key;
            this.text = text;
        }

        void apply() {
            rpc.smallImageKey = key;
            rpc.smallImageText = text;
        }

        SmallImage next() {
                if (this == Urmomia2) return Urmomia3;
                if (this == Urmomia3) return Urmomia4;
                if (this == Urmomia4) return Urmomia5;
                if (this == Urmomia5) return Urmomia2;
                else return Default;
        }
    }

    public enum Smode {
        Urmomia,
        Sowl,
        NekoHax
    }
}
