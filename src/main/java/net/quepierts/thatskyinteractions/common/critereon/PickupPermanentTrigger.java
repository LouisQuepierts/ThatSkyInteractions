package net.quepierts.thatskyinteractions.common.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.PickupComponent;
import net.quepierts.thatskyinteractions.common.registry.TriggerTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PickupPermanentTrigger extends SimpleCriterionTrigger<PickupPermanentTrigger.TriggerInstance> {
    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> instance.matches(player));
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

        public boolean matches(ServerPlayer player) {
            return this.predicate.test(player);
        }

        public static Criterion<TriggerInstance> requiredAmount(int i) {
            return TriggerTypes.PICKUP_PERMANENT.get().createCriterion(new TriggerInstance(Optional.empty(), new Predicate(i)));
        }
    }

    public record Predicate(int amount) {
        public static final Codec<Predicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("amount").forGetter(Predicate::amount)
        ).apply(instance, Predicate::new));

        public boolean test(ServerPlayer player) {
            UserDataAttachment attachment = UserDataAttachment.getAttachment(player);
            PickupComponent pickupComponent = attachment.getPickup();
            return pickupComponent.permanent().size() >= this.amount;
        }
    }
}
