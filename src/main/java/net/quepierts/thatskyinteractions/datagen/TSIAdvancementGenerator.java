package net.quepierts.thatskyinteractions.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.critereon.CompletedRelationshipTrigger;
import net.quepierts.thatskyinteractions.common.critereon.PickupPermanentTrigger;
import net.quepierts.thatskyinteractions.common.critereon.UnlockRelationshipTrigger;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class TSIAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(
            @NotNull HolderLookup.Provider provider,
            @NotNull Consumer<AdvancementHolder> saver,
            @NotNull ExistingFileHelper existingFileHelper
    ) {
        Advancement.Builder.advancement()
                .display(
                        new ItemStack(net.quepierts.thatskyinteractions.common.registry.Items.SIMPLE_CLOUD),
                        Component.translatable("advancements.thatskyinteractions.root.title"),
                        Component.translatable("advancements.thatskyinteractions.root.description"),
                        ResourceLocation.withDefaultNamespace("textures/block/white_concrete.png"), AdvancementType.TASK,
                        true, true, false
                )
                .addCriterion("item", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CANDLE))
                .requirements(AdvancementRequirements.allOf(List.of("item")))
                .save(saver, ThatSkyInteractions.getLocation("root"), existingFileHelper);
        Advancement.Builder.advancement().parent(AdvancementSubProvider.createPlaceholder("thatskyinteractions:root"))
                .display(
                    new ItemStack(net.quepierts.thatskyinteractions.common.registry.Items.WING_OF_LIGHT),
                    Component.translatable("advancements.thatskyinteractions.wing_of_light.title"),
                    Component.translatable("advancements.thatskyinteractions.wing_of_light.description"),
                    null, AdvancementType.GOAL,
                    true, true, false
                )
                .addCriterion("pickup", PickupPermanentTrigger.TriggerInstance.requiredAmount(1))
                .requirements(AdvancementRequirements.allOf(List.of("pickup")))
                .save(saver, ThatSkyInteractions.getLocation("wing_of_light"), existingFileHelper);
        Advancement.Builder.advancement().parent(AdvancementSubProvider.createPlaceholder("thatskyinteractions:root"))
                .display(
                        new ItemStack(Items.CANDLE),
                        Component.translatable("advancements.thatskyinteractions.friendship.title"),
                        Component.translatable("advancements.thatskyinteractions.friendship.description"),
                        null, AdvancementType.GOAL,
                        true, true, false
                )
                .addCriterion("unlock", UnlockRelationshipTrigger.TriggerInstance.unlockNode("root"))
                .requirements(AdvancementRequirements.allOf(List.of("unlock")))
                .save(saver, ThatSkyInteractions.getLocation("friendship"), existingFileHelper);
        Advancement.Builder.advancement().parent(AdvancementSubProvider.createPlaceholder("thatskyinteractions:friendship"))
                .display(
                        new ItemStack(Items.RED_CANDLE),
                        Component.translatable("advancements.thatskyinteractions.intimate.title"),
                        Component.translatable("advancements.thatskyinteractions.intimate.description"),
                        null, AdvancementType.CHALLENGE,
                        true, true, false
                )
                .addCriterion("completed", CompletedRelationshipTrigger.TriggerInstance.completed())
                .requirements(AdvancementRequirements.allOf(List.of("completed")))
                .save(saver, ThatSkyInteractions.getLocation("intimate"), existingFileHelper);
    }
}
