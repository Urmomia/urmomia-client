package dev.urmomia;

import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.IEventBus;
import dev.urmomia.events.game.GameLeftEvent;
import dev.urmomia.events.meteor.ClientInitialisedEvent;
import dev.urmomia.events.meteor.KeyEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.gui.GuiThemes;
import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.tabs.Tabs;
import dev.urmomia.rendering.Blur;
import dev.urmomia.rendering.Fonts;
import dev.urmomia.rendering.Matrices;
import dev.urmomia.rendering.gl.PostProcessRenderer;
import dev.urmomia.rendering.text.CustomTextRenderer;
import dev.urmomia.systems.Systems;
import dev.urmomia.systems.modules.Categories;
import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.misc.DiscordPresence;
import dev.urmomia.systems.modules.render.hud.HUD;
import dev.urmomia.utils.Utils;
import dev.urmomia.utils.misc.FakeClientPlayer;
import dev.urmomia.utils.misc.MeteorPlayers;
import dev.urmomia.utils.misc.Names;
import dev.urmomia.utils.misc.input.KeyAction;
import dev.urmomia.utils.misc.input.KeyBinds;
import dev.urmomia.utils.network.Capes;
import dev.urmomia.utils.network.MainExecutor;
import dev.urmomia.utils.player.EChestMemory;
import dev.urmomia.utils.player.Rotations;
import dev.urmomia.utils.render.color.RainbowColors;
import dev.urmomia.utils.world.BlockIterator;
import dev.urmomia.utils.world.BlockUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainClient implements ClientModInitializer {
    public static MainClient INSTANCE;
    public static final IEventBus EVENT_BUS = new EventBus();
    public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "urmomia-client");
    public static final Logger LOG = LogManager.getLogger();

    public static CustomTextRenderer FONT;

    public static MinecraftClient mc;

    public Screen screenToOpen;

    @Override
    public void onInitializeClient() {
        if (INSTANCE == null) {
            INSTANCE = this;
            return;
        }

        LOG.info("Initializing Urmomia Client");

        List<MeteorAddon> addons = new ArrayList<>();
        for (EntrypointContainer<MeteorAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("meteor", MeteorAddon.class)) {
            addons.add(entrypoint.getEntrypoint());
        }

        mc = MinecraftClient.getInstance();
        Utils.mc = mc;

        Systems.addPreLoadTask(() -> {
            if (!Modules.get().getFile().exists()) {
                Modules.get().get(DiscordPresence.class).toggle(false);
                Utils.addToServerList();
            }
        });

        Matrices.begin(new MatrixStack());
        Fonts.init();
        MainExecutor.init();
        Capes.init();
        RainbowColors.init();
        BlockIterator.init();
        EChestMemory.init();
        Rotations.init();
        Names.init();
        MeteorPlayers.init();
        FakeClientPlayer.init();
        PostProcessRenderer.init();
        Blur.init();
        Tabs.init();
        GuiThemes.init();
        BlockUtils.init();

        // Register categories
        Modules.REGISTERING_CATEGORIES = true;
        Categories.register();
        addons.forEach(MeteorAddon::onRegisterCategories);
        Modules.REGISTERING_CATEGORIES = false;

        Systems.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Systems.save();
            GuiThemes.save();
        }));

        EVENT_BUS.subscribe(this);
        EVENT_BUS.post(new ClientInitialisedEvent()); // TODO: This is there just for compatibility

        // Call onInitialize for addons
        addons.forEach(MeteorAddon::onInitialize);

        Modules.get().sortModules();
        Systems.load();

        GuiRenderer.init();
        GuiThemes.postInit();
    }

    public static boolean hudWasOn = false;

    private void openClickGui() {
        if (Modules.get().get(HUD.class).isActive()) Modules.get().get(HUD.class).toggle(); hudWasOn = true;
        Tabs.get().get(0).openScreen(GuiThemes.get());
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        Systems.save();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Capes.tick();

        if (screenToOpen != null && mc.currentScreen == null) {
            mc.openScreen(screenToOpen);
            screenToOpen = null;
        }

        if (Utils.canUpdate()) {
            mc.player.getActiveStatusEffects().values().removeIf(statusEffectInstance -> statusEffectInstance.getDuration() <= 0);
        }
    }

    @EventHandler
    private void onKey(KeyEvent event) {
        // Click GUI
        if (event.action == KeyAction.Press && KeyBinds.OPEN_CLICK_GUI.matchesKey(event.key, 0)) {
            if (!Utils.canUpdate() && Utils.isWhitelistedScreen() || mc.currentScreen == null) openClickGui();
        }
    }
}
