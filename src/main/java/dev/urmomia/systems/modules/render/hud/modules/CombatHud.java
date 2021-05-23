package dev.urmomia.systems.modules.render.hud.modules;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.urmomia.rendering.DrawMode;
import dev.urmomia.rendering.Matrices;
import dev.urmomia.rendering.Renderer;
import dev.urmomia.rendering.text.TextRenderer;
import dev.urmomia.settings.*;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.systems.modules.render.hud.HudRenderer;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.entity.EntityUtils;
import dev.urmomia.utils.entity.SortPriority;
import dev.urmomia.utils.entity.TargetUtils;
import dev.urmomia.utils.misc.FakeClientPlayer;
import dev.urmomia.utils.player.PlayerUtils;
import dev.urmomia.utils.render.RenderUtils;
import dev.urmomia.utils.render.color.Color;
import dev.urmomia.utils.render.color.SettingColor;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombatHud extends HudElement {
    private static final Identifier TEXTURE = new Identifier("urmomia-client", "textures/combat-background.png");
    private static final Color GREEN = new Color(15, 255, 15);
    private static final Color RED = new Color(255, 15, 15);
    private static final Color BLACK = new Color(0, 0, 0, 255);
    private static final Color WHITE = new Color(255, 255, 255, 200);
    
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
            .name("scale")
            .description("Scale of combat info.")
            .defaultValue(2)
            .min(1)
            .sliderMin(1)
            .sliderMax(4)
            .build()
    );

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
            .name("range")
            .description("The range to target players.")
            .defaultValue(100)
            .min(1)
            .sliderMax(200)
            .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("Ignores friends when targeting.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Background> background = sgGeneral.add(new EnumSetting.Builder<CombatHud.Background>()
            .name("background")
            .description("Background of the combat info.")
            .defaultValue(Background.Texture)
            .build()
    );

    private final Setting<Boolean> displayPing = sgGeneral.add(new BoolSetting.Builder()
            .name("ping")
            .description("Shows the player's ping.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> displayDistance = sgGeneral.add(new BoolSetting.Builder()
            .name("distance")
            .description("Shows the distance between you and the player.")
            .defaultValue(true)
            .build()
    );

    private final Setting<List<Enchantment>> displayedEnchantments = sgGeneral.add(new EnchListSetting.Builder()
            .name("displayed-enchantments")
            .description("The enchantments that are shown on nametags.")
            .defaultValue(getDefaultEnchantments())
            .build()
    );

    private final Setting<SettingColor> backgroundColor = sgGeneral.add(new ColorSetting.Builder()
            .name("background-color")
            .description("Color of background.")
            .defaultValue(new SettingColor(124, 22, 255, 64))
            .build()
    );

    private final Setting<SettingColor> enchantmentTextColor = sgGeneral.add(new ColorSetting.Builder()
            .name("enchantment-color")
            .description("Color of enchantment text.")
            .defaultValue(new SettingColor(255, 255, 255))
            .build()
    );

    private final Setting<SettingColor> pingColor1 = sgGeneral.add(new ColorSetting.Builder()
            .name("ping-stage-1")
            .description("Color of ping text when under 75.")
            .defaultValue(new SettingColor(180, 235, 180))
            .build()
    );

    private final Setting<SettingColor> pingColor2 = sgGeneral.add(new ColorSetting.Builder()
            .name("ping-stage-2")
            .description("Color of ping text when between 75 and 200.")
            .defaultValue(new SettingColor(235, 230, 180))
            .build()
    );

    private final Setting<SettingColor> pingColor3 = sgGeneral.add(new ColorSetting.Builder()
            .name("ping-stage-3")
            .description("Color of ping text when over 200.")
            .defaultValue(new SettingColor(235, 180, 180))
            .build()
    );

    private final Setting<SettingColor> distColor1 = sgGeneral.add(new ColorSetting.Builder()
            .name("distance-stage-1")
            .description("The color when a player is within 10 blocks of you.")
            .defaultValue(new SettingColor(235, 180, 180))
            .build()
    );

    private final Setting<SettingColor> distColor2 = sgGeneral.add(new ColorSetting.Builder()
            .name("distance-stage-2")
            .description("The color when a player is within 50 blocks of you.")
            .defaultValue(new SettingColor(235, 230, 180))
            .build()
    );

    private final Setting<SettingColor> distColor3 = sgGeneral.add(new ColorSetting.Builder()
            .name("distance-stage-3")
            .description("The color when a player is greater then 50 blocks away from you.")
            .defaultValue(new SettingColor(180, 235, 180))
            .build()
    );

    private final Setting<SettingColor> healthColor1 = sgGeneral.add(new ColorSetting.Builder()
            .name("healh-stage-1")
            .description("The color on the left of the health gradient.")
            .defaultValue(new SettingColor(171, 76, 255))
            .build()
    );

    private final Setting<SettingColor> healthColor2 = sgGeneral.add(new ColorSetting.Builder()
            .name("health-stage-2")
            .description("The color in the middle of the health gradient.")
            .defaultValue(new SettingColor(148, 24, 255))
            .build()
    );

    private final Setting<SettingColor> healthColor3 = sgGeneral.add(new ColorSetting.Builder()
            .name("health-stage-3")
            .description("The color on the right of the health gradient.")
            .defaultValue(new SettingColor(123, 20, 255))
            .build()
    );

    private final Setting<SettingColor> healthTextColor= sgGeneral.add(new ColorSetting.Builder()
        .name("health-text")
        .description("Primary color of text.")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    private PlayerEntity playerEntity;

    public CombatHud(HUD hud) {
        super(hud, "combat-info", "Displays information about your combat target.", false);
    }

    @Override
    public void update(HudRenderer renderer) {
        box.setSize(175 * scale.get(), 95 * scale.get());
    }

    @Override
    public void render(HudRenderer renderer) {
        renderer.addPostTask(() -> {
            double x = box.getX();
            double y = box.getY();

            if (isInEditor()) playerEntity = FakeClientPlayer.getPlayer();
            else playerEntity = TargetUtils.getPlayerTarget(range.get(), SortPriority.LowestDistance);

            if (playerEntity == null) return;


            // Background
            if (background.get() != Background.None) drawBackground((int) x, (int) y);
            // Player Model
            InventoryScreen.drawEntity(
                    (int) (x + (25 * scale.get())),
                    (int) (y + (66 * scale.get())),
                    (int) (30 * scale.get()),
                    -MathHelper.wrapDegrees(playerEntity.prevYaw + (playerEntity.yaw - playerEntity.prevYaw) * mc.getTickDelta()),
                    -playerEntity.pitch, playerEntity
            );

            // Moving pos to past player model
            x += 50 * scale.get();
            y += 5 * scale.get();

            // Setting up texts
            String breakText = " | ";

            // Name
            String nameText = playerEntity.getGameProfile().getName();
            Color nameColor = PlayerUtils.getPlayerColor(playerEntity, hud.primaryColor.get());

            // Ping
            int ping = EntityUtils.getPing(playerEntity);
            String pingText = ping + "ms";

            Color pingColor;
            if (ping <= 75) pingColor = pingColor1.get();
            else if (ping <= 200) pingColor = pingColor2.get();
            else pingColor = pingColor3.get();

            // Distance
            double dist = 0;
            if (!isInEditor()) dist = Math.round(mc.player.distanceTo(playerEntity) * 100.0) / 100.0;
            String distText = dist + "m";

            Color distColor;
            if (dist <= 10) distColor = distColor1.get();
            else if (dist <= 50) distColor = distColor2.get();
            else distColor = distColor3.get();

            // Status Text
            String friendText = "Unknown";

            Color friendColor = hud.primaryColor.get();

            if (Friends.get().isFriend(playerEntity)) {
                friendText = "Friend";
                friendColor = Friends.get().color;
            } else {
                boolean naked = true;

                for (int position = 3; position >= 0; position--) {
                    ItemStack itemStack = getItem(position);

                    if (!itemStack.isEmpty()) naked = false;
                }

                if (naked) {
                    friendText = "Naked";
                    friendColor = GREEN;
                }
                else {
                    boolean threat = false;

                    for (int position = 5; position >= 0; position--) {
                        ItemStack itemStack = getItem(position);

                        if (itemStack.getItem() instanceof SwordItem
                                || itemStack.getItem() == Items.END_CRYSTAL
                                || itemStack.getItem() == Items.RESPAWN_ANCHOR
                                || itemStack.getItem() instanceof BedItem) threat = true;
                    }

                    if (threat) {
                        friendText = "Threat";
                        friendColor = RED;
                    }
                }
            }

            TextRenderer.get().begin(0.45 * scale.get(), false, true);

            double breakWidth = TextRenderer.get().getWidth(breakText);
            double pingWidth = TextRenderer.get().getWidth(pingText);
            double friendWidth = TextRenderer.get().getWidth(friendText);

            TextRenderer.get().render(nameText, x, y, nameColor != null ? nameColor : hud.primaryColor.get());

            y += TextRenderer.get().getHeight();

            TextRenderer.get().render(friendText, x, y, friendColor);

            if (displayPing.get()) {
                TextRenderer.get().render(breakText, x + friendWidth, y, hud.secondaryColor.get());
                TextRenderer.get().render(pingText, x + friendWidth + breakWidth, y, pingColor);

                if (displayDistance.get()) {
                    TextRenderer.get().render(breakText, x + friendWidth + breakWidth + pingWidth, y, hud.secondaryColor.get());
                    TextRenderer.get().render(distText, x + friendWidth + breakWidth + pingWidth + breakWidth, y, distColor);
                }
            } else if (displayDistance.get()) {
                TextRenderer.get().render(breakText, x + friendWidth, y, hud.secondaryColor.get());
                TextRenderer.get().render(distText, x + friendWidth + breakWidth, y, distColor);
            }

            TextRenderer.get().end();

            // Moving pos down for armor
            y += 10 * scale.get();

            double armorX;
            double armorY;
            int slot = 5;

            // Drawing armor
            RenderSystem.pushMatrix();
            RenderSystem.scaled(scale.get(), scale.get(), 1);

            x /= scale.get();
            y /= scale.get();

            TextRenderer.get().begin(0.35, false, true);

            for (int position = 0; position < 6; position++) {
                armorX = x + position * 20;
                armorY = y;

                ItemStack itemStack = getItem(slot);

                RenderUtils.drawItem(itemStack, (int) armorX, (int) armorY, true);

                armorY += 18;

                Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(itemStack);
                Map<Enchantment, Integer> enchantmentsToShow = new HashMap<>();

                for (Enchantment enchantment : displayedEnchantments.get()) {
                    if (enchantments.containsKey(enchantment)) enchantmentsToShow.put(enchantment, enchantments.get(enchantment));
                }

                for (Enchantment enchantment : enchantmentsToShow.keySet()) {
                    String enchantName = Utils.getEnchantSimpleName(enchantment, 3) + " " + enchantmentsToShow.get(enchantment);

                    double enchX = (armorX + 8) - (TextRenderer.get().getWidth(enchantName) / 2);

                    TextRenderer.get().render(enchantName, enchX, armorY, enchantment.isCursed() ? RED :  enchantmentTextColor.get());
                    armorY += TextRenderer.get().getHeight();
                }
                slot--;
            }

            TextRenderer.get().end();
            RenderSystem.popMatrix();

            y = (int) (box.getY() + 75 * scale.get());
            x = box.getX();

            // Health bar
            RenderSystem.pushMatrix();
            RenderSystem.scaled(scale.get(), scale.get(), 1);

            x /= scale.get();
            y /= scale.get();

            x += 5;
            y += 5;
            
            float maxHealth = playerEntity.getMaxHealth();
            int maxAbsorb = 16;
            int maxTotal = (int) (maxHealth + maxAbsorb);

            int totalHealthWidth = (int) (161 * maxHealth / maxTotal);
            int totalAbsorbWidth = 161 * maxAbsorb / maxTotal;

            float health = playerEntity.getHealth();
            float absorb = playerEntity.getAbsorptionAmount();
            float totalHealth = health + absorb;

            double healthPrecent = health / maxHealth;
            double absorbPrecent = absorb / maxAbsorb;

            int healthWidth = (int) (totalHealthWidth * healthPrecent);
            int absorbWidth = (int) (totalAbsorbWidth * absorbPrecent);

            Renderer.NORMAL.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
            Renderer.NORMAL.horizontalGradientQuad(x, y, healthWidth, 10, healthColor1.get(), healthColor2.get());
            Renderer.NORMAL.horizontalGradientQuad(x + healthWidth, y, absorbWidth, 10, healthColor2.get(), healthColor3.get());
            Renderer.NORMAL.end();

            Renderer.LINES.begin(null, DrawMode.Lines, VertexFormats.POSITION_COLOR);
            Renderer.LINES.boxEdges(x, y, 165, 10, WHITE);
            Renderer.LINES.end();

            String healthText = String.valueOf(Math.round(totalHealth * 10.0) / 10.0);

            TextRenderer.get().begin(0.45);
            TextRenderer.get().render(healthText, x, y, healthTextColor.get());
            TextRenderer.get().end();

            RenderSystem.popMatrix();
        });
    }

    private ItemStack getItem(int i) {
        if (isInEditor()) {
            switch (i) {
                case 0:  return Items.END_CRYSTAL.getDefaultStack();
                case 1:  return Items.NETHERITE_BOOTS.getDefaultStack();
                case 2:  return Items.NETHERITE_LEGGINGS.getDefaultStack();
                case 3:  return Items.NETHERITE_CHESTPLATE.getDefaultStack();
                case 4:  return Items.NETHERITE_HELMET.getDefaultStack();
                case 5:  return Items.TOTEM_OF_UNDYING.getDefaultStack();
            }
        }

        if (playerEntity == null) return ItemStack.EMPTY;

        if (i == 5) return playerEntity.getMainHandStack();
        else if (i == 4) return playerEntity.getOffHandStack();
        return playerEntity.inventory.getArmorStack(i);
    }

    public static List<Enchantment> getDefaultEnchantments() {
        List<Enchantment> ench = new ArrayList<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            ench.add(enchantment);
        }
        return ench;
    }

    private void drawBackground(int x, int y) {
        int w = (int) box.width;
        int h = (int) box.height;

        switch(background.get()) {
            case Flat:
                Renderer.NORMAL.begin(null, DrawMode.Triangles, VertexFormats.POSITION_COLOR);
                Renderer.NORMAL.quad(x, y, w, h, backgroundColor.get());
                Renderer.NORMAL.end();

                Renderer.LINES.begin(null, DrawMode.Lines, VertexFormats.POSITION_COLOR);
                Renderer.LINES.boxEdges(x, y, 175 * scale.get(), 95 * scale.get(), backgroundColor.get());
                Renderer.LINES.end();
                break;
            case Texture:
                RenderSystem.color4f(backgroundColor.get().r / 255F, backgroundColor.get().g / 255F, backgroundColor.get().b / 255F, backgroundColor.get().a / 255F);
                mc.getTextureManager().bindTexture(TEXTURE);
                DrawableHelper.drawTexture(Matrices.getMatrixStack(), x, y, 0, 0, 0, w, h, h, w);

                Renderer.LINES.begin(null, DrawMode.Lines, VertexFormats.POSITION_COLOR);
                Renderer.LINES.boxEdges(x, y, 175 * scale.get(), 95 * scale.get(), backgroundColor.get());
                Renderer.LINES.end();
                break;
        }
    }

    public enum Background {
        None,
        Flat,
        Texture
    }
}
