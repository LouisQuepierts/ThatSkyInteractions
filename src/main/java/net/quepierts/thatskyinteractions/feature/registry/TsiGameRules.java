package net.quepierts.thatskyinteractions.feature.registry;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import lombok.experimental.UtilityClass;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

import java.util.function.Supplier;

@UtilityClass
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class TsiGameRules {

    public static final DeferredRegister<GameRule<?>> REGISTER
            = DeferredRegister.create(Registries.GAME_RULE, ThatSkyInteractions.MODID);

    public static final Supplier<GameRule<Boolean>> UNCONDITIONAL_FRIENDSHIP
            = REGISTER.register(
                    "unconditional_friendship",
                () -> new GameRule<>(
                        GameRuleCategory.PLAYER,
                        GameRuleType.BOOL,
                        BoolArgumentType.bool(),
                        GameRuleTypeVisitor::visitBoolean,
                        Codec.BOOL,
                        (b) -> b ? 1 : 0,
                        false,
                        FeatureFlagSet.of()
                )
            );

}
