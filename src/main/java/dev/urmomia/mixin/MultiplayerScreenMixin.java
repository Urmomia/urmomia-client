package dev.urmomia.mixin;

import dev.urmomia.gui.GuiThemes;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.misc.AutoLogin;
import dev.urmomia.systems.modules.player.NameProtect;
import dev.urmomia.systems.proxies.Proxies;
import dev.urmomia.systems.proxies.Proxy;
import dev.urmomia.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin extends Screen {
    private int textColor1;
    private int textColor2;
    private int ENABLE;
    private int DISABLE;

    private String loggedInAs;
    private int loggedInAsLength;

    public MultiplayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        textColor1 = Color.fromRGBA(219, 192, 255, 255);
        textColor2 = Color.fromRGBA(175, 175, 175, 255);
        ENABLE = Color.fromRGBA(0, 255, 0, 255);
        DISABLE = Color.fromRGBA(255, 0, 0, 255);

        loggedInAs = "Logged in as ";
        loggedInAsLength = textRenderer.getWidth(loggedInAs);

        addButton(new ButtonWidget(this.width - 75 - 3, 3, 75, 20, new LiteralText("Accounts"), button -> {
            client.openScreen(GuiThemes.get().accountsScreen());
        }));

        addButton(new ButtonWidget(this.width - 75 - 3 - 75 - 2, 3, 75, 20, new LiteralText("Proxies"), button -> {
            client.openScreen(GuiThemes.get().proxiesScreen());
        }));

        addButton(new ButtonWidget(3, this.height - 30 - 3, 80, 20, new LiteralText("Toggle AL"), button -> {
            Modules.get().get(AutoLogin.class).toggle();
        }));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        float x = 3;
        float y = 3;

        // Logged in as
        textRenderer.drawWithShadow(matrices, loggedInAs, x, y, textColor1);
        textRenderer.drawWithShadow(matrices, Modules.get().get(NameProtect.class).getName(client.getSession().getUsername()), x + loggedInAsLength, y, textColor2);

        y += textRenderer.fontHeight + 2;

        // Proxy
        Proxy proxy = Proxies.get().getEnabled();

        String left = proxy != null ? "Using proxy " : "Not using a proxy";
        String right = proxy != null ? "(" + proxy.name + ") " + proxy.ip + ":" + proxy.port : null;

        textRenderer.drawWithShadow(matrices, left, x, y, textColor1);
        if (right != null) textRenderer.drawWithShadow(matrices, right, x + textRenderer.getWidth(left), y, textColor2);

        if (Modules.get().get(AutoLogin.class).isActive()) textRenderer.drawWithShadow(matrices, "AutoLogin Enabled", 3, this.height - 10 - 3, ENABLE);
        if (!(Modules.get().get(AutoLogin.class).isActive())) textRenderer.drawWithShadow(matrices, "AutoLogin Disabled", 3, this.height - 10 - 3, DISABLE);
    }
}
