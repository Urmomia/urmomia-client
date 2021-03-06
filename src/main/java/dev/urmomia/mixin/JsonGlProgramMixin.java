package dev.urmomia.mixin;

import dev.urmomia.utils.render.Outlines;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(JsonGlProgram.class)
public class JsonGlProgramMixin {
    @ModifyVariable(method = "<init>", at = @At("STORE"))
    private Identifier onInitNewIdentifierModifyVariable(Identifier identifier) {
        if (Outlines.loadingOutlineShader && identifier.getPath().equals("shaders/program/my_entity_outline.json")) {
            return new Identifier("urmomia-client", identifier.getPath());
        }

        return identifier;
    }

    @ModifyVariable(method = "getShader", at = @At("STORE"))
    private static Identifier onGetShaderNewIdentifierModifyVariable(Identifier identifier) {
        if (Outlines.loadingOutlineShader && identifier.getPath().equals("shaders/program/my_entity_sobel.fsh")) {
            return new Identifier("urmomia-client", identifier.getPath());
        }

        return identifier;
    }
}
