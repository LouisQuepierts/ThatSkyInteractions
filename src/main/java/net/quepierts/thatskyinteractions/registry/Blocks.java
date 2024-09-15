package net.quepierts.thatskyinteractions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.*;

public class Blocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(
            BuiltInRegistries.BLOCK, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<Block, WingOfLightBlock> WING_OF_LIGHT = REGISTER.register(
            "wing_of_light", () -> new WingOfLightBlock(
                    BlockBehaviour.Properties.of()
                            .strength(-1.0F, 3600000.8F)
                            .mapColor(MapColor.COLOR_YELLOW)
                            .noLootTable()
                            .noCollission()
                            .noOcclusion()
                            .isValidSpawn(net.minecraft.world.level.block.Blocks::never)
                            .noTerrainParticles()
                            .pushReaction(PushReaction.BLOCK)
                            .lightLevel(blockState -> 15)
            )
    );
    public static final DeferredHolder<Block, CloudBlock> CLOUD = REGISTER.register(
            "cloud", () -> new CloudBlock(
                    BlockBehaviour.Properties.of()
                            .strength(-1.0F, 3600000.8F)
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .noLootTable()
                            .noCollission()
                            .noOcclusion()
                            .isValidSpawn(net.minecraft.world.level.block.Blocks::never)
                            .noTerrainParticles()
                            .pushReaction(PushReaction.BLOCK)
            )
    );

    public static final DeferredHolder<Block, ColoredCloudBlock> COLORED_CLOUD = REGISTER.register(
            "colored_cloud", () -> new ColoredCloudBlock(
                    BlockBehaviour.Properties.of()
                            .strength(-1.0F, 3600000.8F)
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .noLootTable()
                            .noCollission()
                            .noOcclusion()
                            .isValidSpawn(net.minecraft.world.level.block.Blocks::never)
                            .noTerrainParticles()
                            .pushReaction(PushReaction.BLOCK)
            )
    );

    public static final DeferredHolder<Block, MuralBlock> MURAL = REGISTER.register(
            "mural", () -> new MuralBlock(
                    BlockBehaviour.Properties.of()
                            .strength(-1.0F, 3600000.8F)
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .noLootTable()
                            .noCollission()
                            .noOcclusion()
                            .isValidSpawn(net.minecraft.world.level.block.Blocks::never)
                            .noTerrainParticles()
                            .pushReaction(PushReaction.BLOCK)
            )
    );

    public static final DeferredHolder<Block, CandleClusterBlock> CANDLE_CLUSTER = REGISTER.register(
            "candle_cluster", () -> new CandleClusterBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_RED)
                            .noOcclusion()
                            .strength(0.1F)
                            .sound(SoundType.CANDLE)
                            .pushReaction(PushReaction.BLOCK)
                            .lightLevel(CandleClusterBlock.LIGHT_EMISSION)
            )
    );
}
