package dev.urmomia.systems.modules.render.hud.modules;

import dev.urmomia.settings.*;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.combat.*;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.HudRenderer;
import dev.urmomia.utils.render.color.SettingColor;

import java.util.ArrayList;
import java.util.List;

public class ModuleInfoHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Module>> modules = sgGeneral.add(new ModuleListSetting.Builder()
            .name("modules")
            .description("Which modules to display")
            .defaultValue(getDefaultModules())
            .build()
    );

    private final Setting<Boolean> info = sgGeneral.add(new BoolSetting.Builder()
            .name("additional-info")
            .description("Shows additional info from the module next to the name in the module info list.")
            .defaultValue(true)
            .build()
    );

    private final Setting<SettingColor> onColor = sgGeneral.add(new ColorSetting.Builder()
            .name("on-color")
            .description("Color when module is on.")
            .defaultValue(new SettingColor(138, 48, 255))
            .build()
    );

    private final Setting<SettingColor> offColor = sgGeneral.add(new ColorSetting.Builder()
            .name("off-color")
            .description("Color when module is off.")
            .defaultValue(new SettingColor(255, 138, 48))
            .build()
    );

    public ModuleInfoHud(HUD hud) {
        super(hud, "module-info", "Displays if selected modules are enabled or disabled.", false);
    }

    @Override
    public void update(HudRenderer renderer) {
        if (Modules.get() == null || modules.get().isEmpty()) {
            box.setSize(renderer.textWidth("Module Info"), renderer.textHeight());
            return;
        }

        double width = 0;
        double height = 0;

        int i = 0;
        for (Module module : modules.get()) {
            width = Math.max(width, getModuleWidth(renderer, module));
            height += renderer.textHeight();
            if (i > 0) height += 2;

            i++;
        }

        box.setSize(width, height);
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (Modules.get() == null || modules.get().isEmpty()) {
            renderer.text("Module Info", x, y, hud.primaryColor.get());
            return;
        }

        for (Module module : modules.get()) {
            renderModule(renderer, module, x + box.alignX(getModuleWidth(renderer, module)), y);

            y += 2 + renderer.textHeight();
        }
    }

    private void renderModule(HudRenderer renderer, Module module, double x, double y) {
        renderer.text(module.title, x, y, hud.primaryColor.get());

        String info = getModuleInfo(module);
        renderer.text(info,x + renderer.textWidth(module.title) + renderer.textWidth(" "), y, module.isActive() ? onColor.get() : offColor.get());
    }

    private double getModuleWidth(HudRenderer renderer, Module module) {
        double width = renderer.textWidth(module.title);
        if (info.get()) width += renderer.textWidth(" ") + renderer.textWidth(getModuleInfo(module));
        return width;
    }

    private String getModuleInfo(Module module) {
        if (module.getInfoString() != null && module.isActive() && info.get()) return module.getInfoString();
        else if (module.isActive()) return "ON";
        else return "OFF";
    }

    private static List<Module> getDefaultModules() {
        List<Module> modules = new ArrayList<>(6);
        modules.add(Modules.get().get(KillAura.class));
        modules.add(Modules.get().get(CrystalAura.class));
        modules.add(Modules.get().get(AnchorAura.class));
        modules.add(Modules.get().get(Burrow.class));
        modules.add(Modules.get().get(BedAura.class));
        modules.add(Modules.get().get(Surround.class));
        return modules;
    }
}
