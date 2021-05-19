package dev.urmomia.systems.config;

import com.g00fy2.versioncompare.Version;

import dev.urmomia.gui.tabs.builtin.ConfigTab;
import dev.urmomia.settings.Setting;
import dev.urmomia.systems.System;
import dev.urmomia.systems.Systems;
import dev.urmomia.utils.render.color.RainbowColors;
//import net.fabricmc.loader.api.FabricLoader; not used for now?
import net.minecraft.nbt.CompoundTag;

public class Config extends System<Config> {
    public final static Version version = new Version("m0.7.3");
    public String devBuild;
    public String prefix = ConfigTab.prefix.get();
    public boolean chatCommandsInfo = ConfigTab.chatCommandsInfo.get();
    public boolean deleteChatCommandsInfo = ConfigTab.deleteChatCommandsInfo.get();
    public boolean rainbowPrefix = ConfigTab.rainbowPrefix.get();
    public boolean customFont = ConfigTab.customFont.get();
    public int rotationHoldTicks = ConfigTab.rotationHoldTicks.get();
    public double rainbowPrefixSpeed, rainbowPrefixSpread;

    public boolean titleScreenCredits = ConfigTab.titleScreenCredits.get();

    public boolean windowTitle = false;

    public Config() {
        super("config");

        //let's not
    }

    public static Config get() {
        return Systems.get(Config.class);
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putString("version", version.getOriginalString());
        //tag.putBoolean("sendDataToApi", sendDataToApi);
        tag.putInt("rotationHoldTicks", rotationHoldTicks);
        tag.putString("prefix", prefix);
        tag.putBoolean("chatCommandsInfo", chatCommandsInfo);
        tag.putBoolean("deleteChatCommandsInfo", deleteChatCommandsInfo);
        tag.putBoolean("customFont", customFont);
        tag.putBoolean("rainbowPrefix", rainbowPrefix);
        tag.putDouble("rainbowSpeed", RainbowColors.GLOBAL.getSpeed());
        tag.putBoolean("chatCommandsInfo", chatCommandsInfo);
        tag.putBoolean("deleteChatCommandsInfo", deleteChatCommandsInfo);
        //tag.putBoolean("sendDataToApi", sendDataToApi);
        tag.putBoolean("titleScreenCredits", titleScreenCredits);
        tag.putBoolean("windowTitle", windowTitle);

        return tag;
    }


    @Override
    public Config fromTag(CompoundTag tag) {
        customFont = getBoolean(tag, "customFont", ConfigTab.customFont);
        RainbowColors.GLOBAL.setSpeed(tag.contains("rainbowSpeed") ? tag.getDouble("rainbowSpeed") : ConfigTab.rainbowSpeed.getDefaultValue() / 100);
        //sendDataToApi = getBoolean(tag, "sendDataToApi", ConfigTab.sendDataToApi);
        rotationHoldTicks = getInt(tag, "rotationHoldTicks", ConfigTab.rotationHoldTicks);

        prefix = getString(tag, "prefix", ConfigTab.prefix);
        chatCommandsInfo = getBoolean(tag, "chatCommandsInfo", ConfigTab.chatCommandsInfo);
        deleteChatCommandsInfo = getBoolean(tag, "deleteChatCommandsInfo", ConfigTab.deleteChatCommandsInfo);
        rainbowPrefix = getBoolean(tag, "rainbowPrefix", ConfigTab.rainbowPrefix);

        titleScreenCredits = getBoolean(tag, "titleScreenCredits", ConfigTab.titleScreenCredits);

        return this;
    }

    private boolean getBoolean(CompoundTag tag, String key, Setting<Boolean> setting) {
        return tag.contains(key) ? tag.getBoolean(key) : setting.get();
    }
    private String getString(CompoundTag tag, String key, Setting<String> setting) {
        return tag.contains(key) ? tag.getString(key) : setting.get();
    }
    private double getDouble(CompoundTag tag, String key, Setting<Double> setting) {
        return tag.contains(key) ? tag.getDouble(key) : setting.get();
    }
    private int getInt(CompoundTag tag, String key, Setting<Integer> setting) {
        return tag.contains(key) ? tag.getInt(key) : setting.get();
    }
}
