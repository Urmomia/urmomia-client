/*

 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui.widgets;

import dev.urmomia.utils.render.color.Color;

public abstract class WLabel extends WWidget {
    public Color color;

    protected String text;
    protected boolean title;

    public WLabel(String text, boolean title) {
        this.text = text;
        this.title = title;
    }

    @Override
    protected void onCalculateSize() {
        width = theme.textWidth(text, text.length(), title);
        height = theme.textHeight(title);
    }

    public void set(String text) {
        if (Math.round(theme.textWidth(text, text.length(), title)) != width) invalidate();

        this.text = text;
    }

    public String get() {
        return text;
    }
}
