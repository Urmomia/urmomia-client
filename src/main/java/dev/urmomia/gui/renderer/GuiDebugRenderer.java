/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui.renderer;

import dev.urmomia.gui.utils.Cell;
import dev.urmomia.gui.widgets.WWidget;
import dev.urmomia.gui.widgets.containers.WContainer;
import dev.urmomia.rendering.DrawMode;
import dev.urmomia.rendering.MeshBuilder;
import dev.urmomia.utils.render.color.Color;
import net.minecraft.client.render.VertexFormats;

public class GuiDebugRenderer {
    private static final Color CELL_COLOR = new Color(25, 225, 25);
    private static final Color WIDGET_COLOR = new Color(25, 25, 225);

    private final MeshBuilder mb = new MeshBuilder();

    public void render(WWidget widget) {
        if (widget == null) return;

        mb.begin(null, DrawMode.Lines, VertexFormats.POSITION_COLOR);
        renderWidget(widget);
        mb.end();
    }

    private void renderWidget(WWidget widget) {
        lineBox(widget.x, widget.y, widget.width, widget.height, WIDGET_COLOR);

        if (widget instanceof WContainer) {
            for (Cell<?> cell : ((WContainer) widget).cells) {
                lineBox(cell.x, cell.y, cell.width, cell.height, CELL_COLOR);
                renderWidget(cell.widget());
            }
        }
    }

    private void lineBox(double x, double y, double width, double height, Color color) {
        line(x, y, x + width, y, color);
        line(x + width, y, x + width, y + height, color);
        line(x, y, x, y + height, color);
        line(x, y + height, x + width, y + height, color);
    }

    private void line(double x1, double y1, double x2, double y2, Color color) {
        mb.pos(x1, y1, 0).color(color).endVertex();
        mb.pos(x2, y2, 0).color(color).endVertex();
    }
}
