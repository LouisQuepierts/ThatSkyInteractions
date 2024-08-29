package net.quepierts.thatskyinteractions.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.WingOfLightBlock;

public class Blocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(
            BuiltInRegistries.BLOCK, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<Block, WingOfLightBlock> WING_OF_LIGHT = REGISTER.register(
            "wing_of_light", () -> new WingOfLightBlock()
    );
}
