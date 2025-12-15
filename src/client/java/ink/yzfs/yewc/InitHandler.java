package ink.yzfs.yewc;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import ink.yzfs.yewc.config.Configs;
import ink.yzfs.yewc.event.InputHandler;
import ink.yzfs.yewc.event.KeyCallbacks;

public class InitHandler implements IInitializationHandler {
    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(Reference.MOD_ID, new Configs());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());

        KeyCallbacks.init();
    }
}
