/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.modules.combat;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import dev.urmomia.events.entity.player.AttackEntityEvent;
import dev.urmomia.settings.BoolSetting;
import dev.urmomia.settings.EntityTypeListSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.systems.modules.Modules;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

public class AntiHit extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> antiFriendHit = sgGeneral.add(new BoolSetting.Builder()
        .name("anti-friend-hit")
        .description("Doesn't allow friends to be attacked.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Object2BooleanMap<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Entities to avoid attacking.")
        .defaultValue(new Object2BooleanOpenHashMap<>(0))
        .onlyAttackable()
        .build()
    );

    public AntiHit() {
        super(Categories.Combat, "anti-hit", "Cancels out attacks on certain entities.");
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onAttackEntity(AttackEntityEvent event) {
        if (antiFriendHit.get() && event.entity instanceof PlayerEntity && !Friends.get().attack((PlayerEntity) event.entity)) event.cancel();
        if (Modules.get().isActive(AntiHit.class) && entities.get().containsKey(event.entity.getType())) event.cancel();
    }
}
