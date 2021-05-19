/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.profiles;

import meteordevelopment.orbit.EventHandler;
import dev.urmomia.MainClient;
import dev.urmomia.events.game.GameJoinedEvent;
import dev.urmomia.systems.System;
import dev.urmomia.systems.Systems;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.misc.NbtUtils;
import net.minecraft.nbt.CompoundTag;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Profiles extends System<Profiles> implements Iterable<Profile> {

    public static final File FOLDER = new File(MainClient.FOLDER, "profiles");
    private List<Profile> profiles = new ArrayList<>();

    public Profiles() {
        super("profiles");
    }

    public static Profiles get() {
        return Systems.get(Profiles.class);
    }

    public void add(Profile profile) {
        if (!profiles.contains(profile)) profiles.add(profile);
        profile.save();
        save();
    }

    public void remove(Profile profile) {
        if (profiles.remove(profile)) profile.delete();
        save();
    }

    public Profile get(String name) {
        for (Profile profile : this) {
            if (profile.name.equalsIgnoreCase(name)) {
                return profile;
            }
        }

        return null;
    }

    public List<Profile> getAll() {
        return profiles;
    }

    @Override
    public File getFile() {
        return new File(FOLDER, "profiles.nbt");
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("profiles", NbtUtils.listToTag(profiles));
        return tag;
    }

    @Override
    public Profiles fromTag(CompoundTag tag) {
        profiles = NbtUtils.listFromTag(tag.getList("profiles", 10), tag1 -> new Profile().fromTag((CompoundTag) tag1));
        return this;
    }

    @EventHandler
    private void onGameJoined(GameJoinedEvent event) {
        for (Profile profile : this) {
            if (profile.loadOnJoinIps.contains(Utils.getWorldName())) {
                profile.load();
            }
        }
    }

    @Override
    public Iterator<Profile> iterator() {
        return profiles.iterator();
    }
}