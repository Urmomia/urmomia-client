package dev.urmomia.mixin;

import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.misc.AutoLogin;
import dev.urmomia.systems.modules.misc.AutoReconnect;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisconnectedScreen.class)
public class DisconnectedScreenMixin extends ScreenMixin {

    @Shadow private int reasonHeight;

    private ButtonWidget reconnectBtn;
    private double time = Modules.get().get(AutoReconnect.class).time.get() * 20;

    @Inject(method = "init", at = @At("TAIL"))
    private void onRenderBackground(CallbackInfo info) {
        if (Modules.get().get(AutoReconnect.class).lastServerInfo != null) {
            int x = width / 2 - 100;
            int y = Math.min((height / 2 + reasonHeight / 2) + 32, height - 30);

            reconnectBtn = addButton(new ButtonWidget(x, y, 200, 20, new LiteralText(getText()), button -> {if(AutoLogin.said == 1) AutoLogin.said = 0; client.openScreen(new ConnectScreen(new MultiplayerScreen(new TitleScreen()), client, Modules.get().get(AutoReconnect.class).lastServerInfo));}));
        }
    }

    @Override
    public void tick() {
        if (!Modules.get().isActive(AutoReconnect.class)) return;

        if (time <= 0) {
            client.openScreen(new ConnectScreen(new MultiplayerScreen(new TitleScreen()), client, Modules.get().get(AutoReconnect.class).lastServerInfo));
        } else {
            time--;
            if (reconnectBtn != null) ((AbstractButtonWidgetAccessor) reconnectBtn).setText(new LiteralText(getText()));
        }
    }

    private String getText() {
        String reconnectText = "Reconnect";
        if (Modules.get().isActive(AutoReconnect.class)) reconnectText += " " + String.format("(%.1f)", time / 20);
        return reconnectText;
    }

}