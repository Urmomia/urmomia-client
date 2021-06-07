package dev.urmomia.systems.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.ints.*;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import dev.urmomia.events.entity.EntityAddedEvent;
import dev.urmomia.events.entity.EntityRemovedEvent;
import dev.urmomia.events.packets.PacketEvent;
import dev.urmomia.events.render.Render2DEvent;
import dev.urmomia.events.render.RenderEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.mixininterface.IBox;
import dev.urmomia.mixininterface.IClientPlayerInteractionManager;
import dev.urmomia.mixininterface.IRaycastContext;
import dev.urmomia.mixininterface.IVec3d;
import dev.urmomia.rendering.Renderer;
import dev.urmomia.rendering.ShapeMode;
import dev.urmomia.rendering.text.TextRenderer;
import dev.urmomia.settings.*;
import dev.urmomia.systems.friends.Friends;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.entity.EntityUtils;
import dev.urmomia.utils.entity.Target;
import dev.urmomia.utils.entity.fakeplayer.FakePlayerManager;
import dev.urmomia.utils.misc.Keybind;
import dev.urmomia.utils.misc.Vec3;
import dev.urmomia.utils.player.DamageUtils;
import dev.urmomia.utils.player.InvUtils;
import dev.urmomia.utils.player.PlayerUtils;
import dev.urmomia.utils.player.Rotations;
import dev.urmomia.utils.render.NametagUtils;
import dev.urmomia.utils.render.color.SettingColor;
import dev.urmomia.utils.world.BlockIterator;
import dev.urmomia.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.TypeFilterableList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CrystalAura extends Module {
    public enum YawStepMode {
        Break,
        All,
    }

    public enum AutoSwitchMode {
        Normal,
        Silent,
        None
    }

    public enum SupportMode {
        Disabled,
        Accurate,
        Fast
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgPlace = settings.createGroup("Place");
    private final SettingGroup sgFacePlace = settings.createGroup("Face Place");
    private final SettingGroup sgBreak = settings.createGroup("Break");
    private final SettingGroup sgPause = settings.createGroup("Pause");
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
            .name("target-range")
            .description("Range in which to target players.")
            .defaultValue(10)
            .min(0)
            .sliderMax(16)
            .build()
    );

    private final Setting<Boolean> predictMovement = sgGeneral.add(new BoolSetting.Builder()
            .name("predict-movement")
            .description("Predicts target movement.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> ignoreTerrain = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-terrain")
            .description("Completely ignores terrain if it can be blown up by end crystals.")
            .defaultValue(true)
            .build()
    );


    private final Setting<Double> minDamage = sgGeneral.add(new DoubleSetting.Builder()
            .name("min-damage")
            .description("Minimum damage the crystal needs to deal to your target.")
            .defaultValue(6)
            .min(0)
            .build()
    );

    private final Setting<Double> maxDamage = sgGeneral.add(new DoubleSetting.Builder()
            .name("max-damage")
            .description("Maximum damage crystals can deal to yourself.")
            .defaultValue(6)
            .min(0)
            .max(36)
            .sliderMax(36)
            .build()
    );

    private final Setting<AutoSwitchMode> autoSwitch = sgGeneral.add(new EnumSetting.Builder<AutoSwitchMode>()
            .name("auto-switch")
            .description("Switches to crystals in your hotbar once a target is found.")
            .defaultValue(AutoSwitchMode.Normal)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Rotates server-side towards the crystals being hit/placed.")
            .defaultValue(true)
            .build()
    );

    private final Setting<YawStepMode> yawStepMode = sgGeneral.add(new EnumSetting.Builder<YawStepMode>()
            .name("yaw-steps-mode")
            .description("When to run the yaw steps check.")
            .defaultValue(YawStepMode.Break)
            .visible(rotate::get)
            .build()
    );

    private final Setting<Double> yawSteps = sgGeneral.add(new DoubleSetting.Builder()
            .name("yaw-steps")
            .description("Maximum number of degrees its allowed to rotate in one tick.")
            .defaultValue(180)
            .min(1)
            .max(180)
            .sliderMin(1)
            .sliderMax(180)
            .visible(rotate::get)
            .build()
    );

    private final Setting<Boolean> antiSuicide = sgGeneral.add(new BoolSetting.Builder()
            .name("anti-suicide")
            .description("Will not place and break crystals if they will kill you.")
            .defaultValue(true)
            .build()
    );

    // Place

    private final Setting<Boolean> doPlace = sgPlace.add(new BoolSetting.Builder()
            .name("place")
            .description("If the CA should place crystals.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> placeDelay = sgPlace.add(new IntSetting.Builder()
            .name("place-delay")
            .description("The delay in ticks to wait to place a crystal after it's exploded.")
            .defaultValue(0)
            .min(0)
            .sliderMin(0)
            .sliderMax(20)
            .build()
    );

    private final Setting<Double> placeRange = sgPlace.add(new DoubleSetting.Builder()
            .name("place-range")
            .description("Range in which to place crystals.")
            .defaultValue(4.5)
            .min(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Double> placeWallsRange = sgPlace.add(new DoubleSetting.Builder()
            .name("place-walls-range")
            .description("Range in which to place crystals when behind blocks.")
            .defaultValue(3.5)
            .min(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Boolean> placement112 = sgPlace.add(new BoolSetting.Builder()
            .name("1.12-placement")
            .description("Uses 1.12 crystal placement.")
            .defaultValue(false)
            .build()
    );

    private final Setting<SupportMode> support = sgPlace.add(new EnumSetting.Builder<SupportMode>()
            .name("support")
            .description("Places a support block in air if no other position have been found.")
            .defaultValue(SupportMode.Disabled)
            .build()
    );

    private final Setting<Integer> supportDelay = sgPlace.add(new IntSetting.Builder()
            .name("support-delay")
            .description("Delay in ticks after placing support block.")
            .defaultValue(1)
            .min(0)
            .visible(() -> support.get() != SupportMode.Disabled)
            .build()
    );

    // Face place

    private final Setting<Boolean> facePlace = sgFacePlace.add(new BoolSetting.Builder()
            .name("face-place")
            .description("Will face-place when target is below a certain health or armor durability threshold.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Double> facePlaceHealth = sgFacePlace.add(new DoubleSetting.Builder()
            .name("face-place-health")
            .description("The health the target has to be at to start face placing.")
            .defaultValue(8)
            .min(1)
            .sliderMin(1)
            .sliderMax(36)
            .visible(facePlace::get)
            .build()
    );

    private final Setting<Double> facePlaceDurability = sgFacePlace.add(new DoubleSetting.Builder()
            .name("face-place-durability")
            .description("The durability threshold percentage to be able to face-place.")
            .defaultValue(2)
            .min(1)
            .sliderMin(1)
            .sliderMax(100)
            .visible(facePlace::get)
            .build()
    );

    private final Setting<Boolean> facePlaceArmor = sgFacePlace.add(new BoolSetting.Builder()
            .name("face-place-missing-armor")
            .description("Automatically starts face placing when a target misses a piece of armor.")
            .defaultValue(false)
            .visible(facePlace::get)
            .build()
    );

    private final Setting<Keybind> forceFacePlace = sgFacePlace.add(new KeybindSetting.Builder()
            .name("force-face-place")
            .description("Starts face place when this button is pressed.")
            .defaultValue(Keybind.fromKey(-1))
            .build()
    );

    // Break

    private final Setting<Boolean> doBreak = sgBreak.add(new BoolSetting.Builder()
            .name("break")
            .description("If the CA should break crystals.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> breakDelay = sgBreak.add(new IntSetting.Builder()
            .name("break-delay")
            .description("The delay in ticks to wait to break a crystal after it's placed.")
            .defaultValue(0)
            .min(0)
            .sliderMin(0)
            .sliderMax(20)
            .build()
    );

    private final Setting<Boolean> smartDelay = sgBreak.add(new BoolSetting.Builder()
            .name("smart-delay")
            .description("Only breaks crystals when the target can receive damage.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> switchDelay = sgBreak.add(new IntSetting.Builder()
            .name("switch-delay")
            .description("The delay in ticks to wait to break a crystal after switching hotbar slot.")
            .defaultValue(0)
            .min(0)
            .sliderMax(10)
            .build()
    );

    private final Setting<Double> breakRange = sgBreak.add(new DoubleSetting.Builder()
            .name("break-range")
            .description("Range in which to break crystals.")
            .defaultValue(4.5)
            .min(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Double> breakWallsRange = sgBreak.add(new DoubleSetting.Builder()
            .name("break-walls-range")
            .description("Range in which to break crystals when behind blocks.")
            .defaultValue(3)
            .min(0)
            .sliderMax(6)
            .build()
    );

    private final Setting<Boolean> onlyBreakOwn = sgBreak.add(new BoolSetting.Builder()
            .name("only-own")
            .description("Only breaks own crystals.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> breakAttempts = sgBreak.add(new IntSetting.Builder()
            .name("break-attempts")
            .description("How many times to hit a crystal before stopping to target it.")
            .defaultValue(2)
            .sliderMin(1)
            .sliderMax(5)
            .build()
    );

    private final Setting<Integer> minimumCrystalAge = sgBreak.add(new IntSetting.Builder()
            .name("minimum-crystal-age")
            .description("How many ticks the crystal needs to exist in a world before trying to attack it.")
            .defaultValue(0)
            .min(0)
            .build()
    );

    private final Setting<Boolean> fastBreak = sgBreak.add(new BoolSetting.Builder()
            .name("fast-break")
            .description("Ignores break delay and tries to break the crystal as soon as it's spawned in the world.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> antiWeakness = sgBreak.add(new BoolSetting.Builder()
            .name("anti-weakness")
            .description("Switches to tools with high enough damage to explode the crystal with weakness effect.")
            .defaultValue(true)
            .build()
    );

    // Pause

    private final Setting<Boolean> eatPause = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-eat")
            .description("Pauses Crystal Aura when eating.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> drinkPause = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-drink")
            .description("Pauses Crystal Aura when drinking.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> minePause = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-mine")
            .description("Pauses Crystal Aura when mining.")
            .defaultValue(false)
            .build()
    );

    // Render

    private final Setting<Boolean> renderSwing = sgRender.add(new BoolSetting.Builder()
            .name("swing")
            .description("Renders hand swinging client side.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders a block overlay over the block the crystals are being placed on.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> renderBreak = sgRender.add(new BoolSetting.Builder()
            .name("break")
            .description("Renders a block overlay over the block the crystals are broken on.")
            .defaultValue(false)
            .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color of the block overlay.")
            .defaultValue(new SettingColor(255, 255, 255, 45))
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the block overlay.")
            .defaultValue(new SettingColor(255, 255, 255, 255))
            .build()
    );

    private final Setting<Boolean> renderDamageText = sgRender.add(new BoolSetting.Builder()
            .name("damage")
            .description("Renders crystal damage text in the block overlay.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Double> damageTextScale = sgRender.add(new DoubleSetting.Builder()
            .name("damage-scale")
            .description("How big the damage text should be.")
            .defaultValue(1.25)
            .min(1)
            .sliderMax(4)
            .visible(renderDamageText::get)
            .build()
    );

    private final Setting<Integer> renderTime = sgRender.add(new IntSetting.Builder()
            .name("render-time")
            .description("How long to render for.")
            .defaultValue(10)
            .min(0)
            .sliderMax(20)
            .build()
    );

    private final Setting<Integer> renderBreakTime = sgRender.add(new IntSetting.Builder()
            .name("break-time")
            .description("How long to render breaking for.")
            .defaultValue(13)
            .min(0)
            .sliderMax(20)
            .visible(renderBreak::get)
            .build()
    );

    // Fields

    private int breakTimer, placeTimer, switchTimer;
    private final List<PlayerEntity> targets = new ArrayList<>();
    private Hand hand;

    private final Vec3d vec3d = new Vec3d(0, 0, 0);
    private final Vec3d playerEyePos = new Vec3d(0, 0, 0);
    private final Vec3 vec3 = new Vec3();
    private final BlockPos.Mutable blockPos = new BlockPos.Mutable();
    private final Box box = new Box(0, 0, 0, 0, 0, 0);

    private final Vec3d vec3dRayTraceEnd = new Vec3d(0, 0, 0);
    private RaycastContext raycastContext;

    private final IntSet placedCrystals = new IntOpenHashSet();
    private boolean placing;
    private int placingTimer;
    private final BlockPos.Mutable placingCrystalBlockPos = new BlockPos.Mutable();

    private final IntSet removed = new IntOpenHashSet();
    private final Int2IntMap attemptedBreaks = new Int2IntOpenHashMap();
    private final Int2IntMap waitingToExplode = new Int2IntOpenHashMap();

    private double serverYaw;

    private PlayerEntity bestTarget;
    private double bestTargetDamage;
    private int bestTargetTimer;

    private boolean didRotateThisTick;
    private boolean isLastRotationPos;
    private final Vec3d lastRotationPos = new Vec3d(0, 0 ,0);
    private double lastYaw, lastPitch;
    private int lastRotationTimer;

    private int renderTimer, breakRenderTimer;
    private final BlockPos.Mutable renderPos = new BlockPos.Mutable();
    private final BlockPos.Mutable breakRenderPos = new BlockPos.Mutable();
    private double renderDamage;

    public CrystalAura() {
        super(Categories.Combat, "crystal-aura", "Automatically places and attacks crystals.");
    }

    @Override
    public void onActivate() {
        breakTimer = 0;
        placeTimer = 0;

        raycastContext = new RaycastContext(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);

        placing = false;
        placingTimer = 0;

        serverYaw = mc.player.yaw;

        bestTargetDamage = 0;
        bestTargetTimer = 0;

        lastRotationTimer = getLastRotationStopDelay();

        renderTimer = 0;
        breakRenderTimer = 0;
    }

    @Override
    public void onDeactivate() {
        targets.clear();

        placedCrystals.clear();

        attemptedBreaks.clear();
        waitingToExplode.clear();

        removed.clear();

        bestTarget = null;
    }

    private int getLastRotationStopDelay() {
        return Math.max(10, placeDelay.get() / 2 + breakDelay.get() / 2 + 10);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onPreTick(TickEvent.Pre event) {
        // Update last rotation
        didRotateThisTick = false;
        lastRotationTimer++;

        // Decrement placing timer
        if (placing) {
            if (placingTimer > 0) placingTimer--;
            else placing = false;
        }

        // Decrement best target timer
        if (bestTargetTimer > 0) bestTargetTimer--;
        bestTargetDamage = 0;

        // Decrement break, place and switch timers
        if (breakTimer > 0) breakTimer--;
        if (placeTimer > 0) placeTimer--;
        if (switchTimer > 0) switchTimer--;

        // Decrement render timers
        if (renderTimer > 0) renderTimer--;
        if (breakRenderTimer > 0) breakRenderTimer--;

        // Update waiting to explode crystals and mark them as existing if reached threshold
        for (IntIterator it = waitingToExplode.keySet().iterator(); it.hasNext();) {
            int id = it.nextInt();
            int ticks = waitingToExplode.get(id);

            if (ticks > 3) {
                it.remove();
                removed.remove(id);
            }
            else {
                waitingToExplode.put(id, ticks + 1);
            }
        }

        // Check pause settings
        if (PlayerUtils.shouldPause(minePause.get(), eatPause.get(), drinkPause.get())) return;

        // Set player eye pos
        ((IVec3d) playerEyePos).set(mc.player.getPos().x, mc.player.getPos().y + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getPos().z);

        // Find targets, break and place
        findTargets();

        if (targets.size() > 0) {
            if (!didRotateThisTick) doBreak();
            if (!didRotateThisTick) doPlace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST - 666)
    private void onPreTickLast(TickEvent.Pre event) {
        // Rotate to last rotation
        if (rotate.get() && lastRotationTimer < getLastRotationStopDelay() && !didRotateThisTick) {
            Rotations.rotate(isLastRotationPos ? Rotations.getYaw(lastRotationPos) : lastYaw, isLastRotationPos ? Rotations.getPitch(lastRotationPos) : lastPitch, -100, null);
        }
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof EndCrystalEntity)) return;

        if (placing && event.entity.getBlockPos().equals(placingCrystalBlockPos)) {
            placing = false;
            placingTimer = 0;
            placedCrystals.add(event.entity.getEntityId());
        }

        if (fastBreak.get() && !didRotateThisTick) {
            double damage = getBreakDamage(event.entity, true);
            if (damage > minDamage.get()) doBreak(event.entity);
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (event.entity instanceof EndCrystalEntity) {
            placedCrystals.remove(event.entity.getEntityId());
            removed.remove(event.entity.getEntityId());
            waitingToExplode.remove(event.entity.getEntityId());
        }
    }

    private void setRotation(boolean isPos, Vec3d pos, double yaw, double pitch) {
        didRotateThisTick = true;
        isLastRotationPos = isPos;

        if (isPos) ((IVec3d) lastRotationPos).set(pos.x, pos.y, pos.z);
        else {
            lastYaw = yaw;
            lastPitch = pitch;
        }

        lastRotationTimer = 0;
    }

    // Break

    private void doBreak() {
        if (!doBreak.get() || breakTimer > 0 || switchTimer > 0) return;

        double bestDamage = 0;
        Entity crystal = null;

        // Find best crystal to break
        for (Entity entity : mc.world.getEntities()) {
            double damage = getBreakDamage(entity, true);

            if (damage > bestDamage) {
                bestDamage = damage;
                crystal = entity;
            }
        }

        // Break the crystal
        if (crystal != null) doBreak(crystal);
    }

    private double getBreakDamage(Entity entity, boolean checkCrystalAge) {
        if (!(entity instanceof EndCrystalEntity)) return 0;

        // Check only break own
        if (onlyBreakOwn.get() && !placedCrystals.contains(entity.getEntityId())) return 0;

        // Check if it should already be removed
        if (removed.contains(entity.getEntityId())) return 0;

        // Check attempted breaks
        if (attemptedBreaks.get(entity.getEntityId()) > breakAttempts.get()) return 0;

        // Check crystal age
        if (checkCrystalAge && entity.age < minimumCrystalAge.get()) return 0;

        // Check range
        if (isOutOfRange(entity.getPos(), entity.getBlockPos(), false)) return 0;

        // Check damage to self and anti suicide
        blockPos.set(entity.getBlockPos()).move(0, -1, 0);
        double selfDamage = DamageUtils.crystalDamage(mc.player, entity.getPos(), predictMovement.get(), raycastContext, blockPos, ignoreTerrain.get());
        if (selfDamage > maxDamage.get() || (antiSuicide.get() && selfDamage >= EntityUtils.getTotalHealth(mc.player))) return 0;

        // Check damage to targets and face place
        double damage = getDamageToTargets(entity.getPos(), blockPos, true, false);
        boolean facePlaced = (facePlace.get() && shouldFacePlace(entity.getBlockPos()) || forceFacePlace.get().isPressed());

        if (!facePlaced && damage < minDamage.get()) return 0;

        return damage;
    }

    private void doBreak(Entity crystal) {
        // Anti weakness
        if (antiWeakness.get()) {
            StatusEffectInstance weakness = mc.player.getStatusEffect(StatusEffects.WEAKNESS);
            StatusEffectInstance strength = mc.player.getStatusEffect(StatusEffects.STRENGTH);

            // Check for strength
            if (weakness != null && (strength == null || strength.getAmplifier() <= weakness.getAmplifier())) {
                // Check if the item in your hand is already valid
                if (!isValidWeaknessItem(mc.player.getMainHandStack())) {
                    // Find valid item to break with
                    int slot = InvUtils.findItemInHotbar(this::isValidWeaknessItem);
                    if (slot == -1) return;

                    mc.player.inventory.selectedSlot = slot;
                    ((IClientPlayerInteractionManager) mc.interactionManager).syncSelectedSlot2();

                    switchTimer = 1;
                    return;
                }
            }
        }

        // Rotate and attack
        boolean attacked = true;

        if (rotate.get()) {
            double yaw = Rotations.getYaw(crystal);
            double pitch = Rotations.getPitch(crystal, Target.Feet);

            if (doYawSteps(yaw, pitch)) {
                setRotation(true, crystal.getPos(), 0, 0);
                Rotations.rotate(yaw, pitch, 50, () -> attackCrystal(crystal));

                breakTimer = breakDelay.get();
            }
            else {
                attacked = false;
            }
        }
        else {
            attackCrystal(crystal);
            breakTimer = breakDelay.get();
        }

        if (attacked) {
            // Update state
            removed.add(crystal.getEntityId());
            attemptedBreaks.put(crystal.getEntityId(), attemptedBreaks.get(crystal.getEntityId()) + 1);
            waitingToExplode.put(crystal.getEntityId(), 0);

            // Break render
            breakRenderPos.set(crystal.getBlockPos().down());
            breakRenderTimer = renderBreakTime.get();
        }
    }

    private boolean isValidWeaknessItem(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ToolItem) || itemStack.getItem() instanceof HoeItem) return false;

        ToolMaterial material = ((ToolItem) itemStack.getItem()).getMaterial();
        return material == ToolMaterials.DIAMOND || material == ToolMaterials.NETHERITE;
    }

    private void attackCrystal(Entity entity) {
        // Attack
        mc.player.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, mc.player.isSneaking()));

        if (renderSwing.get()) mc.player.swingHand(hand);
        else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (event.packet instanceof UpdateSelectedSlotC2SPacket) {
            switchTimer = switchDelay.get();
        }
    }

    // Place

    private void doPlace() {
        if (!doPlace.get() || placeTimer > 0) return;

        // Return if there are no crystals in hotbar or offhand
        if (mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL && InvUtils.findItemInHotbar(Items.END_CRYSTAL) == -1) return;

        // Return if there are no crystals in either hand and auto switch mode is none
        if (autoSwitch.get() == AutoSwitchMode.None && mc.player.getOffHandStack().getItem() != Items.END_CRYSTAL && mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL) return;

        // Check for multiplace
        for (Entity entity : mc.world.getEntities()) {
            if (getBreakDamage(entity, false) > 0) return;
        }

        // Setup variables
        AtomicDouble bestDamage = new AtomicDouble(0);
        AtomicReference<BlockPos.Mutable> bestBlockPos = new AtomicReference<>(new BlockPos.Mutable());
        AtomicBoolean isSupport = new AtomicBoolean(support.get() != SupportMode.Disabled);

        // Find best position to place the crystal on
        BlockIterator.register((int) Math.ceil(placeRange.get()), (int) Math.ceil(placeRange.get()), (bp, blockState) -> {
            // Check if its bedrock or obsidian and return if isSupport is false
            boolean hasBlock = blockState.isOf(Blocks.BEDROCK) || blockState.isOf(Blocks.OBSIDIAN);
            if (!hasBlock && (!isSupport.get() || !blockState.getMaterial().isReplaceable())) return;

            // Check if there is air on top
            blockPos.set(bp.getX(), bp.getY() + 1, bp.getZ());
            if (!mc.world.getBlockState(blockPos).isAir()) return;

            if (placement112.get()) {
                blockPos.move(0, 1, 0);
                if (!mc.world.getBlockState(blockPos).isAir()) return;
            }

            // Check range
            ((IVec3d) vec3d).set(bp.getX() + 0.5, bp.getY() + 1, bp.getZ() + 0.5);
            blockPos.set(bp).move(0, 1, 0);
            if (isOutOfRange(vec3d, blockPos, true)) return;

            // Check damage to self and anti suicide
            double selfDamage = DamageUtils.crystalDamage(mc.player, vec3d, predictMovement.get(), raycastContext, bp, ignoreTerrain.get());
            if (selfDamage > maxDamage.get() || (antiSuicide.get() && selfDamage >= EntityUtils.getTotalHealth(mc.player))) return;

            // Check damage to targets and face place
            double damage = getDamageToTargets(vec3d, bp, false, !hasBlock && support.get() == SupportMode.Fast);

            boolean facePlaced = (facePlace.get() && shouldFacePlace(blockPos)) || (forceFacePlace.get().isPressed());

            if (!facePlaced && damage < minDamage.get()) return;

            // Check if it can be placed
            double x = bp.getX();
            double y = bp.getY() + 1;
            double z = bp.getZ();
            ((IBox) box).set(x, y, z, x + 1, y + (placement112.get() ? 1 : 2), z + 1);

            if (intersectsWithEntities(box)) return;

            // Compare damage
            if (damage > bestDamage.get() || (isSupport.get() && hasBlock)) {
                bestDamage.set(damage);
                bestBlockPos.get().set(bp);
            }

            if (hasBlock) isSupport.set(false);
        });

        // Place the crystal
        BlockIterator.after(() -> {
            if (bestDamage.get() == 0) return;

            BlockHitResult result = getPlaceInfo(bestBlockPos.get());

            ((IVec3d) vec3d).set(
                    result.getBlockPos().getX() + 0.5 + result.getSide().getVector().getX() * 1.0 / 2.0,
                    result.getBlockPos().getY() + 0.5 + result.getSide().getVector().getY() * 1.0 / 2.0,
                    result.getBlockPos().getZ() + 0.5 + result.getSide().getVector().getZ() * 1.0 / 2.0
            );

            if (rotate.get()) {
                double yaw = Rotations.getYaw(vec3d);
                double pitch = Rotations.getPitch(vec3d);

                if (yawStepMode.get() == YawStepMode.Break || doYawSteps(yaw, pitch)) {
                    setRotation(true, vec3d, 0, 0);
                    Rotations.rotate(yaw, pitch, 50, () -> placeCrystal(result, bestDamage.get(), isSupport.get() ? bestBlockPos.get() : null));

                    placeTimer += placeDelay.get();
                }
            }
            else {
                placeCrystal(result, bestDamage.get(), isSupport.get() ? bestBlockPos.get() : null);
                placeTimer += placeDelay.get();
            }
        });
    }

    private BlockHitResult getPlaceInfo(BlockPos blockPos) {
        ((IVec3d) vec3d).set(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        for (Direction side : Direction.values()) {
            ((IVec3d) vec3dRayTraceEnd).set(
                    blockPos.getX() + 0.5 + side.getVector().getX() * 0.5,
                    blockPos.getY() + 0.5 + side.getVector().getY() * 0.5,
                    blockPos.getZ() + 0.5 + side.getVector().getZ() * 0.5
            );

            ((IRaycastContext) raycastContext).set(vec3d, vec3dRayTraceEnd, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
            BlockHitResult result = mc.world.raycast(raycastContext);

            if (result != null && result.getType() == HitResult.Type.BLOCK && result.getBlockPos().equals(blockPos)) {
                return result;
            }
        }

        Direction side = blockPos.getY() > vec3d.y ? Direction.DOWN : Direction.UP;
        return new BlockHitResult(vec3d, side, blockPos, false);
    }

    private void placeCrystal(BlockHitResult result, double damage, BlockPos supportBlock) {
        // Switch
        Item targetItem = supportBlock == null ? Items.END_CRYSTAL : Items.OBSIDIAN;

        int slot = -1;
        boolean triedFindingSlot = false;

        hand = Hand.OFF_HAND;
        if (mc.player.getOffHandStack().getItem() != targetItem) {
            hand = Hand.MAIN_HAND;

            if (mc.player.getMainHandStack().getItem() != targetItem) {
                slot = InvUtils.findItemInHotbar(targetItem);
                triedFindingSlot = true;
            }
        }

        if (triedFindingSlot && slot == -1) return;

        int prevSlot = mc.player.inventory.selectedSlot;

        if (autoSwitch.get() != AutoSwitchMode.None && hand != Hand.OFF_HAND && slot != -1) {
            mc.player.inventory.selectedSlot = slot;
            ((IClientPlayerInteractionManager) mc.interactionManager).syncSelectedSlot2();
        }

        // Place
        if (supportBlock == null) {
            // Place crystal
            mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, result));

            if (renderSwing.get()) mc.player.swingHand(hand);
            else mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(hand));

            placing = true;
            placingTimer = 4;
            placingCrystalBlockPos.set(result.getBlockPos()).move(0, 1, 0);

            renderTimer = renderTime.get();
            renderPos.set(result.getBlockPos());
            renderDamage = damage;
        }
        else {
            // Place support block
            BlockUtils.place(supportBlock, hand, slot, false, 0, renderSwing.get(), true, true, false);
            placeTimer += supportDelay.get();

            if (supportDelay.get() == 0) placeCrystal(result, damage, null);
        }

        // Switch back
        if (autoSwitch.get() == AutoSwitchMode.Silent) {
            mc.player.inventory.selectedSlot = prevSlot;
            ((IClientPlayerInteractionManager) mc.interactionManager).syncSelectedSlot2();
        }
    }

    // Yaw steps

    @EventHandler
    private void onPacketSent(PacketEvent.Sent event) {
        if (event.packet instanceof PlayerMoveC2SPacket) {
            serverYaw = ((PlayerMoveC2SPacket) event.packet).getYaw((float) serverYaw);
        }
    }

    public boolean doYawSteps(double targetYaw, double targetPitch) {
        targetYaw = MathHelper.wrapDegrees(targetYaw) + 180;
        double serverYaw = MathHelper.wrapDegrees(this.serverYaw) + 180;

        if (distanceBetweenAngles(serverYaw, targetYaw) <= yawSteps.get()) return true;

        double delta = Math.abs(targetYaw - serverYaw);
        double yaw = this.serverYaw;

        if (serverYaw < targetYaw) {
            if (delta < 180) yaw += yawSteps.get();
            else yaw -= yawSteps.get();
        }
        else {
            if (delta < 180) yaw -= yawSteps.get();
            else yaw += yawSteps.get();
        }

        setRotation(false, null, yaw, targetPitch);
        Rotations.rotate(yaw, targetPitch, -100, null); // Priority -100 so it sends the packet as the last one, im pretty sure it doesn't matte but idc
        return false;
    }

    private static double distanceBetweenAngles(double alpha, double beta) {
        double phi = Math.abs(beta - alpha) % 360;
        return phi > 180 ? 360 - phi : phi;
    }

    // Face place

    private boolean shouldFacePlace(BlockPos crystal) {
        // Checks if the provided crystal position should face place to any target
        for (PlayerEntity target : targets) {
            BlockPos pos = target.getBlockPos();

            if (crystal.getY() == pos.getY() + 1 && Math.abs(pos.getX() - crystal.getX()) <= 1 && Math.abs(pos.getZ() - crystal.getZ()) <= 1) {
                if (EntityUtils.getTotalHealth(target) <= facePlaceHealth.get()) return true;

                for (ItemStack itemStack : target.getArmorItems()) {
                    if (itemStack == null || itemStack.isEmpty()) {
                        if (facePlaceArmor.get()) return true;
                    }
                    else {
                        if ((double) (itemStack.getMaxDamage() - itemStack.getDamage()) / itemStack.getMaxDamage() * 100 <= facePlaceDurability.get()) return true;
                    }
                }
            }
        }

        return false;
    }

    // Others

    private boolean isOutOfRange(Vec3d vec3d, BlockPos blockPos, boolean place) {
        ((IRaycastContext) raycastContext).set(playerEyePos, vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);

        BlockHitResult result = mc.world.raycast(raycastContext);
        boolean behindWall = result == null || !result.getBlockPos().equals(blockPos);
        double distance = mc.player.getPos().distanceTo(vec3d);

        return distance > (behindWall ? (place ? placeWallsRange : breakWallsRange).get() : (place ? placeRange : breakRange).get());
    }

    private PlayerEntity getNearestTarget() {
        PlayerEntity nearestTarget = null;
        double nearestDistance = Double.MAX_VALUE;

        for (PlayerEntity target : targets) {
            double distance = target.squaredDistanceTo(mc.player);

            if (distance < nearestDistance) {
                nearestTarget = target;
                nearestDistance = distance;
            }
        }

        return nearestTarget;
    }

    private double getDamageToTargets(Vec3d vec3d, BlockPos obsidianPos, boolean breaking, boolean fast) {
        double damage = 0;

        if (fast) {
            PlayerEntity target = getNearestTarget();
            if (!(smartDelay.get() && breaking && target.hurtTime > 0)) damage = DamageUtils.crystalDamage(target, vec3d, predictMovement.get(), raycastContext, obsidianPos, ignoreTerrain.get());
        }
        else {
            for (PlayerEntity target : targets) {
                if (smartDelay.get() && breaking && target.hurtTime > 0) continue;

                double dmg = DamageUtils.crystalDamage(target, vec3d, predictMovement.get(), raycastContext, obsidianPos, ignoreTerrain.get());

                // Update best target
                if (dmg > bestTargetDamage) {
                    bestTarget = target;
                    bestTargetDamage = dmg;
                    bestTargetTimer = 10;
                }

                damage += dmg;
            }
        }

        return damage;
    }

    @Override
    public String getInfoString() {
        return bestTarget != null && bestTargetTimer > 0 ? bestTarget.getGameProfile().getName() : null;
    }

    private void findTargets() {
        targets.clear();

        // Players
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.abilities.creativeMode || player == mc.player) continue;

            if (!player.isDead() && player.isAlive() && Friends.get().shouldAttack(player) && player.distanceTo(mc.player) <= targetRange.get()) {
                targets.add(player);
            }
        }

        // Fake players
        for (PlayerEntity player : FakePlayerManager.getPlayers()) {
            if (!player.isDead() && player.isAlive() && Friends.get().shouldAttack(player) && player.distanceTo(mc.player) <= targetRange.get()) {
                targets.add(player);
            }
        }
    }

    private boolean intersectsWithEntities(Box box) {
        // Not using mc.world.getOtherEntities() just because this is a bit faster
        int startX = MathHelper.floor((box.minX - 2) / 16);
        int endX = MathHelper.floor((box.maxX + 2) / 16);
        int startZ = MathHelper.floor((box.minZ - 2) / 16);
        int endZ = MathHelper.floor((box.maxZ + 2) / 16);
        ChunkManager chunkManager = mc.world.getChunkManager();

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                WorldChunk chunk = chunkManager.getWorldChunk(x, z, false);

                if (chunk != null) {
                    TypeFilterableList<Entity>[] entitySections = chunk.getEntitySectionArray();

                    int startY = MathHelper.floor((box.minY - 2) / 16);
                    int endY = MathHelper.floor((box.maxY + 2) / 16);
                    startY = MathHelper.clamp(startY, 0, entitySections.length - 1);
                    endY = MathHelper.clamp(endY, 0, entitySections.length - 1);

                    for (int y = startY; y <= endY; y++) {
                        TypeFilterableList<Entity> entitySection = entitySections[y];

                        for (Entity entity : entitySection) {
                            if (entity.getBoundingBox().intersects(box) && !entity.isSpectator() && !removed.contains(entity.getEntityId())) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    // Render

    @EventHandler
    private void onRender(RenderEvent event) {
        if (renderTimer > 0 && render.get()) {
            Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, renderPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }

        if (breakRenderTimer > 0 && renderBreak.get() && !mc.world.getBlockState(breakRenderPos).isAir()) {
            int preSideA = sideColor.get().a;
            sideColor.get().a -= 20;
            sideColor.get().validate();

            int preLineA = lineColor.get().a;
            lineColor.get().a -= 20;
            lineColor.get().validate();

            Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, breakRenderPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);

            sideColor.get().a = preSideA;
            lineColor.get().a = preLineA;
        }
    }

    @EventHandler
    private void onRender2D(Render2DEvent event) {
        if (!render.get() || renderTimer <= 0 || !renderDamageText.get()) return;

        vec3.set(renderPos.getX() + 0.5, renderPos.getY() + 0.5, renderPos.getZ() + 0.5);

        if (NametagUtils.to2D(vec3, damageTextScale.get())) {
            NametagUtils.begin(vec3);
            TextRenderer.get().begin(1, false, true);

            String text = String.format("%.1f", renderDamage);
            double w = TextRenderer.get().getWidth(text) / 2;
            TextRenderer.get().render(text, -w, 0, lineColor.get(), true);

            TextRenderer.get().end();
            NametagUtils.end();
        }
    }
}