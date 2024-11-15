package net.quepierts.thatskyinteractions.common.registry;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.critereon.CompletedRelationshipTrigger;
import net.quepierts.thatskyinteractions.common.critereon.PickupPermanentTrigger;
import net.quepierts.thatskyinteractions.common.critereon.UnlockRelationshipTrigger;

public class TriggerTypes {
    public static final DeferredRegister<CriterionTrigger<?>> REGISTER = DeferredRegister.create(
            Registries.TRIGGER_TYPE, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<CriterionTrigger<?>, PickupPermanentTrigger> PICKUP_PERMANENT = REGISTER.register(
            "pickup_permanent", PickupPermanentTrigger::new
    );

    public static final DeferredHolder<CriterionTrigger<?>, UnlockRelationshipTrigger> UNLOCK_RELATIONSHIP = REGISTER.register(
            "unlock_relationship", UnlockRelationshipTrigger::new
    );

    public static final DeferredHolder<CriterionTrigger<?>, CompletedRelationshipTrigger> COMPLETED_RELATIONSHIP = REGISTER.register(
            "completed_relationship", CompletedRelationshipTrigger::new
    );
}
