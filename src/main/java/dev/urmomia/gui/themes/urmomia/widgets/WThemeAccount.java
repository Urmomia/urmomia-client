package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.WAccount;
import dev.urmomia.systems.accounts.Account;
import dev.urmomia.utils.render.color.Color;

public class WThemeAccount extends WAccount implements ThemeWidget {
    public WThemeAccount(WidgetScreen screen, Account<?> account) {
        super(screen, account);
    }

    @Override
    protected Color loggedInColor() {
        return theme().loggedInColor.get();
    }

    @Override
    protected Color accountTypeColor() {
        return theme().textSecondaryColor.get();
    }
}
