package ink.yzfs.yewc.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class YewcClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 只有在MaLiLib存在时才初始化GUI和热键
        if (FabricLoader.getInstance().isModLoaded("malilib")) {
            initMaLiLib();
        }
    }

    private void initMaLiLib() {
        try {
            fi.dy.masa.malilib.event.InitializationHandler.getInstance()
                    .registerInitializationHandler(new ink.yzfs.yewc.InitHandler());
        } catch (Exception e) {
            System.err.println("[YEWC] Failed to initialize MaLiLib integration: " + e.getMessage());
        }
    }
}
