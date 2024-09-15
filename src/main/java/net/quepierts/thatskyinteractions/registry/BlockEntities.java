package net.quepierts.thatskyinteractions.registry;

import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.*;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WingOfLightBlockEntity>> WING_OF_LIGHT = REGISTER.register(
            "wing_of_light_entity", () -> BlockEntityType.Builder.of(WingOfLightBlockEntity::new, Blocks.WING_OF_LIGHT.get()).build(DSL.remainderType())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SimpleCloudBlockEntity>> SIMPLE_CLOUD = REGISTER.register(
            "simple_cloud", () -> BlockEntityType.Builder.of(SimpleCloudBlockEntity::new, Blocks.CLOUD.get()).build(DSL.remainderType())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ColoredCloudBlockEntity>> COLORED_CLOUD = REGISTER.register(
            "colored_cloud", () -> BlockEntityType.Builder.of(ColoredCloudBlockEntity::new, Blocks.COLORED_CLOUD.get()).build(DSL.remainderType())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MuralBlockEntity>> MURAL = REGISTER.register(
            "mural", () -> BlockEntityType.Builder.of(MuralBlockEntity::new, Blocks.MURAL.get()).build(DSL.remainderType())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CandleClusterBlockEntity>> CANDLE_CLUSTER = REGISTER.register(
            "candle_cluster", () -> BlockEntityType.Builder.of(CandleClusterBlockEntity::new, Blocks.CANDLE_CLUSTER.get()).build(DSL.remainderType())
    );
}
