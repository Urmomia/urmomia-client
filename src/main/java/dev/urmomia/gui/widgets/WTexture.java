/*

 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import net.minecraft.client.texture.AbstractTexture;

public class WTexture extends WWidget {
    private final double width, height;
    private final double rotation;
    private final AbstractTexture texture;

    public WTexture(double width, double height, double rotation, AbstractTexture texture) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.texture = texture;
    }

    @Override
    protected void onCalculateSize() {
        super.width = theme.scale(width);
        super.height = theme.scale(height);
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        renderer.texture(x, y, super.width, super.height, rotation, texture);
    }
}
