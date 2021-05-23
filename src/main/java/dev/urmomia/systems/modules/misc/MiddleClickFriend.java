package dev.urmomia.systems.modules.misc;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.meteor.MouseButtonEvent;
import dev.urmomia.systems.friends.Friend;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.misc.input.KeyAction;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.settings.StringSetting;
import net.minecraft.entity.player.PlayerEntity;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriend extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> message = sgGeneral.add(new BoolSetting.Builder()
            .name("message")
            .description("Sends a message to the player when you add them as a friend.")
            .defaultValue(false)
            .build()
    );

    private final Setting<String> h = sgGeneral.add(new StringSetting.Builder()
            .name("message")
            .description("The specified message sent to the player.")
            .defaultValue("i added you as my friend!")
            .build()
    );


    public MiddleClickFriend() {
        super(Categories.Misc, "middle-click-friend", "Adds or removes a player as a friend via middle click.");
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (event.action == KeyAction.Press && event.button == GLFW_MOUSE_BUTTON_MIDDLE && mc.currentScreen == null && mc.targetedEntity != null && mc.targetedEntity instanceof PlayerEntity) {
            if (!Friends.get().isFriend((PlayerEntity) mc.targetedEntity)) {
                Friends.get().add(new Friend((PlayerEntity) mc.targetedEntity));
                if (message.get()) mc.player.sendChatMessage("/msg " + mc.targetedEntity.getEntityName() + " " + h.get());
            } else {
                Friends.get().remove(Friends.get().get((PlayerEntity) mc.targetedEntity));
            }
        }
    }
}
