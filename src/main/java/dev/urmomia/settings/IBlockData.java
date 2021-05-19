/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.settings;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.utils.misc.IChangeable;
import dev.urmomia.utils.misc.ICopyable;
import dev.urmomia.utils.misc.ISerializable;
import net.minecraft.block.Block;

public interface IBlockData<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> {
    WidgetScreen createScreen(GuiTheme theme, Block block, BlockDataSetting<T> setting);
}
