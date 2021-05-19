package dev.urmomia.gui.screens.settings;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.settings.PotionSetting;
import dev.urmomia.utils.misc.MyPotion;

public class PotionSettingScreen extends WindowScreen {
    public PotionSettingScreen(GuiTheme theme, PotionSetting setting) {
        super(theme, "Select potion");

        WTable table = add(theme.table()).expandX().widget();

        for (MyPotion potion : MyPotion.values()) {
            table.add(theme.itemWithLabel(potion.potion, potion.potion.getName().getString()));

            WButton select = table.add(theme.button("Select")).widget();
            select.action = () -> {
                setting.set(potion);
                onClose();
            };

            table.row();
        }
    }
}
