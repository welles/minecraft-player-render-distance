package dev.welles.playerrenderdistance.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.welles.playerrenderdistance.RenderDistanceConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Server side: decides which players are sent to which other players. Vanilla sends an entity to a player only within
 * the player's view distance and only while the entity's chunk is loaded by the player; for players this extends to
 * the tracking range, loaded or not.
 */
@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
abstract class TrackedEntityMixin {
    @Shadow
    @Final
    Entity entity;

    /** The horizontal distance within which the entity is sent: the smaller of its tracking range and the view distance. */
    @ModifyExpressionValue(method = "updatePlayer", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int playerrenderdistance$extendRange(int range) {
        return entity instanceof ServerPlayer ? Math.max(range, RenderDistanceConfig.get().trackingRange) : range;
    }

    /** Whether the receiving player has the entity's chunk loaded. */
    @ModifyExpressionValue(
        method = "updatePlayer",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ChunkMap;isChunkTracked(Lnet/minecraft/server/level/ServerPlayer;II)Z")
    )
    private boolean playerrenderdistance$ignoreChunks(boolean tracked) {
        return tracked || entity instanceof ServerPlayer && RenderDistanceConfig.get().trackingRange > 0;
    }
}
