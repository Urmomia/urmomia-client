package dev.urmomia.systems.friends;

import dev.urmomia.systems.System;
import dev.urmomia.systems.Systems;
import dev.urmomia.utils.misc.NbtUtils;
import dev.urmomia.utils.render.color.RainbowColors;
import dev.urmomia.utils.render.color.SettingColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Friends extends System<Friends> implements Iterable<Friend> {
    private List<Friend> friends = new ArrayList<>();

    public final SettingColor color = new SettingColor(0, 255, 180);
    public boolean attack = false;

    public Friends() {
        super("friends");
    }

    public static Friends get() {
        return Systems.get(Friends.class);
    }

    @Override
    public void init() {
        RainbowColors.add(color);
    }

    public boolean add(Friend friend) {
        if (friend.name.isEmpty()) return false;

        if (!friends.contains(friend)) {
            friends.add(friend);
            save();

            return true;
        }

        return false;
    }

    public boolean remove(Friend friend) {
        if (friends.remove(friend)) {
            save();
            return true;
        }

        return false;
    }

    public Friend get(String name) {
        for (Friend friend : friends) {
            if (friend.name.equals(name)) {
                return friend;
            }
        }

        return null;
    }

    public Friend get(PlayerEntity player) {
        return get(player.getEntityName());
    }

    public boolean isFriend(PlayerEntity player) {
        return get(player) != null;
    }

    public boolean shouldAttack(PlayerEntity player) {
        Friend friend = get(player);
        return friend != null && !attack;
    }

    public int count() {
        return friends.size();
    }

    @Override
    public @NotNull Iterator<Friend> iterator() {
        return friends.iterator();
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag friendsTag = new ListTag();

        for (Friend friend : friends) friendsTag.add(friend.toTag());
        tag.put("friends", friendsTag);
        tag.put("color", color.toTag());
        tag.putBoolean("attack", attack);

        return tag;
    }

    @Override
    public Friends fromTag(CompoundTag tag) {
        friends = NbtUtils.listFromTag(tag.getList("friends", 10), tag1 -> new Friend((CompoundTag) tag1));
        if (tag.contains("color")) color.fromTag(tag.getCompound("color"));
        attack = tag.contains("attack") && tag.getBoolean("attack");
        return this;
    }
}