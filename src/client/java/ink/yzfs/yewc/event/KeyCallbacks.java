package ink.yzfs.yewc.event;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.util.InfoUtils;
import ink.yzfs.yewc.config.Configs;
import ink.yzfs.yewc.config.Hotkeys;
import ink.yzfs.yewc.gui.GuiConfigs;
import ink.yzfs.yewc.util.StructureUtils;
import net.minecraft.client.MinecraftClient;

public class KeyCallbacks {
    public static void init() {
        Hotkeys.OPEN_CONFIG_GUI.getKeybind().setCallback(new CallbackOpenConfigGui());
        Hotkeys.SET_STRUCTURE.getKeybind().setCallback(new CallbackSetStructure());
        Hotkeys.CLEAR_STRUCTURE.getKeybind().setCallback(new CallbackClearStructure());
        Hotkeys.LIST_STRUCTURES.getKeybind().setCallback(new CallbackListStructures());
    }

    private static class CallbackOpenConfigGui implements IHotkeyCallback {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {
            GuiBase.openGui(new GuiConfigs());
            return true;
        }
    }

    private static class CallbackSetStructure implements IHotkeyCallback {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.world != null) {
                String structureId = Configs.Generic.STRUCTURE_TYPE.getStringValue();
                boolean generateBuilding = Configs.Generic.GENERATE_BUILDING.getBooleanValue();
                
                String result = StructureUtils.setStructure(mc, structureId, generateBuilding);
                InfoUtils.showGuiOrInGameMessage(MessageType.SUCCESS, result);
            }
            return true;
        }
    }

    private static class CallbackClearStructure implements IHotkeyCallback {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.world != null) {
                String result = StructureUtils.clearStructures(mc);
                InfoUtils.showGuiOrInGameMessage(MessageType.WARNING, result);
            }
            return true;
        }
    }

    private static class CallbackListStructures implements IHotkeyCallback {
        @Override
        public boolean onKeyAction(KeyAction action, IKeybind key) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null && mc.world != null) {
                String result = StructureUtils.listStructures(mc);
                InfoUtils.showGuiOrInGameMessage(MessageType.INFO, result);
            }
            return true;
        }
    }
}
