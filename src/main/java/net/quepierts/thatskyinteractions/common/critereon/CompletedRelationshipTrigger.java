package net.quepierts.thatskyinteractions.common.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.RelationshipComponent;
import net.quepierts.thatskyinteractions.common.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.common.registry.TriggerTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class CompletedRelationshipTrigger extends SimpleCriterionTrigger<CompletedRelationshipTrigger.TriggerInstance> {
    public void trigger(ServerPlayer player, UUID other) {
        this.trigger(player, instance -> instance.test(player, other));
    }

    @Override
    @NotNull
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player)
        implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player)
        ).apply(instance, TriggerInstance::new));

        public static Criterion<TriggerInstance> completed() {
            return TriggerTypes.COMPLETED_RELATIONSHIP.get().createCriterion(new TriggerInstance(Optional.empty()));
        }

        public boolean test(ServerPlayer player, UUID other) {
            RelationshipComponent relationship = UserDataAttachment.getAttachment(player).getRelationship();
            if (!relationship.isFriend(other)) {
                return false;
            }

            InteractTreeInstance instance = relationship.get(player, other);
            return instance.isCompleted();
        }
    }
}
