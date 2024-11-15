package net.quepierts.thatskyinteractions.common.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.common.registry.TriggerTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UnlockRelationshipTrigger extends SimpleCriterionTrigger<UnlockRelationshipTrigger.TriggerInstance> {
    public void trigger(ServerPlayer player, String node) {
        this.trigger(player, instance -> instance.predicate().test(node));
    }

    @Override
    @NotNull
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Predicate predicate)
            implements SimpleInstance {

        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                        Predicate.CODEC.fieldOf("predicate").forGetter(TriggerInstance::predicate)
                ).apply(instance, TriggerInstance::new)
        );

        public static Criterion<TriggerInstance> unlockNode(String node) {
            return TriggerTypes.UNLOCK_RELATIONSHIP.get().createCriterion(new TriggerInstance(Optional.empty(), new Predicate(node)));
        }
    }

    public record Predicate(String node) {
        public static final Codec<Predicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("node").forGetter(Predicate::node)
        ).apply(instance, Predicate::new));

        public boolean test(@NotNull String node) {
            return this.node.equals(node);
        }
    }
}
