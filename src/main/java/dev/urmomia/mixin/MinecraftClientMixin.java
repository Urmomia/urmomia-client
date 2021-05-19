package dev.urmomia.mixin;

import dev.urmomia.MainClient;
import dev.urmomia.events.entity.player.ItemUseCrosshairTargetEvent;
import dev.urmomia.events.game.GameLeftEvent;
import dev.urmomia.events.game.OpenScreenEvent;
import dev.urmomia.events.game.ResourcePacksReloadedEvent;
import dev.urmomia.events.world.TickEvent;
import dev.urmomia.gui.WidgetScreen;
import dev.urmomia.mixininterface.IMinecraftClient;
import dev.urmomia.systems.config.Config;
//import dev.urmomia.utils.network.OnlinePlayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

@Mixin(value = MinecraftClient.class, priority = 1001)
public abstract class MinecraftClientMixin implements IMinecraftClient {
    @Shadow public ClientWorld world;

    @Shadow protected abstract void doItemUse();

    @Shadow @Final public Mouse mouse;

    @Shadow @Final private Window window;

    @Shadow @Nullable public Screen currentScreen;

    @Shadow public abstract Profiler getProfiler();

    @Unique private boolean doItemUseCalled;
    @Unique private boolean rightClick;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        MainClient.INSTANCE.onInitializeClient();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void onPreTick(CallbackInfo info) {
        //OnlinePlayers.update();

        doItemUseCalled = false;

        getProfiler().push("meteor-client_pre_update");
        MainClient.EVENT_BUS.post(TickEvent.Pre.get());
        getProfiler().pop();

        if (rightClick && !doItemUseCalled) doItemUse();
        rightClick = false;
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        getProfiler().push("meteor-client_post_update");
        MainClient.EVENT_BUS.post(TickEvent.Post.get());
        getProfiler().pop();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void onDoItemUse(CallbackInfo info) {
        doItemUseCalled = true;
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"))
    private void onDisconnect(Screen screen, CallbackInfo info) {
        if (world != null) {
            MainClient.EVENT_BUS.post(GameLeftEvent.get());
        }
    }

    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(Screen screen, CallbackInfo info) {
        if (screen instanceof WidgetScreen) screen.mouseMoved(mouse.getX() * window.getScaleFactor(), mouse.getY() * window.getScaleFactor());

        OpenScreenEvent event = OpenScreenEvent.get(screen);
        MainClient.EVENT_BUS.post(event);

        if (event.isCancelled()) info.cancel();
    }

    @Redirect(method = "doItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", ordinal = 1))
    private HitResult doItemUseMinecraftClientCrosshairTargetProxy(MinecraftClient client) {
        return MainClient.EVENT_BUS.post(ItemUseCrosshairTargetEvent.get(client.crosshairTarget)).target;
    }

    @ModifyVariable(method = "reloadResources", at = @At("STORE"), ordinal = 0)
    private CompletableFuture<Void> onReloadResourcesNewCompletableFuture(CompletableFuture<Void> completableFuture) {
        completableFuture.thenRun(() -> MainClient.EVENT_BUS.post(ResourcePacksReloadedEvent.get()));
        return completableFuture;
    }

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true)
    private void getTitle(CallbackInfoReturnable<String> cir) {
        if (Config.get() != null && Config.get().windowTitle) cir.setReturnValue("Urmomia Client " + Config.version.getOriginalString());
    }

    // Interface

    @Override
    public void rightClick() {
        rightClick = true;
    }
}
