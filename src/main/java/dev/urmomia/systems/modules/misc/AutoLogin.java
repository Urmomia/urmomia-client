package dev.urmomia.systems.modules.misc;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.game.GameJoinedEvent;
import dev.urmomia.events.game.GameLeftEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.StringSetting;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class AutoLogin extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    public final Setting<String> passx = sgGeneral.add(new StringSetting.Builder()
            .name("text")
            .description("The AuthMe Password you want to use.")
            .defaultValue("pass123")
            .onChanged(booleanSetting -> oven())
            .build()
    );
    
    private int ticks;
    private int d;
    private int joins;
    public static int said;

    public static String passw;

    public AutoLogin() {
        super(Categories.Misc, "auto-login", "Automatically login in AuthMe servers when you join.");
    }
    
    @Override
    public void onActivate() {
        ticks = 0;
        d = 2;
        joins = 0;
        said = 0;
    }

    @Override
    public void onDeactivate() {
        ticks = 0;
        d = 2;
        joins = 0;
        said = 0;
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
        joins++;
        if (d == 2) d = 1;
        said = 0;
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        ticks = 0;
        d = 2;
        joins = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {

        for (; d == 1 && !(ticks >= 30);) {
            ticks++;
            if (ticks >= 30) d = 0; said = 0; break;
        }
        //delay
        if (said == 0 && joins == 1 && d == 0 && ticks >= 30) mc.player.sendChatMessage("/login " + passw); said = 1;
    }

    public void oven() {
        passw = passx.get();
    }

    public void floppa(String pass) {
        passx.set(pass);
        oven();
    }
}
