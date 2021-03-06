package dev.urmomia.systems.modules.render;

import dev.urmomia.events.render.RenderEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.ColorSetting;
import dev.urmomia.settings.EntityTypeListSetting;
import dev.urmomia.settings.EnumSetting;
import dev.urmomia.settings.IntSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.entity.EntityUtils;
import dev.urmomia.utils.entity.Target;
import dev.urmomia.utils.player.PlayerUtils;
import dev.urmomia.utils.render.RenderUtils;
import dev.urmomia.utils.render.color.Color;
import dev.urmomia.utils.render.color.SettingColor;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

public class Tracers extends Module {
        private final SettingGroup sgGeneral = settings.getDefaultGroup();
        private final SettingGroup sgAppearance = settings.createGroup("Appearance");
        private final SettingGroup sgColors = settings.createGroup("Colors");
    
        // General
    
        private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
                .name("entites")
                .description("Select specific entities.")
                .defaultValue(Utils.asObject2BooleanOpenHashMap(EntityType.PLAYER))
                .build()
        );
    
        private final Setting<Target> target = sgAppearance.add(new EnumSetting.Builder<Target>()
                .name("target")
                .description("What part of the entity to target.")
                .defaultValue(Target.Body)
                .build()
        );
    
        private final Setting<Boolean> stem = sgAppearance.add(new BoolSetting.Builder()
                .name("stem")
                .description("Draw a line through the center of the tracer target.")
                .defaultValue(true)
                .build()
        );
    
        private final Setting<Integer> maxDist = sgAppearance.add(new IntSetting.Builder()
                .name("max-distance")
                .description("Maximum distance for tracers to show.")
                .defaultValue(256)
                .min(0)
                .sliderMax(256)
                .build()
        );
    
        public final Setting<Boolean> showInvis = sgGeneral.add(new BoolSetting.Builder()
                .name("show-invisible")
                .description("Shows invisibile entities.")
                .defaultValue(true)
                .build()
        );
    
        // Colors
    
        public final Setting<Boolean> distance = sgColors.add(new BoolSetting.Builder()
                .name("distance-colors")
                .description("Changes the color of tracers depending on distance.")
                .defaultValue(false)
                .build()
        );
    
        private final Setting<SettingColor> playersColor = sgColors.add(new ColorSetting.Builder()
                .name("players-colors")
                .description("The player's color.")
                .defaultValue(new SettingColor(205, 205, 205, 127))
                .visible(() -> !distance.get())
                .build()
        );
    
        private final Setting<SettingColor> animalsColor = sgColors.add(new ColorSetting.Builder()
                .name("animals-color")
                .description("The animal's color.")
                .defaultValue(new SettingColor(145, 255, 145, 127))
                .visible(() -> !distance.get())
                .build()
        );
    
        private final Setting<SettingColor> waterAnimalsColor = sgColors.add(new ColorSetting.Builder()
                .name("water-animals-color")
                .description("The water animal's color.")
                .defaultValue(new SettingColor(145, 145, 255, 127))
                .visible(() -> !distance.get())
                .build()
        );
    
        private final Setting<SettingColor> monstersColor = sgColors.add(new ColorSetting.Builder()
                .name("monsters-color")
                .description("The monster's color.")
                .defaultValue(new SettingColor(255, 145, 145, 127))
                .visible(() -> !distance.get())
                .build()
        );
    
        private final Setting<SettingColor> ambientColor = sgColors.add(new ColorSetting.Builder()
                .name("ambient-color")
                .description("The ambient color.")
                .defaultValue(new SettingColor(75, 75, 75, 127))
                .visible(() -> !distance.get())
                .build()
        );
    
        private final Setting<SettingColor> miscColor = sgColors.add(new ColorSetting.Builder()
                .name("misc-color")
                .description("The misc color.")
                .defaultValue(new SettingColor(145, 145, 145, 127))
                .visible(() -> !distance.get())
                .build()
        );
    
        private int count;
        private final Color distanceColor = new Color(255, 255, 255);
    
        public Tracers() {
            super(Categories.Render, "tracers", "Displays tracer lines to specified entities.");
        }
    
        @EventHandler
        private void onRender(RenderEvent event) {
            count = 0;
            for (Entity entity : mc.world.getEntities()) {
                if (mc.player.distanceTo(entity) > maxDist.get()
                        || (!Modules.get().isActive(Freecam.class) && entity == mc.player)
                        || !entities.get().getBoolean(entity.getType())
                        || (!showInvis.get() && entity.isInvisible())
                        || !EntityUtils.isInRenderDistance(entity)
                ) continue;
    
                Color color;
    
                if (distance.get() && entity instanceof PlayerEntity) {
                    color = getColorFromDistance((PlayerEntity) entity);
                } else if (entity instanceof PlayerEntity) {
                    color = PlayerUtils.getPlayerColor(((PlayerEntity) entity), playersColor.get());
                } else {
                    switch (entity.getType().getSpawnGroup()) {
                        case CREATURE:          color = animalsColor.get(); break;
                        case WATER_AMBIENT:
                        case WATER_CREATURE:    color = waterAnimalsColor.get(); break;
                        case MONSTER:           color = monstersColor.get(); break;
                        case AMBIENT:           color = ambientColor.get(); break;
                        default:                color = miscColor.get(); break;
                    }
                }
    
                RenderUtils.drawTracerToEntity(event, entity, color, target.get(), stem.get());
                count++;
            }
        }
    
        private Color getColorFromDistance(PlayerEntity player) {
            //Credit to Icy from Stackoverflow
            double distance = mc.player.distanceTo(player);
            double percent = distance / 60;
    
            if (percent < 0 || percent > 1) {
                distanceColor.set(0, 255, 0, 255);
                return distanceColor;
            }
    
            int r, g;
    
            if (percent < 0.5) {
                r = 255;
                g = (int) (255 * percent / 0.5);  //closer to 0.5, closer to yellow (255,255,0)
            }
            else {
                g = 255;
                r = 255 - (int) (255 * (percent - 0.5) / 0.5); //closer to 1.0, closer to green (0,255,0)
            }
    
            distanceColor.set(r, g, 0, 255);
            return distanceColor;
        }
    
    
        @Override
        public String getInfoString() {
            return Integer.toString(count);
        }
    }