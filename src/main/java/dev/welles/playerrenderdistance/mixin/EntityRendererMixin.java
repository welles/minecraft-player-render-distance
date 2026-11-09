package dev.welles.playerrenderdistance.mixin;

//? if >=1.21.9 {
/*import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.welles.playerrenderdistance.FarPlayers;
import dev.welles.playerrenderdistance.OtherPlayerState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.sugar.Local;

/^*
 * Client side, since 1.21.9: marks the render states of other players, which {@code EntityRenderDispatcherMixin} draws
 * closer when they are far away,.
 ^/
@Mixin(EntityRenderer.class)
abstract class EntityRendererMixin {
    @ModifyReturnValue(
        method = "createRenderState(Lnet/minecraft/world/entity/Entity;F)Lnet/minecraft/client/renderer/entity/state/EntityRenderState;",
        at = @At("RETURN")
    )
    private EntityRenderState playerrenderdistance$markOtherPlayers(EntityRenderState state, @Local(argsOnly = true) Entity entity) {
        ((OtherPlayerState) state).playerrenderdistance$setOtherPlayer(FarPlayers.isOtherPlayer(entity));
        return state;
    }
}
*///?}
