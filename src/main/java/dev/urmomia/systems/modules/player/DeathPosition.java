package dev.urmomia.systems.modules.player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalXZ;
import dev.urmomia.events.game.OpenScreenEvent;
import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.widgets.WLabel;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.waypoints.Waypoint;
import dev.urmomia.systems.waypoints.Waypoints;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.util.math.Vec3d;

public class DeathPosition extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> createWaypoint = sgGeneral.add(new BoolSetting.Builder()
            .name("create-waypoint")
            .description("Creates a waypoint when you die.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> showTimestamp = sgGeneral.add(new BoolSetting.Builder()
            .name("show-timestamp")
            .description("Show timestamp in chat.")
            .defaultValue(true)
            .build()
    );
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final Map<String, Double> deathPos = new HashMap<>();
    private Waypoint waypoint;

    private Vec3d dmgPos;

    private String labelText = "No latest death";

    public DeathPosition() {
        super(Categories.Player, "death-position", "Sends you the coordinates to your latest death.");
    }

    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (event.screen instanceof DeathScreen) onDeath();
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        WHorizontalList list = theme.horizontalList();

        WLabel label = list.add(theme.label(labelText)).expandCellX().widget();

        WButton path = list.add(theme.button("Path")).widget();
        path.action = this::path;

        WButton clear = list.add(theme.button("Clear")).widget();
        clear.action = () -> {
            Waypoints.get().remove(waypoint);
            labelText = "No latest death";

            label.set(labelText);
        };

        return list;
    }

    private void onDeath() {
        if (mc.player == null) return;
        dmgPos = mc.player.getPos();
        deathPos.put("x", dmgPos.x);
        deathPos.put("z", dmgPos.z);
        labelText = String.format("Latest death: %s.", ChatUtils.formatCoords(dmgPos));

        String time = dateFormat.format(new Date());
        info("Died at %s on %s", ChatUtils.formatCoords(dmgPos), (showTimestamp.get() ? String.format(" on %s.", time) : "."));

        // Create waypoint
        if (createWaypoint.get()) {
            waypoint = new Waypoint();
            waypoint.name = "Death " + time;

            waypoint.x = (int) dmgPos.x;
            waypoint.y = (int) dmgPos.y + 2;
            waypoint.z = (int) dmgPos.z;
            waypoint.maxVisibleDistance = Integer.MAX_VALUE;
            waypoint.actualDimension = Utils.getDimension();

            switch (Utils.getDimension()) {
                case Overworld:
                    waypoint.overworld = true;
                    break;
                case Nether:
                    waypoint.nether = true;
                    break;
                case End:
                    waypoint.end = true;
                    break;
            }

            Waypoints.get().add(waypoint);
        }
    }

    private void path() {
        if (deathPos.isEmpty() && mc.player != null) {
            warning("No latest death found.");
        }
        else {
            if (mc.world != null) {
                double x = dmgPos.x, z = dmgPos.z;
                if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
                    BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
                }

                BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ((int) x, (int) z));
            }
        }
    }
}