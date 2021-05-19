/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.render;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.MainClient;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import net.minecraft.client.MinecraftClient;

public class Fullbright extends Module {
    public Fullbright() {
        super(Categories.Render, "fullbright", "Lights up your world!");

        MainClient.EVENT_BUS.subscribe(StaticListener.class);
    }

    @Override
    public void onActivate() {
        enable();
    }

    @Override
    public void onDeactivate() {
        disable();
    }

    public static void enable() {
        StaticListener.timesEnabled++;
    }

    public static void disable() {
        StaticListener.timesEnabled--;
    }

    private static class StaticListener {
        private static final MinecraftClient mc = MinecraftClient.getInstance();

        private static int timesEnabled;
        private static int lastTimesEnabled;

        private static double prevGamma;

        @EventHandler
        private static void onTick(TickEvent.Post event) {
            if (timesEnabled > 0 && lastTimesEnabled == 0) {
                prevGamma = mc.options.gamma;
            }
            else if (timesEnabled == 0 && lastTimesEnabled > 0) {
                mc.options.gamma = prevGamma == 16 ? 1 : prevGamma;
            }

            if (timesEnabled > 0) {
                mc.options.gamma = 16;
            }

            lastTimesEnabled = timesEnabled;
        }
    }
}
