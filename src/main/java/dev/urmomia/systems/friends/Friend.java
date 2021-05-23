package dev.urmomia.systems.friends;

import dev.urmomia.utils.misc.ISerializable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public class Friend implements ISerializable<Friend> {
    public String name;


    public Friend(String name) {
        this.name = name;
    }

    public Friend(PlayerEntity player) {
        this(player.getEntityName());
    }

    public Friend(CompoundTag tag) {
        fromTag(tag);
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        return tag;
    }

    @Override
    public Friend fromTag(CompoundTag tag) {
        name = tag.getString("name");
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return Objects.equals(name, friend.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}