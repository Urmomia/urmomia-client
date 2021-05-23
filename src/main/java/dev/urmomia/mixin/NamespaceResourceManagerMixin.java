package dev.urmomia.mixin;

import dev.urmomia.MainClient;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** This mixin is only active when fabric-resource-loader mod is not present */
@Mixin(NamespaceResourceManager.class)
public class NamespaceResourceManagerMixin {
    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    private void onGetResource(Identifier id, CallbackInfoReturnable<Resource> info) {
        if (id.getNamespace().equals("urmomia-client")) {
            info.setReturnValue(new ResourceImpl("urmomia-client", id, MainClient.class.getResourceAsStream("/assets/urmomia-client/" + id.getPath()), null));
        }
        //this is a meteor skid what did you expect  nadsjdnjnwjnendjewsm q
        else if (id.getNamespace().equals("meteor-client")) {
            info.setReturnValue(new ResourceImpl("meteor-client", id, MainClient.class.getResourceAsStream("/assets/meteor-client/" + id.getPath()), null));
        }
    }
}
