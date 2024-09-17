package net.quepierts.thatskyinteractions.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IPickable {
    IPickable DUMMY = new Dummy();
    UUID DUMMY_UUID = new UUID(42L, 42L);
    BlockPos DUMMY_BP = new BlockPos(0, 0, 0);

    UUID getUUID();


    BlockPos getBlockPos();

    void onPickup(ServerPlayer player);

    default boolean isDailyRefresh() {
        return false;
    }

    final class Dummy implements IPickable {
        @Override
        public @NotNull UUID getUUID() {
            return DUMMY_UUID;
        }

        @Override
        public @NotNull BlockPos getBlockPos() {
            return DUMMY_BP;
        }

        @Override
        public void onPickup(ServerPlayer player) {}
    }
}
