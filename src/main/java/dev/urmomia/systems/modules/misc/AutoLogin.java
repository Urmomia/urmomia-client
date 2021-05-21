package dev.urmomia.systems.modules.misc;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.game.GameJoinedEvent;
import dev.urmomia.events.game.GameLeftEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.settings.*;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;

public class AutoLogin extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public Setting<String> pass = sgGeneral.add(new StringSetting.Builder()
            .name("password")
            .description("The password of your AuthMe account.")
            .defaultValue("pass123")
            .onChanged(booleanSetting -> oven())
            .build()
    );
    
    private int ticks;
    private int d;
    private int joins;
    private int said = 0;

    private String passw;

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
//pee
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
        said = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for (; d == 1 && !(ticks >= 30);) {
            ticks++;
            if (ticks >= 30) d = 0; break;
        }
        //delay
        if (said == 0 && joins == 1 && d == 0 && ticks >= 30) mc.player.sendChatMessage("/login " + passw); said = 1;
    }

    private void oven() {
        passw = pass.get();
    }
    //pppppppppppppppppppppppppppppppp
}
