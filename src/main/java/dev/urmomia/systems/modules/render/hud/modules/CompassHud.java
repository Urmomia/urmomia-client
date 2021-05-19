package dev.urmomia.systems.modules.render.hud.modules;

import dev.urmomia.rendering.DrawMode;
import dev.urmomia.rendering.Matrices;
import dev.urmomia.rendering.Renderer;
import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.EnumSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.HudRenderer;
import dev.urmomia.utils.render.color.Color;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class CompassHud extends HudElement {

    private static final Identifier COMPASS = new Identifier("urmomia-client", "textures/hud/compass.png");

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<CompassHud.Mode>()
            .name("type")
            .description("Which type of axis to show.")
            .defaultValue(CompassHud.Mode.Axis)
            .build()
    );

    private final Setting<Background> background = sgGeneral.add(new EnumSetting.Builder<CompassHud.Background>()
            .name("background")
            .description("Background of the Compass HUD.")
            .defaultValue(Background.Flat)
            .build()
    );

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("The scale of compass.")
            .defaultValue(2)
            .sliderMin(1)
            .sliderMax(5)
            .build()
    );

    private final Color NORTH = new Color(225, 45, 45);
    private final Color WHITE = new Color(255, 255, 255);
    private final Color BG = new Color(0, 0, 0, 180);
    private double yaw, pitch;

    public CompassHud(HUD hud) {
        super(hud, "compass", "Displays a compass.");
    }

    @Override
    public void update(HudRenderer renderer) {
        if (!isInEditor()) pitch = mc.player.pitch;
        else pitch = 90;

        pitch = MathHelper.clamp(pitch + 30, -90, 90);
        pitch = Math.toRadians(pitch);

        if (!isInEditor()) yaw = mc.player.yaw;
        else yaw = 180;

        yaw = MathHelper.wrapDegrees(yaw);
        yaw = Math.toRadians(yaw);

        box.setSize(100 *  scale.get(), 100 *  scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX() + (box.width / 2);
        double y = box.getY() + (box.height / 2);

        if (background.get() != Background.None) drawBackground((int) box.getX(), (int) box.getY());

        for (Direction dir : Direction.values()) {
            String axis = mode.get() == Mode.Axis ? dir.getAlternate() : dir.name();

            renderer.text(axis, (x + getX(dir)) - (renderer.textWidth(axis) / 2), (y + getY(dir)) - (renderer.textHeight() / 2), dir == Direction.N ? NORTH : WHITE);
        }
    }

    private void drawBackground(int x, int y) {
        int w = (int) box.width;
        int h = (int) box.height;

        switch(background.get()) {
            case Texture:
                mc.getTextureManager().bindTexture(COMPASS);
                DrawableHelper.drawTexture(Matrices.getMatrixStack(), x, y, 0, 0, 0, w, h, h, w);
                break;
            case Flat:
                Renderer.NORMAL.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
                Renderer.NORMAL.quad(x, y, w, h, BG);
                Renderer.NORMAL.end();
                Renderer.LINES.begin(null, DrawMode.Lines, VertexFormats.POSITION_COLOR);
                Renderer.LINES.boxEdges(x, y, 100, 100, new Color(0, 0, 0));
                Renderer.LINES.end();
            break;
        }
    }

    private double getX(Direction dir) {
        return Math.sin(getPosOnCompass(dir)) * scale.get() * 40;
    }

    private double getY(Direction dir) {
        return Math.cos(getPosOnCompass(dir)) * Math.sin(pitch) * scale.get() * 40;
    }

    private double getPosOnCompass(Direction dir) {
        return yaw + dir.ordinal() * Math.PI / 2;
    }

    private enum Direction {
        N("Z-"),
        W("X-"),
        S("Z+"),
        E("X+");

        String alternate;

        Direction(String alternate) {
            this.alternate = alternate;
        }

        public String getAlternate() {
            return alternate;
        }
    }

    public enum Mode {
        Axis, Pole
    }

    public enum Background {
        None,
        Texture,
        Flat
    }

}