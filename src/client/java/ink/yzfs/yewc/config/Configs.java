package ink.yzfs.yewc.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.JsonUtils;
import ink.yzfs.yewc.Reference;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

public class Configs implements IConfigHandler {
    private static final String CONFIG_FILE_NAME = Reference.MOD_ID + ".json";

    public static class Generic {
        public static final ConfigString STRUCTURE_TYPE = new ConfigString("结构类型", "minecraft:swamp_hut", "要设置的结构类型ID (例如 minecraft:swamp_hut)");
        public static final ConfigBoolean GENERATE_BUILDING = new ConfigBoolean("生成建筑", true, "是否生成结构建筑物");

        public static final ImmutableList<IConfigBase> OPTIONS = ImmutableList.of(
                STRUCTURE_TYPE,
                GENERATE_BUILDING
        );
    }

    private static File getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    public static void loadFromFile() {
        File configFile = new File(getConfigDirectory(), CONFIG_FILE_NAME);

        if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
            JsonElement element = JsonUtils.parseJsonFile(configFile);

            if (element != null && element.isJsonObject()) {
                JsonObject root = element.getAsJsonObject();

                ConfigUtils.readConfigBase(root, "Generic", Generic.OPTIONS);
            }
        }
    }

    public static void saveToFile() {
        File dir = getConfigDirectory();

        if ((dir.exists() && dir.isDirectory()) || dir.mkdirs()) {
            JsonObject root = new JsonObject();

            ConfigUtils.writeConfigBase(root, "Generic", Generic.OPTIONS);

            JsonUtils.writeJsonToFile(root, new File(dir, CONFIG_FILE_NAME));
        }
    }

    @Override
    public void load() {
        loadFromFile();
    }

    @Override
    public void save() {
        saveToFile();
    }
}
