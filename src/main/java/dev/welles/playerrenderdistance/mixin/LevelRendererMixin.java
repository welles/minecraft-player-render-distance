package dev.welles.playerrenderdistance.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.welles.playerrenderdistance.PlayerRenderDistanceClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Client side, before 26.2: vanilla only draws entities in chunk sections that are loaded and compiled. Other players
 * are drawn anywhere, also beyond the render distance.
 */
@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin {
    //? if >=1.21.11 {
    /*@ModifyExpressionValue(
        method = "extractVisibleEntities",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiledAndVisible(Lnet/minecraft/core/BlockPos;)Z")
    )
    *///?} elif >=1.21.10 {
    /*@ModifyExpressionValue(
        method = "extractVisibleEntities",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiled(Lnet/minecraft/core/BlockPos;)Z")
    )
    *///?} elif >=1.21.2 {
    /*@ModifyExpressionValue(
        method = "collectVisibleEntities",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiled(Lnet/minecraft/core/BlockPos;)Z")
    )
    *///?} else {
    @ModifyExpressionValue(
        method = "renderLevel",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiled(Lnet/minecraft/core/BlockPos;)Z")
    )
    //?}
    private boolean playerrenderdistance$drawPlayersAnywhere(boolean compiled, @Local Entity entity) {
        return compiled || PlayerRenderDistanceClient.isDrawnOutsideLoadedChunks(entity);
    }
}
