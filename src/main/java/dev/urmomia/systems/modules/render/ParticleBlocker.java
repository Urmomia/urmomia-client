package dev.urmomia.systems.modules.render;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.ParticleEvent;
import dev.urmomia.settings.ParticleTypeListSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import net.minecraft.particle.ParticleType;

import java.util.ArrayList;
import java.util.List;

public class ParticleBlocker extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<ParticleType<?>>> particles = sgGeneral.add(new ParticleTypeListSetting.Builder()
            .name("particles")
            .description("Particles to block.")
            .defaultValue(new ArrayList<>(0))
            .build()
    );

    public ParticleBlocker() {
        super(Categories.Render, "particle-blocker", "Stops specified particles from rendering.");
    }

    @EventHandler
    private void onRenderParticle(ParticleEvent event) {
        if (event.particle != null && particles.get().contains(event.particle)) event.cancel();
    }
}
