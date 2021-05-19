package dev.urmomia.events.entity;

import net.minecraft.item.ItemStack;

public class DropItemsEvent {
    private static final DropItemsEvent INSTANCE = new DropItemsEvent();

    public ItemStack itemStack;

    public static DropItemsEvent get(ItemStack itemStack) {
        INSTANCE.itemStack = itemStack;
        return INSTANCE;
    }

}
