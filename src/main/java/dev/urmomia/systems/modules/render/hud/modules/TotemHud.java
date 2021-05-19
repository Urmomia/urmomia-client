/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.render.hud.modules;

import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.HudRenderer;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.render.RenderUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TotemHud extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("Scale of totem counter.")
            .defaultValue(2)
            .min(1)
            .sliderMin(1)
            .sliderMax(4)
            .build()
    );

    public TotemHud(HUD hud) {
        super(hud, "totems", "Displays the amount of totems in your inventory.", false);
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(16 * scale.get(), 16 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        if (isInEditor()) {
            RenderUtils.drawItem(Items.TOTEM_OF_UNDYING.getDefaultStack(), (int) x, (int) y, scale.get(), true);
        } else if (InvUtils.findItemWithCount(Items.TOTEM_OF_UNDYING).count > 0) {
            RenderUtils.drawItem(new ItemStack(Items.TOTEM_OF_UNDYING, InvUtils.findItemWithCount(Items.TOTEM_OF_UNDYING).count), (int) x, (int) y, scale.get(), true);
        }
    }
}
