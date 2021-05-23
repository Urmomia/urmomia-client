/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.mixin;

import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.world.AutoSteal;
import dev.urmomia.utils.render.ThemeButtonWidget;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxScreen.class)
public abstract class ShulkerBoxScreenMixin extends HandledScreen<ShulkerBoxScreenHandler> {
    public ShulkerBoxScreenMixin(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        AutoSteal autoSteal = Modules.get().get(AutoSteal.class);

        if (autoSteal.isActive() && autoSteal.getStealButtonEnabled())
            addButton(new ThemeButtonWidget(x + backgroundWidth - 88, y + 3, 40, 12, new LiteralText("Steal"), button -> steal(handler)));
        if (autoSteal.isActive() && autoSteal.getDumpButtonEnabled())
            addButton(new ThemeButtonWidget(x + backgroundWidth - 46, y + 3, 40, 12, new LiteralText("Dump"), button -> dump(handler)));

        if (autoSteal.isActive() && autoSteal.getAutoStealEnabled()) steal(handler);
        else if (autoSteal.isActive() && autoSteal.getAutoDumpEnabled()) dump(handler);
    }

    private void steal(ShulkerBoxScreenHandler handler) {
        Modules.get().get(AutoSteal.class).stealAsync(handler);
    }

    private void dump(ShulkerBoxScreenHandler handler) {
        Modules.get().get(AutoSteal.class).dumpAsync(handler);
    }
}
