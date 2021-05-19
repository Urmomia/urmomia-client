/*

 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui.widgets;

public class WVerticalSeparator extends WWidget {
    @Override
    protected void onCalculateSize() {
        width = theme.scale(3);
        height = 1;
    }
}
