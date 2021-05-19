package dev.urmomia.gui.screens;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.widgets.WAccount;
import dev.urmomia.gui.widgets.containers.WContainer;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.systems.accounts.Account;
import dev.urmomia.systems.accounts.Accounts;
import dev.urmomia.utils.network.MainExecutor;

import static dev.urmomia.utils.Utils.mc;

public class AccountsScreen extends WindowScreen {

    public AccountsScreen(GuiTheme theme) {
        super(theme, "Accounts");
    }

    @Override
    protected void init() {
        super.init();

        clear();
        initWidgets();
    }

    private void initWidgets() {
        // Accounts
        for (Account<?> account : Accounts.get()) {
            WAccount wAccount = add(theme.account(this, account)).expandX().widget();
            wAccount.refreshScreenAction = () -> {
                clear();
                initWidgets();
            };
        }

        // Add account
        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        addButton(l, "cracced", () -> mc.openScreen(new AddCrackedAccountScreen(theme)));
        addButton(l, "premhum", () -> mc.openScreen(new AddPremiumAccountScreen(theme)));
        addButton(l, "altenign", () -> mc.openScreen(new AddAlteningAccountScreen(theme)));
    }

    private void addButton(WContainer c, String text, Runnable action) {
        WButton button = c.add(theme.button(text)).expandX().widget();
        button.action = action;
    }

    public static void addAccount(WButton add, WidgetScreen screen, Account<?> account) {
        add.set("...");
        screen.locked = true;

        MainExecutor.execute(() -> {
            if (account.fetchInfo() && account.fetchHead()) {
                Accounts.get().add(account);
                screen.locked = false;
                screen.onClose();
            }

            add.set("Add");
            screen.locked = false;
        });
    }
}
