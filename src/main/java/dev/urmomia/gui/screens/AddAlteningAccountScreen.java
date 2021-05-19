package dev.urmomia.gui.screens;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.systems.accounts.types.TheAlteningAccount;

public class AddAlteningAccountScreen extends WindowScreen {
    public AddAlteningAccountScreen(GuiTheme theme) {
        super(theme, "Add The Altening Account");

        WTable t = add(theme.table()).widget();

        // Token
        t.add(theme.label("Token: "));
        WTextBox token = t.add(theme.textBox("")).minWidth(400).expandX().widget();
        token.setFocused(true);
        t.row();

        // Add
        WButton add = t.add(theme.button("Add")).expandX().widget();
        add.action = () -> {
            if (!token.get().isEmpty()) {
                AccountsScreen.addAccount(add, this, new TheAlteningAccount(token.get()));
            }
        };
    }
}
