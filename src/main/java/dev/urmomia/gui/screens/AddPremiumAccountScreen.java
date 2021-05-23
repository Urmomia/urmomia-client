package dev.urmomia.gui.screens;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.systems.accounts.Accounts;
import dev.urmomia.systems.accounts.types.PremiumAccount;

public class AddPremiumAccountScreen extends WindowScreen {
    public AddPremiumAccountScreen(GuiTheme theme) {
        super(theme, "Add Premium Account");

        WTable t = add(theme.table()).widget();

        // Email
        t.add(theme.label("Email: "));
        WTextBox email = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        email.setFocused(true);
        t.row();

        // Password
        t.add(theme.label("Password: "));
        WTextBox password = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        t.row();

        // Add
        WButton add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            PremiumAccount account = new PremiumAccount(email.get(), password.get());
            if (!email.get().isEmpty() && !password.get().isEmpty() && email.get().contains("@") && !Accounts.get().exists(account)) {
                AccountsScreen.addAccount(add, this, account);
            }
        };
        
        enterAction = add.action;
    }
}
