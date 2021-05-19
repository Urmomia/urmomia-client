package dev.urmomia.systems.modules.render.hud.modules;

import dev.urmomia.rendering.DrawMode;
import dev.urmomia.rendering.Renderer;
import dev.urmomia.settings.BlockListSetting;
import dev.urmomia.settings.DoubleSetting;
import dev.urmomia.settings.Setting;
import dev.urmomia.settings.SettingGroup;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.HudRenderer;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.render.RenderUtils;
import dev.urmomia.utils.render.color.Color;
import dev.urmomia.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.List;

public class HoleHud extends HudElement {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .defaultValue(3)
            .min(1)
            .sliderMin(1)
            .build()
    );

    public final Setting<List<Block>> safe = sgGeneral.add(new BlockListSetting.Builder()
            .name("safe-blocks")
            .description("Which blocks to consider safe.")
            .defaultValue(Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK))
            .build()
    );

    private final Color BG_COLOR = new Color(255, 25, 25, 100);
    private final Color OL_COLOR = new Color(255, 25, 25, 255);

    public HoleHud(HUD hud) {
        super(hud, "hole", "Displays information about the hole you are standing in.", false);
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(16 * 3 * scale.get(), 16 * 3 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = box.getX();
        double y = box.getY();

        drawBlock(get(Facing.Left), x, y + 16 * scale.get()); // Left
        drawBlock(get(Facing.Front), x + 16 * scale.get(), y); // Front
        drawBlock(get(Facing.Right), x + 32 * scale.get(), y + 16 * scale.get()); // Right
        drawBlock(get(Facing.Back), x + 16 * scale.get(), y + 32 * scale.get()); // Back
    }

    private Direction get(Facing dir) {
        if (!Utils.canUpdate() || isInEditor()) return Direction.DOWN;
        return Direction.fromRotation(MathHelper.wrapDegrees(mc.player.yaw + dir.offset));
    }

    private void drawBlock(Direction dir, double x, double y) {
        Block block = dir == Direction.DOWN ? Blocks.OBSIDIAN : mc.world.getBlockState(mc.player.getBlockPos().offset(dir)).getBlock();
        if (!safe.get().contains(block)) block = Blocks.AIR;

        RenderUtils.drawItem(block.asItem().getDefaultStack(), (int) x, (int) y, scale.get(),false);

        if (dir == Direction.DOWN) return;

        BlockUtils.breakingBlocks.values().forEach(info -> {
            if (info.getPos().equals(mc.player.getBlockPos().offset(dir))) {
                renderBreaking(x, y, info.getStage() / 9f);
            }
        });
    }

    private void renderBreaking(double x, double y, double percent) {
        Renderer.NORMAL.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
        Renderer.NORMAL.quad(x, y, (16 * percent) * scale.get(), 16 * scale.get(), BG_COLOR);
        Renderer.NORMAL.quad(x, y, 16 * scale.get(), 1 * scale.get(), OL_COLOR);
        Renderer.NORMAL.quad(x, y + 15 * scale.get(), 16 * scale.get(), 1 * scale.get(), OL_COLOR);
        Renderer.NORMAL.quad(x, y, 1 * scale.get(), 16 * scale.get(),OL_COLOR);
        Renderer.NORMAL.quad(x + 15 * scale.get(), y, 1 * scale.get(), 16 * scale.get(), OL_COLOR);
        Renderer.NORMAL.end();
    }

    private enum Facing {
        Left(-90),
        Right(90),
        Front(0),
        Back(180);

        int offset;

        Facing(int offset) {
            this.offset = offset;
        }
    }
}