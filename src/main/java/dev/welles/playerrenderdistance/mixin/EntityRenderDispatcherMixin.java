package dev.welles.playerrenderdistance.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.welles.playerrenderdistance.FarPlayers;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

//? if >=1.21.9 {
/*import dev.welles.playerrenderdistance.OtherPlayerState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
//? if >=26.1 {
/^import net.minecraft.client.renderer.state.level.CameraRenderState;
^///?} else {
import net.minecraft.client.renderer.state.CameraRenderState;
//?}
*///?} else {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
//?}

/**
 * Client side: draws other players far away closer to the camera and smaller, out of the fog, see {@link FarPlayers}.
 * Everything the dispatcher draws for the player (model, name, fire, shadow) is moved and shrunk alike.
 */
@Mixin(EntityRenderDispatcher.class)
abstract class EntityRenderDispatcherMixin {
    //? if >=1.21.9 {
    /*@WrapMethod(method = "submit")
    private void playerrenderdistance$drawCloser(
        EntityRenderState state, CameraRenderState camera, double x, double y, double z, PoseStack poseStack, SubmitNodeCollector collector,
        Operation<Void> original
    ) {
        double scale = ((OtherPlayerState) state).playerrenderdistance$isOtherPlayer() ? FarPlayers.scale(x, y, z) : 1;
        if (scale >= 1) {
            original.call(state, camera, x, y, z, poseStack, collector);
            return;
        }
        poseStack.pushPose();
        poseStack.translate(x * scale, y * scale, z * scale);
        poseStack.scale((float) scale, (float) scale, (float) scale);
        int light = state.lightCoords;
        state.lightCoords = FarPlayers.light(light);
        original.call(state, camera, 0.0, 0.0, 0.0, poseStack, collector);
        state.lightCoords = light;
        poseStack.popPose();
    }
    *///?} elif >=1.21.2 {
    /*@WrapMethod(method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void playerrenderdistance$drawCloser(
        Entity entity, double x, double y, double z, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light,
        Operation<Void> original
    ) {
        double scale = FarPlayers.isOtherPlayer(entity) ? FarPlayers.scale(x, y, z) : 1;
        if (scale >= 1) {
            original.call(entity, x, y, z, partialTick, poseStack, buffers, light);
            return;
        }
        poseStack.pushPose();
        poseStack.translate(x * scale, y * scale, z * scale);
        poseStack.scale((float) scale, (float) scale, (float) scale);
        original.call(entity, 0.0, 0.0, 0.0, partialTick, poseStack, buffers, FarPlayers.light(light));
        poseStack.popPose();
    }
    *///?} else {
    @WrapMethod(method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void playerrenderdistance$drawCloser(
        Entity entity, double x, double y, double z, float rotation, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light,
        Operation<Void> original
    ) {
        double scale = FarPlayers.isOtherPlayer(entity) ? FarPlayers.scale(x, y, z) : 1;
        if (scale >= 1) {
            original.call(entity, x, y, z, rotation, partialTick, poseStack, buffers, light);
            return;
        }
        poseStack.pushPose();
        poseStack.translate(x * scale, y * scale, z * scale);
        poseStack.scale((float) scale, (float) scale, (float) scale);
        original.call(entity, 0.0, 0.0, 0.0, rotation, partialTick, poseStack, buffers, FarPlayers.light(light));
        poseStack.popPose();
    }
    //?}
}
