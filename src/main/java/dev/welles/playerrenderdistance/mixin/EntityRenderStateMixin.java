package dev.welles.playerrenderdistance.mixin;

//? if >=1.21.9 {
/*import dev.welles.playerrenderdistance.OtherPlayerState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/^* Client side, since 1.21.9: remembers in the render state whether it belongs to another player. ^/
@Mixin(EntityRenderState.class)
abstract class EntityRenderStateMixin implements OtherPlayerState {
    @Unique
    private boolean playerrenderdistance$otherPlayer;

    @Override
    public boolean playerrenderdistance$isOtherPlayer() {
        return playerrenderdistance$otherPlayer;
    }

    @Override
    public void playerrenderdistance$setOtherPlayer(boolean otherPlayer) {
        playerrenderdistance$otherPlayer = otherPlayer;
    }
}
*///?}
