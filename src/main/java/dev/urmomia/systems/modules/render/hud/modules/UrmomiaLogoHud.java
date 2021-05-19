package dev.urmomia.systems.modules.render.hud.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.urmomia.rendering.Matrices;
import dev.urmomia.settings.*;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.HudRenderer;
import dev.urmomia.utils.render.color.SettingColor;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

public class UrmomiaLogoHud extends HudElement {

    private static final Identifier TRANSPARENT = new Identifier("urmomia-client", "textures/hud/urmomiacl.png");
    private static final Identifier BLACK = new Identifier("urmomia-client", "textures/hud/client-type.png");
    private static final Identifier TROLLFACE = new Identifier("urmomia-client", "textures/hud/trollface-logo.png");
    private static final Identifier TRANSPARENTMARK = new Identifier("urmomia-client", "textures/hud/urmomiacl-mark.png");
    private static final Identifier BLACKMARK = new Identifier("urmomia-client", "textures/hud/client-mark.png");
    private static final Identifier TROLLFACEMARK = new Identifier("urmomia-client", "textures/hud/trollface-mark.png");

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("Scale of the logo.")
            .defaultValue(0.2)
            .min(0.1)
            .sliderMin(0.1)
            .sliderMax(1)
            .max(1)
            .build()
    );
    private final Setting<TypeBox> tbox = sgGeneral.add(new EnumSetting.Builder<UrmomiaLogoHud.TypeBox>()
            .name("logo-type")
            .description("Type of the logo.")
            .defaultValue(TypeBox.Logotype)
            .build()
    );
    private final Setting<Background> background = sgGeneral.add(new EnumSetting.Builder<UrmomiaLogoHud.Background>()
            .name("background")
            .description("Texture of the logo.")
            .defaultValue(Background.Black)
            .build()
    );

    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("background-color")
        .description("Color of the background.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .build()
    );
    
    public UrmomiaLogoHud(HUD hud) {
        super(hud, "urmomia-logo", "Displays Urmomia Client's logo.");
    }

    @Override
    public void update(HudRenderer renderer) {
        switch(tbox.get()) {
            case Logotype:
            box.setSize(1050 * scale.get(), 256 * scale.get());
            break;
            case Logomark:
            box.setSize(256 * scale.get(), 256 * scale.get());
            break;
        }
        
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        drawBackground((int) x, (int) y);
    }

    private void drawBackground(int x, int y) {
        int w = (int) box.width;
        int h = (int) box.height;

        if (tbox.get() == TypeBox.Logotype) {
            switch(background.get()) {
                case Transparent:
                case Black:
                        RenderSystem.color4f(color.get().r / 255F, color.get().g / 255F, color.get().b / 255F, color.get().a / 255F);
                        mc.getTextureManager().bindTexture(background.get() == Background.Transparent ? TRANSPARENT : BLACK);
                        DrawableHelper.drawTexture(Matrices.getMatrixStack(), x, y, 0, 0, 0, w, h, h, w);
                    break;
                case Trollface:
                        RenderSystem.color4f(color.get().r / 255F, color.get().g / 255F, color.get().b / 255F, color.get().a / 255F);
                        mc.getTextureManager().bindTexture(background.get() == Background.Transparent ? TRANSPARENT : TROLLFACE);
                        DrawableHelper.drawTexture(Matrices.getMatrixStack(), x, y, 0, 0, 0, w, h, h, w);
                    break;               
            }
        }
        else if (tbox.get() == TypeBox.Logomark) {
            switch(background.get()) {
                case Transparent:
                case Black:
                        RenderSystem.color4f(color.get().r / 255F, color.get().g / 255F, color.get().b / 255F, color.get().a / 255F);
                        mc.getTextureManager().bindTexture(background.get() == Background.Transparent ? TRANSPARENTMARK : BLACKMARK);
                        DrawableHelper.drawTexture(Matrices.getMatrixStack(), x, y, 0, 0, 0, w, h, h, w);
                    break;
                case Trollface:
                        RenderSystem.color4f(color.get().r / 255F, color.get().g / 255F, color.get().b / 255F, color.get().a / 255F);
                        mc.getTextureManager().bindTexture(background.get() == Background.Transparent ? TRANSPARENTMARK : TROLLFACEMARK);
                        DrawableHelper.drawTexture(Matrices.getMatrixStack(), x, y, 0, 0, 0, w, h, h, w);
                    break;
            }
        }
    }

    public enum Background {
        None,
        Transparent,
        Black,
        Trollface
    }

    public enum TypeBox {
        Logotype,
        Logomark
    }
    
}
