package dev.urmomia.gui.screens;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.systems.accounts.Accounts;
import dev.urmomia.systems.accounts.types.CrackedAccount;

public class AddCrackedAccountScreen extends WindowScreen {
    public AddCrackedAccountScreen(GuiTheme theme) {
        super(theme, "Add Cracked Account");

        WTable t = add(theme.table()).widget();

        // Name
        t.add(theme.label("Name: "));
        WTextBox name = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        name.setFocused(true);
        t.row();

        // Add
        WButton add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            CrackedAccount account = new CrackedAccount(name.get());
            if (!name.get().trim().isEmpty() && !(Accounts.get().exists(account))) {
                AccountsScreen.addAccount(add, this, account);
            }
        };
        
        enterAction = add.action;
    }
}
