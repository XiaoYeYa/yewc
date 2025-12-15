package ink.yzfs.yewc;

import ink.yzfs.yewc.command.SetStructureCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Yewc implements ModInitializer {
    public static final String MOD_ID = "yewc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Yewc mod initializing...");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SetStructureCommand.register(dispatcher, registryAccess);
            LOGGER.info("Registered /setstructure command");
        });
    }
}
