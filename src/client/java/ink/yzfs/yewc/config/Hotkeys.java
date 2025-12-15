package ink.yzfs.yewc.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.IHotkey;

import java.util.List;

public class Hotkeys {
    public static final ConfigHotkey OPEN_CONFIG_GUI = new ConfigHotkey("打开配置界面", "Y,C", "打开YEWC配置界面的热键");
    public static final ConfigHotkey SET_STRUCTURE = new ConfigHotkey("设置结构", "Y,S", "在当前位置设置结构的热键");
    public static final ConfigHotkey CLEAR_STRUCTURE = new ConfigHotkey("清除结构", "Y,X", "清除当前位置结构的热键");
    public static final ConfigHotkey LIST_STRUCTURES = new ConfigHotkey("列出结构", "Y,L", "列出当前位置结构的热键");

    public static final List<ConfigHotkey> HOTKEY_LIST = ImmutableList.of(
            OPEN_CONFIG_GUI,
            SET_STRUCTURE,
            CLEAR_STRUCTURE,
            LIST_STRUCTURES
    );

    public static List<? extends IHotkey> getHotkeys() {
        return HOTKEY_LIST;
    }
}
