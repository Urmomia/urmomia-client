package dev.urmomia.gui.screens;

import static dev.urmomia.utils.Utils.mc;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.gui.widgets.pressable.WCheckbox;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.misc.AutoLogin;
import dev.urmomia.utils.Utils;

public class LoginScreen extends WindowScreen {
    public LoginScreen(GuiTheme theme) {
        super(theme, "AutoLogin");

        WTable t = add(theme.table()).widget();

        // Pass
        t.add(theme.label("Pass: "));
        WTextBox name = t.add(theme.textBox(Modules.get().get(AutoLogin.class).passx.get())).minWidth(400).expandX().widget();
        name.setFocused(true);
        t.row();
        
        t.add(theme.label("Active: "));
        WCheckbox active = t.add(theme.checkbox(Modules.get().get(AutoLogin.class).isActive())).expandCellX().widget();
        active.action = () -> {
            if (Modules.get().get(AutoLogin.class).isActive() != active.checked) Modules.get().get(AutoLogin.class).toggle(Utils.canUpdate());
        };

        // Bottom
        WHorizontalList b = add(theme.horizontalList()).expandX().widget();
        WButton add = b.add(theme.button("Change")).expandX().widget();
        add.action = () -> {
            Modules.get().get(AutoLogin.class).floppa(name.get());
        };
        WButton the = t.add(theme.button("Accounts")).expandX().widget();
        the.action = () -> { mc.openScreen(new AccountsScreen(theme));};

        enterAction = add.action;
    }
}
