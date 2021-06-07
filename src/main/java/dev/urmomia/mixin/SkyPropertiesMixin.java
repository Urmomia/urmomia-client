package dev.urmomia.mixin;

import dev.urmomia.systems.modules.Modules;
import dev.urmomia.systems.modules.render.Fullbright;
import net.minecraft.client.render.SkyProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkyProperties.class)
public class SkyPropertiesMixin {

    @Inject(method = "shouldBrightenLighting", at = @At(value = "HEAD"), cancellable = true)
    private void onShouldBrightenLighting(CallbackInfoReturnable<Boolean> cir) {
        Fullbright fullbright = Modules.get().get(Fullbright.class);

        if((fullbright.mode.get() == Fullbright.Mode.Luminance) && Fullbright.isEnabled()) {
            cir.setReturnValue(true);
        }
    }

}