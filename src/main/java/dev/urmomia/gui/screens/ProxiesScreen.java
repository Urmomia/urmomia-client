package dev.urmomia.gui.screens;

import dev.urmomia.gui.GuiTheme;
import dev.urmomia.gui.WindowScreen;
import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.widgets.WLabel;
import dev.urmomia.gui.widgets.containers.WHorizontalList;
import dev.urmomia.gui.widgets.containers.WTable;
import dev.urmomia.gui.widgets.input.WDropdown;
import dev.urmomia.gui.widgets.input.WIntEdit;
import dev.urmomia.gui.widgets.input.WTextBox;
import dev.urmomia.gui.widgets.pressable.WButton;
import dev.urmomia.gui.widgets.pressable.WCheckbox;
import dev.urmomia.gui.widgets.pressable.WMinus;
import dev.urmomia.systems.config.Config;
import dev.urmomia.systems.proxies.Proxies;
import dev.urmomia.systems.proxies.Proxy;
import dev.urmomia.systems.proxies.ProxyType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static dev.urmomia.utils.Utils.mc;

public class ProxiesScreen extends WindowScreen {
    private static final Identifier LOGO = new Identifier("urmomia-client", "textures/hud/tab-client.png");
    private final List<WCheckbox> checkboxes = new ArrayList<>();

    public ProxiesScreen(GuiTheme theme) {
        super(theme, "Proxies");

        mc.getTextureManager().bindTexture(LOGO);
        WHorizontalList watermark = add(theme.horizontalList()).pad(4).top().widget();
        watermark.add(theme.texture(128 * 0.4, 128 * 0.4, 0, mc.getTextureManager().getTexture(LOGO)));
        watermark.add(theme.label(Config.version.getOriginalString()));
    }

    protected void openEditProxyScreen(Proxy proxy) {
        mc.openScreen(new EditProxyScreen(theme, proxy));
    }

    @Override
    protected void init() {
        super.init();

        initWidgets();
    }

    private void initWidgets() {
        clear();
        checkboxes.clear();

        // Proxies
        WTable table = add(theme.table()).expandX().widget();

        int i = 0;
        for (Proxy proxy : Proxies.get()) {
            int index = i;

            // Enabled
            WCheckbox enabled = table.add(theme.checkbox(proxy.enabled)).widget();
            checkboxes.add(enabled);
            enabled.action = () -> {
                boolean checked = enabled.checked;
                Proxies.get().setEnabled(proxy, checked);

                for (WCheckbox checkbox : checkboxes) checkbox.checked = false;
                checkboxes.get(index).checked = checked;
            };

            // Name
            WLabel name = table.add(theme.label(proxy.name)).widget();
            name.color = theme.textColor();

            // Type
            WLabel type = table.add(theme.label("(" + proxy.type + ")")).widget();
            type.color = theme.textSecondaryColor();

            // IP + Port
            WHorizontalList ipList = table.add(theme.horizontalList()).expandCellX().widget();
            ipList.spacing = 0;

            ipList.add(theme.label(proxy.ip));
            ipList.add(theme.label(":")).widget().color = theme.textSecondaryColor();
            ipList.add(theme.label(Integer.toString(proxy.port)));

            // Edit
            WButton edit = table.add(theme.button(GuiRenderer.EDIT)).widget();
            edit.action = () -> openEditProxyScreen(proxy);

            // Remove
            WMinus remove = table.add(theme.minus()).widget();
            remove.action = () -> {
                Proxies.get().remove(proxy);
                initWidgets();
            };

            table.row();
            i++;
        }

        // New
        WButton newBtn = add(theme.button("New")).expandX().widget();
        newBtn.action = () -> openEditProxyScreen(null);
    }

    protected static class EditProxyScreen extends WindowScreen {
        public EditProxyScreen(GuiTheme theme, Proxy p) {
            super(theme, p == null ? "New Proxy" : "Edit Proxy");

            boolean isNew = p == null;
            Proxy proxy = p == null ? new Proxy() : p;

            // General
            WTable general = add(theme.table()).expandX().widget();

            //   Name
            general.add(theme.label("Proxy Name:"));
            WTextBox name = general.add(theme.textBox(proxy.name)).expandX().widget();
            name.action = () -> proxy.name = name.get();
            general.row();

            //   Type
            general.add(theme.label("Type:"));
            WDropdown<ProxyType> type = general.add(theme.dropdown(proxy.type)).widget();
            type.action = () -> proxy.type = type.get();
            general.row();

            //   IP
            general.add(theme.label("IP:"));
            WTextBox ip = general.add(theme.textBox(proxy.ip)).minWidth(400).expandX().widget();
            ip.action = () -> proxy.ip = ip.get();
            general.row();

            //   Port
            general.add(theme.label("Port:"));
            WIntEdit port = general.add(theme.intEdit(proxy.port, 0, 0)).expandX().widget();
            port.min = 0;
            port.max = 65535;
            port.action = () -> proxy.port = port.get();

            // Optional
            add(theme.horizontalSeparator("Optional")).expandX().widget();
            WTable optional = add(theme.table()).expandX().widget();

            //   Username
            optional.add(theme.label("Username:"));
            WTextBox username = optional.add(theme.textBox(proxy.username)).expandX().widget();
            username.action = () -> proxy.username = username.get();
            optional.row();

            //   Password
            optional.add(theme.label("Password:"));
            WTextBox password = optional.add(theme.textBox(proxy.password)).expandX().widget();
            password.action = () -> proxy.password = password.get();

            // Add / Save
            add(theme.horizontalSeparator()).expandX();

            WButton addSave = add(theme.button(isNew ? "Add" : "Save")).expandX().widget();
            addSave.action = () -> {
                if (proxy.isValid() && (!isNew || Proxies.get().add(proxy))) {
                    onClose();
                }
            };
            
            enterAction = addSave.action;
        }
    }
}
