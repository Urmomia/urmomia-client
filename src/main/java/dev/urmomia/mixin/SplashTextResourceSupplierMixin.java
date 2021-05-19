package dev.urmomia.mixin;

import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {

    private boolean override = true;
    private final Random random = new Random();

    private final List<String> meteorSplashes = getMeteorSplashes();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<String> cir) {
        if (override) {
            cir.setReturnValue(meteorSplashes.get(random.nextInt(meteorSplashes.size())));
        }
        override = !override;
    }

    private static List<String> getMeteorSplashes() {
        return Arrays.asList(
                "§dbased",
                ":EZ:",
                ":kekw:",
                "hi!",
                "cat",
                "monkey",
                "§6follow honsda.github.io uwu",
                "§5Urmomia on top!",
                "§5based utility mod",
                "do you play in §9OpenAnarchy.org§r?",
                "§echeesecake butt fart shrimp"
        );
    }

}
