package dev.welles.playerrenderdistance.mixin;

//? if >=26.2 {
/*import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.welles.playerrenderdistance.PlayerRenderDistanceClient;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/^*
 * Client side, since 26.2: vanilla only draws entities in chunk sections that are loaded and compiled. Other players
 * are drawn anywhere, also beyond the render distance.
 ^/
@Mixin(LevelExtractor.class)
abstract class LevelExtractorMixin {
    //? if >=26.3 {
    /^@ModifyExpressionValue(
        method = "isEntityVisible",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiledAndVisible(Lnet/minecraft/core/BlockPos;J)Z")
    )
    ^///?} else {
    @ModifyExpressionValue(
        method = "isEntityVisible",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;isSectionCompiledAndVisible(Lnet/minecraft/core/BlockPos;)Z")
    )
    //?}
    private boolean playerrenderdistance$drawPlayersAnywhere(boolean compiled, @Local(argsOnly = true) Entity entity) {
        return compiled || PlayerRenderDistanceClient.isDrawnOutsideLoadedChunks(entity);
    }
}
*///?}
