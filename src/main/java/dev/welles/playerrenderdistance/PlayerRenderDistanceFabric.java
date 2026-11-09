package dev.welles.playerrenderdistance;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class PlayerRenderDistanceFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        PlayerRenderDistance.init(FabricLoader.getInstance().getConfigDir());
    }
}
//?}
