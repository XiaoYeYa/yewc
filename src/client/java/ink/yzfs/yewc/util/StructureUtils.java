package ink.yzfs.yewc.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class StructureUtils {

    public static String setStructure(MinecraftClient mc, String structureId, boolean generateBuilding) {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return "§c玩家不存在";
        }

        BlockPos playerPos = player.getBlockPos();
        ChunkPos chunkPos = new ChunkPos(playerPos);

        String command;
        if (generateBuilding) {
            command = String.format("setstructure %s", structureId);
        } else {
            command = String.format("setstructure %s", structureId);
        }

        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendChatCommand(command);
            return String.format("§a正在设置结构: %s 于区块 [%d, %d]", structureId, chunkPos.x, chunkPos.z);
        }

        return "§c无法发送命令";
    }

    public static String clearStructures(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return "§c玩家不存在";
        }

        BlockPos playerPos = player.getBlockPos();
        ChunkPos chunkPos = new ChunkPos(playerPos);

        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendChatCommand("setstructure clear");
            return String.format("§a正在清除区块 [%d, %d] 的结构", chunkPos.x, chunkPos.z);
        }

        return "§c无法发送命令";
    }

    public static String listStructures(MinecraftClient mc) {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return "§c玩家不存在";
        }

        BlockPos playerPos = player.getBlockPos();
        ChunkPos chunkPos = new ChunkPos(playerPos);

        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendChatCommand("setstructure list");
            return String.format("§a正在列出区块 [%d, %d] 的结构", chunkPos.x, chunkPos.z);
        }

        return "§c无法发送命令";
    }
}
