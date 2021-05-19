/*

 * Copyright (c) 2021 Meteor Development.
 */

package dev.urmomia.gui.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.utils.misc.Keybind;

public class WKeybind extends WHorizontalList {
    public Runnable action;
    public Runnable actionOnSet;

    private WLabel label;

    private final Keybind keybind;
    private final int defaultValue;
    private boolean listening;

    public WKeybind(Keybind keybind, int defaultValue) {
        this.keybind = keybind;
        this.defaultValue = defaultValue;
    }

    @Override
    public void init() {
        label = add(theme.label("")).widget();

        WButton set = add(theme.button("Set")).widget();
        set.action = () -> {
            listening = true;
            label.set(appendBindText("..."));

            if (actionOnSet != null) actionOnSet.run();
        };

        WButton reset = add(theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
        reset.action = this::resetBind;

        refreshLabel();
    }

    public boolean onAction(boolean isKey, int value) {
        if (listening && keybind.canBindTo(isKey, value)) {
            keybind.set(isKey, value);
            reset();

            if (action != null) action.run();
            return true;
        }

        return false;
    }

    public void resetBind() {
        keybind.set(true, defaultValue);
        reset();
    }

    public void reset() {
        listening = false;
        refreshLabel();
    }

    private void refreshLabel() {
        label.set(appendBindText(keybind.toString()));
    }

    private String appendBindText(String text) {
        return "Bind: " + text;
    }
}
