package dev.urmomia.systems.modules.player;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WMinus;
import dev.urmomia.gui.widgets.pressable.WPlus;
import dev.urmomia.settings.*;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.player.Rotations;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntiAFK extends Module {
    public enum SpinMode {
        Server,
        Client
    }

    private final SettingGroup sgActions = settings.createGroup("Actions");
    private final SettingGroup sgMessages = settings.createGroup("Messages");

    // Actions

    private final Setting<Boolean> spin = sgActions.add(new BoolSetting.Builder()
            .name("spin")
            .description("Spins.")
            .defaultValue(true)
            .build()
    );

    private final Setting<SpinMode> spinMode = sgActions.add(new EnumSetting.Builder<SpinMode>()
            .name("spin-mode")
            .description("The method of rotating.")
            .defaultValue(SpinMode.Server)
            .build()
    );

    private final Setting<Integer> spinSpeed = sgActions.add(new IntSetting.Builder()
            .name("spin-speed")
            .description("The speed to spin you.")
            .defaultValue(7)
            .build()
    );

    private final Setting<Double> pitch = sgActions.add(new DoubleSetting.Builder()
            .name("pitch")
            .description("The pitch to set in server mode.")
            .defaultValue(-90)
            .min(-90)
            .max(90)
            .sliderMin(-90)
            .sliderMax(90)
            .build()
    );

    private final Setting<Boolean> jump = sgActions.add(new BoolSetting.Builder()
            .name("jump")
            .description("Jumps.")
            .defaultValue(true)
            .build());

    private final Setting<Boolean> click = sgActions.add(new BoolSetting.Builder()
            .name("click")
            .description("Clicks.")
            .defaultValue(false)
            .build());

    private final Setting<Boolean> disco = sgActions.add(new BoolSetting.Builder()
            .name("disco")
            .description("Sneaks and unsneaks quickly.")
            .defaultValue(false)
            .build());

    private final Setting<Boolean> strafe = sgActions.add(new BoolSetting.Builder()
            .name("strafe")
            .description("Strafe right and left")
            .defaultValue(false)
            .onChanged(aBoolean -> {
                strafeTimer = 0;
                direction = false;

                if (isActive()) {
                    mc.options.keyLeft.setPressed(false);
                    mc.options.keyRight.setPressed(false);
                }
            })
            .build());

    // Messages

    private final Setting<Boolean> sendMessages = sgMessages.add(new BoolSetting.Builder()
            .name("send-messages")
            .description("Sends messages to prevent getting kicked for AFK.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> delay = sgMessages.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay between specified messages in seconds.")
            .defaultValue(2)
            .min(0)
            .sliderMax(20)
            .build()
    );

    private final Setting<Boolean> randomMessage = sgMessages.add(new BoolSetting.Builder()
            .name("random")
            .description("Selects a random message from your message list.")
            .defaultValue(false)
            .build()
    );

    private final List<String> messages = new ArrayList<>();
    private int timer;
    private int messageI;
    private int strafeTimer = 0;
    private boolean direction = false;

    private final Random random = new Random();

    private float prevYaw;

    public AntiAFK() {
        super(Categories.Player, "anti-afk", "Performs different actions to prevent getting kicked for AFK reasons.");
    }

    @Override
    public void onActivate() {
        prevYaw = mc.player.yaw;
        timer = delay.get() * 20;
    }

    @Override
    public void onDeactivate() {
        if (strafe.get()) {
            mc.options.keyLeft.setPressed(false);
            mc.options.keyRight.setPressed(false);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (Utils.canUpdate()) {
            //Spin
            if (spin.get()) {
                prevYaw += spinSpeed.get();
                switch (spinMode.get()) {
                    case Client:
                        mc.player.yaw = prevYaw;
                        break;
                    case Server:
                        Rotations.rotate(prevYaw, pitch.get(), -15, null);
                        break;
                }
            }

            //Jump
            if (jump.get() && mc.options.keyJump.isPressed()) mc.options.keyJump.setPressed(false);
            if (jump.get() && mc.options.keySneak.isPressed()) mc.options.keySneak.setPressed(false);
            else if (jump.get() && random.nextInt(99) + 1 == 50) mc.options.keyJump.setPressed(true);

            //Click
            if (click.get() && random.nextInt(99) + 1 == 45) {
                mc.options.keyAttack.setPressed(true);
                Utils.leftClick();
                mc.options.keyAttack.setPressed(false);
            }

            //Disco
            if (disco.get() && random.nextInt(24) + 1 == 15) mc.options.keySneak.setPressed(true);

            //Spam
            if (sendMessages.get() && !messages.isEmpty())
                if (timer <= 0) {
                    int i;
                    if (randomMessage.get()) {
                        i = Utils.random(0, messages.size());
                    } else {
                        if (messageI >= messages.size()) messageI = 0;
                        i = messageI++;
                    }

                    mc.player.sendChatMessage(messages.get(i));

                    timer = delay.get() * 20;
                } else {
                    timer--;
                }

            //Strafe
            if (strafe.get() && strafeTimer == 20) {
                mc.options.keyLeft.setPressed(!direction);
                mc.options.keyRight.setPressed(direction);
                direction = !direction;
                strafeTimer = 0;
            } else
                strafeTimer++;
        }
    }

    @Override
    public WWidget getWidget(GuiTheme theme) {
        messages.removeIf(String::isEmpty);

        WTable table = theme.table();
        fillTable(theme, table);

        return table;
    }

    private void fillTable(GuiTheme theme, WTable table) {
        table.add(theme.horizontalSeparator("Message List")).expandX();

        // Messages
        for (int i = 0; i < messages.size(); i++) {
            int msgI = i;
            String message = messages.get(i);

            WTextBox textBox = table.add(theme.textBox(message)).minWidth(100).expandX().widget();
            textBox.action = () -> messages.set(msgI, textBox.get());

            WMinus delete = table.add(theme.minus()).widget();
            delete.action = () -> {
                messages.remove(msgI);

                table.clear();
                fillTable(theme, table);
            };

            table.row();
        }

        // New Message
        WPlus add = table.add(theme.plus()).expandCellX().right().widget();
        add.action = () -> {
            messages.add("");

            table.clear();
            fillTable(theme, table);
        };
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = super.toTag();

        messages.removeIf(String::isEmpty);
        ListTag messagesTag = new ListTag();

        for (String message : messages) messagesTag.add(StringTag.of(message));
        tag.put("messages", messagesTag);

        return tag;
    }

    @Override
    public Module fromTag(CompoundTag tag) {
        messages.clear();

        if (tag.contains("messages")) {
            ListTag messagesTag = tag.getList("messages", 8);
            for (Tag messageTag : messagesTag) messages.add(messageTag.asString());
        } else {
            messages.add("uwu");
        }

        return super.fromTag(tag);
    }
}