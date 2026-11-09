package dev.welles.playerrenderdistance;

//? if neoforge {
/*import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = PlayerRenderDistance.MOD_ID, dist = Dist.CLIENT)
public class PlayerRenderDistanceNeoForgeClient {
    public PlayerRenderDistanceNeoForgeClient(ModContainer container) {
        // "Config" button in the mod list
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, parent) -> new ConfigScreen(
            parent, FMLPaths.CONFIGDIR.get().resolve(PlayerRenderDistance.MOD_ID + ".properties"), Component.literal(mod.getModInfo().getDisplayName()),
            RenderDistanceConfig.defaults(), RenderDistanceConfig.RANGES, RenderDistanceConfig.CHOICES
        ));
    }
}
*///?}
