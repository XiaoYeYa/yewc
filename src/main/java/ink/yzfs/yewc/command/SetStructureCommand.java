package ink.yzfs.yewc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.structure.Structure;

import java.util.Map;
import java.util.Optional;

public class SetStructureCommand {

    public static final SuggestionProvider<ServerCommandSource> STRUCTURE_SUGGESTIONS = (context, builder) -> {
        return CommandSource.suggestIdentifiers(
                context.getSource().getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE).getKeys()
                        .stream().map(RegistryKey::getValue),
                builder
        );
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(CommandManager.literal("setstructure")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("structure", IdentifierArgumentType.identifier())
                        .suggests(STRUCTURE_SUGGESTIONS)
                        .executes(SetStructureCommand::executeSet))
                .then(CommandManager.literal("clear")
                        .executes(SetStructureCommand::executeClear))
                .then(CommandManager.literal("list")
                        .executes(SetStructureCommand::executeList)));
    }

    private static int executeSet(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(playerPos);

        try {
            Identifier structureId = IdentifierArgumentType.getIdentifier(context, "structure");
            Registry<Structure> structureRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
            
            RegistryKey<Structure> structureKey = RegistryKey.of(RegistryKeys.STRUCTURE, structureId);
            Optional<RegistryEntry.Reference<Structure>> optionalEntry = structureRegistry.getOptional(structureKey);
            
            if (optionalEntry.isEmpty()) {
                source.sendError(Text.literal("§c未找到结构: " + structureId));
                return 0;
            }
            
            RegistryEntry.Reference<Structure> structureEntry = optionalEntry.get();
            Structure structure = structureEntry.value();
            Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

            // 使用世界种子确保一致性
            long seed = world.getSeed();
            ChunkRandom random = new ChunkRandom(new CheckedRandom(seed));
            random.setCarverSeed(seed, chunkPos.x, chunkPos.z);

            // 创建结构起点
            StructureStart structureStart = structure.createStructureStart(
                    structureEntry,
                    world.getRegistryKey(),
                    world.getRegistryManager(),
                    world.getChunkManager().getChunkGenerator(),
                    world.getChunkManager().getChunkGenerator().getBiomeSource(),
                    world.getChunkManager().getNoiseConfig(),
                    world.getStructureTemplateManager(),
                    seed,
                    chunkPos,
                    0,
                    world,
                    biome -> true  // 允许任何生物群系
            );

            if (!structureStart.hasChildren()) {
                source.sendError(Text.literal("§c无法在此位置生成结构: " + structureId + " (结构生成失败)"));
                return 0;
            }

            // 设置结构起点到区块
            chunk.setStructureStart(structure, structureStart);

            // 添加结构引用
            BlockBox boundingBox = structureStart.getBoundingBox();
            
            // 为所有被结构覆盖的区块添加引用
            int minChunkX = boundingBox.getMinX() >> 4;
            int maxChunkX = boundingBox.getMaxX() >> 4;
            int minChunkZ = boundingBox.getMinZ() >> 4;
            int maxChunkZ = boundingBox.getMaxZ() >> 4;
            
            for (int cx = minChunkX; cx <= maxChunkX; cx++) {
                for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                    Chunk refChunk = world.getChunk(cx, cz);
                    refChunk.addStructureReference(structure, chunkPos.toLong());
                }
            }

            // 放置结构建筑
            // 遍历所有结构片段并放置
            for (StructurePiece piece : structureStart.getChildren()) {
                BlockBox pieceBoundingBox = piece.getBoundingBox();
                
                // 计算片段所在的区块范围
                int pieceMinCX = pieceBoundingBox.getMinX() >> 4;
                int pieceMaxCX = pieceBoundingBox.getMaxX() >> 4;
                int pieceMinCZ = pieceBoundingBox.getMinZ() >> 4;
                int pieceMaxCZ = pieceBoundingBox.getMaxZ() >> 4;
                
                for (int cx = pieceMinCX; cx <= pieceMaxCX; cx++) {
                    for (int cz = pieceMinCZ; cz <= pieceMaxCZ; cz++) {
                        ChunkPos pieceChunkPos = new ChunkPos(cx, cz);
                        piece.generate(
                                world,
                                world.getStructureAccessor(),
                                world.getChunkManager().getChunkGenerator(),
                                random,
                                pieceBoundingBox,
                                pieceChunkPos,
                                playerPos  // 使用玩家位置作为中心点
                        );
                    }
                }
            }

            // 标记区块已修改
            chunk.markNeedsSaving();

            source.sendFeedback(() -> Text.literal("§a已在区块 [" + chunkPos.x + ", " + chunkPos.z + "] 生成结构: " + structureId), true);
            source.sendFeedback(() -> Text.literal("§a结构边界: " + boundingBox.getMinX() + "," + boundingBox.getMinY() + "," + boundingBox.getMinZ() + 
                    " -> " + boundingBox.getMaxX() + "," + boundingBox.getMaxY() + "," + boundingBox.getMaxZ()), false);

            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("§c设置结构失败: " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }

    private static int executeClear(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(playerPos);

        try {
            Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);

            Map<Structure, StructureStart> structureStarts = chunk.getStructureStarts();
            int count = structureStarts.size();
            structureStarts.clear();

            source.sendFeedback(() -> Text.literal("§a已清除区块 [" + chunkPos.x + ", " + chunkPos.z + "] 的 " + count + " 个结构"), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("§c清除结构失败: " + e.getMessage()));
            return 0;
        }
    }

    private static int executeList(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerWorld world = source.getWorld();
        BlockPos playerPos = BlockPos.ofFloored(source.getPosition());
        ChunkPos chunkPos = new ChunkPos(playerPos);

        try {
            Chunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
            Map<Structure, StructureStart> structureStarts = chunk.getStructureStarts();

            if (structureStarts.isEmpty()) {
                source.sendFeedback(() -> Text.literal("§e区块 [" + chunkPos.x + ", " + chunkPos.z + "] 没有结构"), false);
            } else {
                source.sendFeedback(() -> Text.literal("§a区块 [" + chunkPos.x + ", " + chunkPos.z + "] 的结构:"), false);
                for (Map.Entry<Structure, StructureStart> entry : structureStarts.entrySet()) {
                    StructureStart start = entry.getValue();
                    if (start != null && start.hasChildren()) {
                        String structureName = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE)
                                .getKey(entry.getKey())
                                .map(key -> key.getValue().toString())
                                .orElse("unknown");
                        source.sendFeedback(() -> Text.literal("  §7- " + structureName), false);
                    }
                }
            }
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("§c列出结构失败: " + e.getMessage()));
            return 0;
        }
    }
}
