package dev.welles.playerrenderdistance;

import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

/** Client side: which players are drawn although vanilla would not draw them. */
public final class PlayerRenderDistanceClient {
    /**
     * Added to the render range for the far plane of the camera: the range is measured horizontally like the
     * tracking range, a player high above or below is further away.
     */
    private static final float DEPTH_MARGIN = 512;

    private PlayerRenderDistanceClient() {}

    /** Whether the entity is another player within the render range, at the given squared distance from the camera. */
    public static boolean isInRange(Entity entity, double distanceSquared) {
        if (!(entity instanceof RemotePlayer)) return false;
        double range = RenderDistanceConfig.get().renderRange;
        return distanceSquared < range * range;
    }

    /**
     * Whether the entity is another player that is drawn even where its chunk section is not loaded or not compiled
     * (yet). Players are always ticked by the client, wherever they are, so they move there as they should.
     */
    public static boolean isDrawnOutsideLoadedChunks(Entity entity) {
        return entity instanceof RemotePlayer && RenderDistanceConfig.get().renderRange > 0;
    }

    /** The far plane of the camera must reach the players in the render range, or they are cut off. */
    public static float depthFar(float vanilla) {
        int range = RenderDistanceConfig.get().renderRange;
        return range > 0 ? Math.max(vanilla, range + DEPTH_MARGIN) : vanilla;
    }
}
