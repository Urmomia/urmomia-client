/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui.screens.settings;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WIntEdit;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.settings.Setting;
import dev.urmomia.utils.misc.Names;
import net.minecraft.entity.effect.StatusEffect;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatusEffectSettingScreen extends WindowScreen {
    private final Setting<Object2IntMap<StatusEffect>> setting;
    private final WTextBox filter;

    private String filterText = "";

    private WTable table;

    public StatusEffectSettingScreen(GuiTheme theme, Setting<Object2IntMap<StatusEffect>> setting) {
        super(theme, "Select potions");

        this.setting = setting;

        // Filter
        filter = add(theme.textBox("")).minWidth(400).expandX().widget();
        filter.setFocused(true);
        filter.action = () -> {
            filterText = filter.get().trim();

            table.clear();
            initWidgets();
        };

        table = add(theme.table()).expandX().widget();

        initWidgets();
    }

    private void initWidgets() {
        List<StatusEffect> statusEffects = new ArrayList<>(setting.get().keySet());
        statusEffects.sort(Comparator.comparing(Names::get));

        for (StatusEffect statusEffect : statusEffects) {
            String name = Names.get(statusEffect);
            if (!StringUtils.containsIgnoreCase(name, filterText)) continue;

            table.add(theme.label(name)).expandCellX();

            WIntEdit level = theme.intEdit(setting.get().getInt(statusEffect), 0, 0);
            level.hasSlider = false;
            level.action = () -> {
                setting.get().put(statusEffect, level.get());
                setting.changed();
            };

            table.add(level).minWidth(50);
            table.row();
        }
    }
}
