/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.systems.profiles;

import dev.urmomia.systems.System;
import dev.urmomia.systems.accounts.Accounts;
import dev.urmomia.systems.config.Config;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.macros.Macros;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.waypoints.Waypoints;
import dev.urmomia.utils.misc.ISerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Profile implements ISerializable<Profile> {

    public String name;
    public boolean onLaunch = false;
    public final List<String> loadOnJoinIps = new ArrayList<>();
    public boolean accounts = false, config = true, friends = false, macros = true, modules = true, waypoints = false;

    public void load(System<?> system) {
        File folder = new File(Profiles.FOLDER, name);
        system.load(folder);
    }

    public void load() {
        File folder = new File(Profiles.FOLDER, name);

        if (accounts) Accounts.get().load(folder);
        if (config) Config.get().load(folder);
        if (friends) Friends.get().load(folder);
        if (macros) Macros.get().load(folder);
        if (modules) Modules.get().load(folder);
        if (waypoints) Waypoints.get().load(folder);
    }

    public void save(System<?> system) {
        File folder = new File(Profiles.FOLDER, name);
        system.save(folder);
    }

    public void save() {
        File folder = new File(Profiles.FOLDER, name);

        if (accounts) Accounts.get().save(folder);
        if (config) Config.get().save(folder);
        if (friends) Friends.get().save(folder);
        if (macros) Macros.get().save(folder);
        if (modules) Modules.get().save(folder);
        if (waypoints) Waypoints.get().save(folder);
    }

    public void delete(System<?> system) {
        File file = new File(new File(Profiles.FOLDER, name), system.getFile().getName());
        file.delete();
    }

    public void delete() {
        try {
            FileUtils.deleteDirectory(new File(Profiles.FOLDER, name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putString("name", name);
        tag.putBoolean("onLaunch", onLaunch);

        tag.putBoolean("accounts", accounts);
        tag.putBoolean("config", config);
        tag.putBoolean("friends", friends);
        tag.putBoolean("macros", macros);
        tag.putBoolean("modules", modules);
        tag.putBoolean("waypoints", waypoints);

        loadOnJoinIps.removeIf(String::isEmpty);

        ListTag ipsTag = new ListTag();
        for (String ip : loadOnJoinIps) ipsTag.add(StringTag.of(ip));
        tag.put("loadOnJoinIps", ipsTag);

        return tag;
    }

    @Override
    public Profile fromTag(CompoundTag tag) {
        name = tag.getString("name");
        onLaunch = tag.contains("onLaunch") && tag.getBoolean("onLaunch");

        accounts = tag.contains("accounts") && tag.getBoolean("accounts");
        config = tag.contains("config") && tag.getBoolean("config");
        friends = tag.contains("friends") && tag.getBoolean("friends");
        macros = tag.contains("macros") && tag.getBoolean("macros");
        modules = tag.contains("modules") && tag.getBoolean("modules");
        waypoints = tag.contains("waypoints") && tag.getBoolean("waypoints");

        loadOnJoinIps.clear();

        if (tag.contains("loadOnJoinIps")) {
            ListTag ipsTag = tag.getList("loadOnJoinIps", 8);
            for (Tag ip : ipsTag) loadOnJoinIps.add(ip.asString());
        }

        return this;
    }

}