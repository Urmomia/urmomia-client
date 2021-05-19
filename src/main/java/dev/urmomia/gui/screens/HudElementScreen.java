package dev.urmomia.gui.screens;

import dev.urmomia.events.render.Render2DEvent;
import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.gui.widgets.pressable.WCheckbox;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.modules.HudElement;

import static dev.urmomia.utils.Utils.getWindowWidth;

public class HudElementScreen extends WindowScreen {
    public HudElementScreen(GuiTheme theme, HudElement element) {
        super(theme, element.title);

        // Description
        add(theme.label(element.description, getWindowWidth() / 2.0));

        // Settings
        if (element.settings.sizeGroups() > 0) {
            add(theme.settings(element.settings)).expandX();

            add(theme.horizontalSeparator()).expandX();
        }

        // Bottom
        WHorizontalList bottomList = add(theme.horizontalList()).expandX().widget();

        //   Active
        bottomList.add(theme.label("Active:"));
        WCheckbox active = bottomList.add(theme.checkbox(element.active)).widget();
        active.action = () -> {
            if (element.active != active.checked) element.toggle();
        };

        WButton reset = bottomList.add(theme.button(GuiRenderer.RESET)).expandCellX().right().widget();
        reset.action = () -> {
            if (element.active != element.defaultActive) element.active = active.checked = element.defaultActive;
        };
    }

    @Override
    protected void onRenderBefore(float delta) {
        Modules.get().get(HUD.class).onRender(Render2DEvent.get(0, 0, delta));
    }
}