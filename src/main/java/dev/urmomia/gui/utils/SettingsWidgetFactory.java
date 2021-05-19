/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui.utils;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.settings.Settings;

public interface SettingsWidgetFactory {
    WWidget create(GuiTheme theme, Settings settings, String filter);
}
