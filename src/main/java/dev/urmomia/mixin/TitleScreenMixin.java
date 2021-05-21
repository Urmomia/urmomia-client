package dev.urmomia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.urmomia.gui.GuiThemes;
import dev.urmomia.gui.tabs.Tabs;
import dev.urmomia.systems.config.Config;
import dev.urmomia.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    private final int WHITE = Color.fromRGBA(255, 255, 255, 255);
    private final int GRAY = Color.fromRGBA(175, 175, 175, 255);
    private final int PURPLE = Color.fromRGBA(138, 48, 255, 255);
    private final int SUS = Color.fromRGBA(195, 0, 255, 255);

    private String text1;
    private int text1Length;

    private String text2;
    private int text2Length;

    private String text3;
    private int text3Length;

    private String text4;
    private int text4Length;

    private String text5;
    private int text5Length;

    private String text6;
    private int text6Length;

    private String text7;
    private int text7Length;

    private String text8;
    private int text8Length;

    private int fullLength;
    private int prevWidth;

    public TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {

        text1 = "Urmomia Client ";
        text2 = "by honsda";
        text3 = ", ";
        text4 = "Codex1729";
        text5 = ", ";
        text6 = "ChompChompDead";
        text7 = " & ";
        text8 = "ProfKambing    ";

        text1Length = textRenderer.getWidth(text1);
        text2Length = textRenderer.getWidth(text2);
        text3Length = textRenderer.getWidth(text3);
        text4Length = textRenderer.getWidth(text4);
        text5Length = textRenderer.getWidth(text5);
        text6Length = textRenderer.getWidth(text6);
        text7Length = textRenderer.getWidth(text7);
        text8Length = textRenderer.getWidth(text8);

        fullLength = text1Length + text2Length + text3Length + text4Length + text5Length + text6Length + text8Length;
        prevWidth = 0;

        addButton(new ButtonWidget((this.width / 2) - 100 - 3, (this.height / 2) - 20 - 3, 45, 20, new LiteralText("HUD"), button -> {
            Tabs.get().get(3).openScreen(GuiThemes.get());
        }));
        addButton(new ButtonWidget((this.width / 2) - 100 - 3, (this.height / 2) + 3, 60, 20, new LiteralText("Config"), button -> {
            Tabs.get().get(1).openScreen(GuiThemes.get());
        }));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info) {
        if (!Config.get().titleScreenCredits) return;
        prevWidth = 0;
        textRenderer.drawWithShadow(matrices, text1, width - fullLength - 3, 3, PURPLE);
        prevWidth += text1Length;
        textRenderer.drawWithShadow(matrices, text2, width - fullLength + prevWidth - 3, 3, WHITE);
        prevWidth += text2Length;
        textRenderer.drawWithShadow(matrices, text3, width - fullLength + prevWidth - 3, 3, GRAY);
        prevWidth += text3Length;
        textRenderer.drawWithShadow(matrices, text4, width - fullLength + prevWidth - 3, 3, WHITE);
        prevWidth += text4Length;
        textRenderer.drawWithShadow(matrices, text5, width - fullLength + prevWidth - 3, 3, GRAY);
        prevWidth += text5Length;
        textRenderer.drawWithShadow(matrices, text6, width - fullLength + prevWidth - 3, 3, WHITE);
        prevWidth += text6Length;
        textRenderer.drawWithShadow(matrices, text7, width - fullLength + prevWidth - 3, 3, GRAY);
        prevWidth += text7Length;
        textRenderer.drawWithShadow(matrices, text8, width - fullLength + prevWidth - 3, 3, WHITE);
        prevWidth += text8Length;
    }
}
