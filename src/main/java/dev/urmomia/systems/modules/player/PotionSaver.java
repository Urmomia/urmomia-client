/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.player;

import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.player.PlayerUtils;

public class PotionSaver extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<Boolean> onlyWhenStationary = sgGeneral.add(new BoolSetting.Builder()
            .name("only-when-stationary")
            .description("Only freezes effects when you aren't moving.")
            .defaultValue(true)
            .build()
    );

    public PotionSaver() {
        super(Categories.Player, "potion-saver", "Stops potion effects ticking when you stand still.");
    }

    public boolean shouldFreeze() {
        if (!Utils.canUpdate()) return false;
        return isActive() && ((onlyWhenStationary.get() && !PlayerUtils.isMoving()) || !onlyWhenStationary.get())  && !mc.player.getStatusEffects().isEmpty();
    }

}
