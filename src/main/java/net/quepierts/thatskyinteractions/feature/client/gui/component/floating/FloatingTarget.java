package net.quepierts.thatskyinteractions.feature.client.gui.component.floating;

import net.minecraft.core.BlockPos;

import java.util.UUID;

public sealed interface FloatingTarget {
    record Entity(UUID uuid) implements FloatingTarget {}
    record Block(BlockPos pos) implements FloatingTarget {}
}
