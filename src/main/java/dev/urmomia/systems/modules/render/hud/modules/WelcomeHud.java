package dev.urmomia.systems.modules.render.hud.modules;

import dev.urmomia.settings.ColorSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.StringSetting;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.player.NameProtect;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.HudRenderer;
import dev.urmomia.utils.render.color.Color;
import dev.urmomia.utils.render.color.SettingColor;

public class WelcomeHud extends HudElement {
    protected Color rightColor;
    protected boolean visible = true;

    private String right;

    private double leftWidth;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
            .name("color")
            .description("Color of welcome text.")
            .defaultValue(new SettingColor(255, 255, 255))
            .build()
    );

    private Setting<String> d = sgGeneral.add(new StringSetting.Builder()
            .name("welcome-text")
            .description("The welcome text you want it to display.")
            .defaultValue("Welcome to Urmomia Client")
            .onChanged(booleanSetting -> oven())
            .build()
    );

    private String left;

    public WelcomeHud(HUD hud) {
        super(hud, "welcome", "Displays a welcome message.");
        rightColor = color.get();
    }

    @Override
    public void update(HudRenderer renderer) {
        oven();
        right = Modules.get().get(NameProtect.class).getName(mc.getSession().getUsername()) + ".";
        leftWidth = renderer.textWidth(left);

        box.setSize(leftWidth + renderer.textWidth(right), renderer.textHeight());
    }

    @Override
    public void render(HudRenderer renderer) {
        if (!visible) return;

        double x = box.getX();
        double y = box.getY();

        renderer.text(left + ", ", x, y, hud.primaryColor.get());
        renderer.text(right, x + leftWidth, y, rightColor);
    }

    private void oven() {
        left = (d.get()) + ", ";
    }
}
