package dev.welles.playerrenderdistance.mixin;

//? if >=26.1 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.welles.playerrenderdistance.PlayerRenderDistanceClient;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/^*
 * Client side, since 26.1: the far plane of the camera is four times the render distance, players further away
 * would be cut off.
 ^/
@Mixin(Camera.class)
abstract class CameraMixin {
    @ModifyExpressionValue(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F", ordinal = 0))
    private float playerrenderdistance$extendDepthFar(float depthFar) {
        return PlayerRenderDistanceClient.depthFar(depthFar);
    }
}
*///?}
