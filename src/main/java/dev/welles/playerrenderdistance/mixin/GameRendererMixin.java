package dev.welles.playerrenderdistance.mixin;

//? if <26.1 {
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.welles.playerrenderdistance.PlayerRenderDistanceClient;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Client side, before 26.1: the far plane of the camera is four times the render distance, players further away
 * would be cut off.
 */
@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
    @ModifyReturnValue(method = "getDepthFar", at = @At("RETURN"))
    private float playerrenderdistance$extendDepthFar(float depthFar) {
        return PlayerRenderDistanceClient.depthFar(depthFar);
    }
}
//?}
