package dev.urmomia.systems.modules.render.hud.modules;

import dev.urmomia.systems.config.Config;
import dev.urmomia.systems.modules.render.hud.HUD;

public class WatermarkHud extends DoubleTextHudElement {
    public WatermarkHud(HUD hud) {
        super(hud, "watermark", "Displays Urmomia Client's version.", "Urmomia Client ");
    }

    @Override
    protected String getRight() {
        return Config.version.getOriginalString();
    }
}