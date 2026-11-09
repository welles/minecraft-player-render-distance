package dev.welles.playerrenderdistance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.entity.Entity;

/**
 * Client side: draws other players beyond half the render distance as if they were closer. Vanilla fades everything
 * into the fog towards the edge of the render distance, and Distant Horizons fades it into its LODs, so a player
 * further away would be invisible even when drawn. Instead the player is moved towards the camera to half the render
 * distance and shrunk by the same factor: on screen it has exactly the same size and position, only out of the fog.
 * Terrain between there and the player no longer hides it, which only matters in the outer half of the render
 * distance, where the terrain is fading into the fog anyway.
 */
public final class FarPlayers {
    /** The closest players are drawn as if they were here, in blocks, also with a tiny render distance. */
    private static final double MIN_DISTANCE = 16;
    /** Packed light with full sky light and no block light, for players outside the loaded chunks. */
    private static final int SKY_LIGHT = 15 << 20;

    private FarPlayers() {}

    /** Whether the entity is another player that is drawn closer than it is when it is far away. */
    public static boolean isOtherPlayer(Entity entity) {
        return entity instanceof RemotePlayer && RenderDistanceConfig.get().renderRange > 0;
    }

    /**
     * The factor by which a player at the given position relative to the camera is moved towards it and shrunk; 1 if
     * it is close enough to be drawn where it is.
     */
    public static double scale(double x, double y, double z) {
        double distance = Math.sqrt(x * x + y * y + z * z);
        double drawnAt = Math.max(MIN_DISTANCE, Minecraft.getInstance().options.getEffectiveRenderDistance() * 16 / 2.0);
        return distance > drawnAt ? drawnAt / distance : 1;
    }

    /**
     * The light a player far away is drawn with. Outside the loaded chunks, and at their edge before their light is
     * known, the client has no light for the player and it would be black; there it gets the light of the open sky,
     * which still follows the time of day.
     */
    public static int light(int light) {
        return light == 0 ? SKY_LIGHT : light;
    }
}
