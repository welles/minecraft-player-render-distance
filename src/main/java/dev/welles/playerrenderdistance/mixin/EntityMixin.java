package dev.welles.playerrenderdistance.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.welles.playerrenderdistance.PlayerRenderDistanceClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Client side: vanilla draws entities only up to a distance from their size and the entity distance in the video
 * settings, 64 blocks for a player at 100%. Other players are drawn within the render range.
 */
@Mixin(Entity.class)
abstract class EntityMixin {
    @ModifyReturnValue(method = "shouldRenderAtSqrDistance", at = @At("RETURN"))
    private boolean playerrenderdistance$extendRenderDistance(boolean shouldRender, @Local(argsOnly = true) double distanceSquared) {
        return shouldRender || PlayerRenderDistanceClient.isInRange((Entity) (Object) this, distanceSquared);
    }
}
