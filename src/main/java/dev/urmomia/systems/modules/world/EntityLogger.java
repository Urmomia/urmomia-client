package dev.urmomia.systems.modules.world;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.entity.EntityAddedEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.EntityTypeListSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.player.ChatUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

public class EntityLogger extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
            .name("entites")
            .description("Select specific entities.")
            .defaultValue(new Object2BooleanOpenHashMap<>(0))
            .build()
    );

    private final Setting<Boolean> playerNames = sgGeneral.add(new BoolSetting.Builder()
            .name("player-names")
            .description("Shows the player's name.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> friends = sgGeneral.add(new BoolSetting.Builder()
            .name("friends")
            .description("Logs friends.")
            .defaultValue(true)
            .build()
    );

    public EntityLogger() {
        super(Categories.World, "entity-logger", "Sends a client-side chat alert if a specified entity appears in render distance.");
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity.getUuid().equals(mc.player.getUuid())) return;

        if (entities.get().getBoolean(event.entity.getType())) {
            if (event.entity instanceof PlayerEntity) {
                if (!friends.get() && Friends.get().get((PlayerEntity) event.entity) != null) return;
            }

            String name;
            if (playerNames.get() && event.entity instanceof PlayerEntity) name = ((PlayerEntity) event.entity).getGameProfile().getName() + " (Player)";
            else name = event.entity.getType().getName().getString();

            info("(highlight)%s(default) has spawned at %s.", name, ChatUtils.formatCoords(event.entity.getPos()));
        }
    }
}