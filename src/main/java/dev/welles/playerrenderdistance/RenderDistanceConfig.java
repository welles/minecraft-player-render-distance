package dev.welles.playerrenderdistance;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Settings stored in {@code config/playerrenderdistance.properties}.
 * The file is re-read automatically when it changes on disk, checked at most once a second.
 */
public final class RenderDistanceConfig {
    /** Largest range in blocks, far beyond what anyone needs and still safe to square. */
    public static final int MAX_RANGE = 100_000;
    /** The allowed values of each number setting; values outside are clamped. */
    public static final Map<String, Range> RANGES = Map.of(
        "trackingRange", new Range(0, MAX_RANGE),
        "renderRange", new Range(0, MAX_RANGE)
    );
    /** The allowed values of each setting with a fixed set of them, none so far. */
    public static final Map<String, List<String>> CHOICES = Map.of();

    public int trackingRange = 1024;
    public int renderRange = 1024;

    private static final String HEADER = """
        Player Render Distance configuration
        Changes are picked up automatically, no restart required.

        trackingRange   Server: the server sends every player the other players in the same dimension within this many
                        blocks, also beyond the view distance and outside the chunks the player has loaded. 0 = like
                        vanilla, only within the view distance. 0 to 100000. Only used on the server (and in
                        singleplayer, i.e. LAN worlds).
        renderRange     Client: other players within this many blocks are drawn, also outside the loaded chunks and
                        beyond the entity distance in the video settings. 0 = like vanilla. 0 to 100000.""";

    private static final long CHECK_INTERVAL_NANOS = 1_000_000_000L;

    private static Path file;
    private static FileTime loadedAt;
    private static long checkedAt;
    private static volatile RenderDistanceConfig current = new RenderDistanceConfig();

    public static synchronized void init(Path configDir) {
        file = configDir.resolve(PlayerRenderDistance.MOD_ID + ".properties");
        reload();
        checkedAt = System.nanoTime();
    }

    /**
     * Returns the current settings. Called for every player in every frame and on every tracking update, so the file
     * is only checked for changes once a second.
     */
    public static RenderDistanceConfig get() {
        if (file != null && System.nanoTime() - checkedAt > CHECK_INTERVAL_NANOS) check();
        return current;
    }

    private static synchronized void check() {
        if (System.nanoTime() - checkedAt <= CHECK_INTERVAL_NANOS) return;
        checkedAt = System.nanoTime();
        try {
            FileTime modified = Files.exists(file) ? Files.getLastModifiedTime(file) : null;
            if (modified == null || !modified.equals(loadedAt)) reload();
        } catch (IOException e) {
            PlayerRenderDistance.LOGGER.warn("Could not check config file {}", file, e);
        }
    }

    private static void reload() {
        RenderDistanceConfig config = new RenderDistanceConfig();
        Properties props = new Properties();
        boolean complete = false;

        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                props.load(reader);
                complete = config.read(props);
            } catch (IOException | RuntimeException e) {
                PlayerRenderDistance.LOGGER.error("Could not read config file {}, using defaults", file, e);
                current = config;
                return;
            }
        }

        if (!complete) {
            try {
                Files.createDirectories(file.getParent());
                try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                    config.write(writer);
                }
            } catch (IOException e) {
                PlayerRenderDistance.LOGGER.error("Could not write config file {}", file, e);
            }
        }

        try {
            loadedAt = Files.exists(file) ? Files.getLastModifiedTime(file) : null;
        } catch (IOException e) {
            loadedAt = null;
        }
        current = config;
    }

    /** Applies all valid values present in {@code props}; returns whether every key was present. */
    private boolean read(Properties props) {
        trackingRange = (int) Math.round(readNumber(props, "trackingRange", trackingRange, true));
        renderRange = (int) Math.round(readNumber(props, "renderRange", renderRange, true));
        return props.keySet().containsAll(toProperties().keySet());
    }

    /** Reads a number clamped to its allowed values; keeps the fallback if it is missing or not a number. */
    private static double readNumber(Properties props, String key, double fallback, boolean integer) {
        String value = props.getProperty(key);
        if (value == null) return fallback;
        try {
            double number = integer ? Integer.parseInt(value.trim()) : Double.parseDouble(value.trim());
            if (Double.isFinite(number)) return RANGES.get(key).clamp(number);
        } catch (NumberFormatException ignored) {
            // logged below
        }
        PlayerRenderDistance.LOGGER.warn("Invalid {} '{}' in {}, using {}", key, value.trim(), file, fallback);
        return fallback;
    }

    private void write(Writer writer) throws IOException {
        for (String line : HEADER.split("\n")) writer.write(("# " + line).stripTrailing() + "\n");
        writer.write("\n");
        for (var entry : toProperties().entrySet()) {
            writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
        }
    }

    /** The default value of every setting, as written to the file. */
    public static Map<String, String> defaults() {
        return new RenderDistanceConfig().toProperties();
    }

    private Map<String, String> toProperties() {
        Map<String, String> props = new LinkedHashMap<>();
        props.put("trackingRange", String.valueOf(trackingRange));
        props.put("renderRange", String.valueOf(renderRange));
        return props;
    }

    public record Range(double min, double max) {
        public double clamp(double value) {
            return Math.clamp(value, min, max);
        }

        public boolean contains(double value) {
            return value >= min && value <= max;
        }
    }
}
