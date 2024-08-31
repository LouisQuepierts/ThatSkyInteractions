package net.quepierts.thatskyinteractions.registry;

import com.mojang.datafixers.DSL;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.CloudBlockEntity;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WingOfLightBlockEntity>> WING_OF_LIGHT_BE = REGISTER.register(
            "wing_of_light_entity", () -> BlockEntityType.Builder.of(WingOfLightBlockEntity::new, Blocks.WING_OF_LIGHT.get()).build(DSL.remainderType())
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CloudBlockEntity>> CLOUD_BE = REGISTER.register(
            "cloud", () -> BlockEntityType.Builder.of(CloudBlockEntity::new, Blocks.CLOUD.get()).build(DSL.remainderType())
    );
}
