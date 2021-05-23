package dev.urmomia.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ThemeButtonWidget extends AbstractPressableButtonWidget {
    public static final Identifier BUTTON_TEXTURE = new Identifier("urmomia-client", "textures/button.png");

    public static final ThemeButtonWidget.TooltipSupplier EMPTY = (button, matrices, mouseX, mouseY) -> {};
    protected final ThemeButtonWidget.PressAction onPress;
    protected final ThemeButtonWidget.TooltipSupplier tooltipSupplier;

    public ThemeButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        this(x, y, width, height, message, onPress, EMPTY);
    }

    public ThemeButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, message);
        this.onPress = onPress;
        this.tooltipSupplier = tooltipSupplier;
    }

    public void onPress() {
        this.onPress.onPress(this);
    }

    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.customRender(matrices);
        if (this.isHovered()) {
            this.renderToolTip(matrices, mouseX, mouseY);
        }

    }

    public void customRender(MatrixStack matrices) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        TextRenderer textRenderer = minecraftClient.textRenderer;
        minecraftClient.getTextureManager().bindTexture(BUTTON_TEXTURE);
        int j = this.active ? 16777215 : 10526880;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        drawTexture(matrices, this.x, this.y, 0, this.isHovered() ? 12 : 0, this.width, this.height, 40, 24);
        drawCenteredText(matrices, textRenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public void renderToolTip(MatrixStack matrices, int mouseX, int mouseY) {
        this.tooltipSupplier.onTooltip(this, matrices, mouseX, mouseY);
    }

    public interface TooltipSupplier {
        void onTooltip(ThemeButtonWidget button, MatrixStack matrices, int mouseX, int mouseY);
    }

    public interface PressAction {
        void onPress(ThemeButtonWidget button);
    }
}
