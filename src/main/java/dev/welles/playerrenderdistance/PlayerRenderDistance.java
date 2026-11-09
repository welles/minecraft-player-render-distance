package dev.welles.playerrenderdistance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Lets players see each other far beyond the view distance. The server sends every player the other players within
 * {@link RenderDistanceConfig#trackingRange} ({@code mixin.TrackedEntityMixin}), the client draws them up to
 * {@link RenderDistanceConfig#renderRange} even outside its loaded chunks ({@link PlayerRenderDistanceClient}).
 */
public final class PlayerRenderDistance {
    public static final String MOD_ID = "playerrenderdistance";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private PlayerRenderDistance() {}

    public static void init(Path configDir) {
        RenderDistanceConfig.init(configDir);
        LOGGER.info("Player Render Distance loaded");
    }
}
