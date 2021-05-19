package dev.urmomia.gui.tabs.builtin;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.tabs.Tab;
import dev.urmomia.gui.tabs.TabScreen;
import dev.urmomia.gui.tabs.WindowTabScreen;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.settings.*;
import dev.urmomia.systems.config.Config;
import dev.urmomia.utils.render.color.RainbowColors;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import static dev.urmomia.utils.Utils.mc;

public class ConfigTab extends Tab {

        private static final Settings settings = new Settings();
        private static final SettingGroup sgGeneral = settings.getDefaultGroup();
        private static final SettingGroup sgChat = settings.createGroup("Chat");
        private static final SettingGroup sgScreens = settings.createGroup("Screens");
    
        public static final Setting<Boolean> customFont = sgGeneral.add(new BoolSetting.Builder()
                .name("custom-font")
                .description("Use a custom font.")
                .defaultValue(true)
                .onChanged(aBoolean -> {
                    Config.get().customFont = aBoolean;
                    if (ConfigTab.currentScreen != null) ConfigTab.currentScreen.invalidate();
                })
                .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().customFont))
                .build()
        );
    
        public static final Setting<Double> rainbowSpeed = sgGeneral.add(new DoubleSetting.Builder()
                .name("rainbow-speed")
                .description("The global rainbow speed.")
                .min(0)
                .sliderMax(5)
                .max(10)
                .defaultValue(0.5)
                .decimalPlaces(2)
                .onChanged(value -> RainbowColors.GLOBAL.setSpeed(value / 100))
                .onModuleActivated(setting -> setting.set(RainbowColors.GLOBAL.getSpeed() * 100))
                .build()
        );
        
        public static final Setting<Integer> rotationHoldTicks = sgGeneral.add(new IntSetting.Builder()
                .name("rotation-hold-ticks")
                .description("Hold long to hold server side rotation when not sending any packets.")
                .defaultValue(9)
                .onChanged(integer -> Config.get().rotationHoldTicks = integer)
                .onModuleActivated(integerSetting -> integerSetting.set(Config.get().rotationHoldTicks))
                .build()
        );
    
        public static final Setting<String> prefix = sgChat.add(new StringSetting.Builder()
                .name("prefix")
                .description("Prefix.")
                .defaultValue(".")
                .onChanged(s -> Config.get().prefix = s)
                .onModuleActivated(stringSetting -> stringSetting.set(Config.get().prefix))
                .build()
        );
    
        public static final Setting<Boolean> chatCommandsInfo = sgChat.add(new BoolSetting.Builder()
                .name("chat-commands-info")
                .description("Sends a chat message when you use chat comamnds (eg toggling module, changing a setting, etc).")
                .defaultValue(true)
                .onChanged(aBoolean -> Config.get().chatCommandsInfo = aBoolean)
                .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().chatCommandsInfo))
                .build()
        );
    
        public static final Setting<Boolean> deleteChatCommandsInfo = sgChat.add(new BoolSetting.Builder()
                .name("delete-chat-commands-info")
                .description("Delete previous chat messages.")
                .defaultValue(true)
                .onChanged(aBoolean -> Config.get().deleteChatCommandsInfo = aBoolean)
                .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().deleteChatCommandsInfo))
                .build()
        );
    
        public static final Setting<Boolean> rainbowPrefix = sgChat.add(new BoolSetting.Builder()
                .name("rainbow-prefix")
                .description("Makes the [Meteor] prefix on chat info rainbow.")
                .defaultValue(true)
                .onChanged(aBoolean -> Config.get().rainbowPrefix = aBoolean)
                .onModuleActivated(booleanSetting -> booleanSetting.set(Config.get().rainbowPrefix))
                .build()
        );
    
        public static final Setting<Boolean> titleScreenCredits = sgScreens.add(new BoolSetting.Builder()
                .name("title-screen-credits")
                .description("Show Meteor credits on title screen")
                .defaultValue(true)
                .onChanged(aBool -> Config.get().titleScreenCredits = aBool)
                .onModuleActivated(boolSetting -> boolSetting.set(Config.get().titleScreenCredits))
                .build()
        );
    

    public static ConfigScreen currentScreen;

    public ConfigTab() {
        super("Config");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return currentScreen = new ConfigScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof ConfigScreen;
    }

    public static class ConfigScreen extends WindowTabScreen {
        private static final Identifier LOGO = new Identifier("urmomia-client", "textures/hud/client-logo.png");

        public ConfigScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);

            mc.getTextureManager().bindTexture(LOGO);
            WHorizontalList watermark = add(theme.horizontalList()).pad(4).top().widget();
            watermark.add(theme.texture(256 * 0.2, 256 * 0.2, 0, mc.getTextureManager().getTexture(LOGO)));
            watermark.add(theme.label(Config.version.getOriginalString()));

            settings.onActivated();
            add(theme.settings(settings)).expandX();
        }
    }
}