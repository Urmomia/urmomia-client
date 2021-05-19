package dev.urmomia.systems.modules;

import net.minecraft.util.Identifier;

public class Category {
    public final String name;
    public final Identifier icon;
    private final int nameHash;

    public Category(String name, Identifier icon) {
        this.name = name;
        this.nameHash = name.hashCode();
        this.icon = icon;
    }
    public Category(String name) {
        this(name, null);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return nameHash == category.nameHash;
    }

    @Override
    public int hashCode() {
        return nameHash;
    }
}
