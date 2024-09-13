package net.quepierts.thatskyinteractions.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.quepierts.thatskyinteractions.block.entity.AbstractUniqueBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UniqueBlockEntitySavedData extends SavedData {
    public static final String ID = "uuid_block_entity";
    public static final SavedData.Factory<UniqueBlockEntitySavedData> FACTORY = new SavedData.Factory<>(
            UniqueBlockEntitySavedData::new,
            UniqueBlockEntitySavedData::load
    );

    private final Map<ResourceLocation, Set<UniqueBlockEntityData>> saved;

    public static UniqueBlockEntitySavedData getData(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, ID);
    }

    private UniqueBlockEntitySavedData() {
        this.setDirty();
        this.saved = new Object2ObjectOpenHashMap<>();
    }

    private static UniqueBlockEntitySavedData load(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider provider) {
        UniqueBlockEntitySavedData data = new UniqueBlockEntitySavedData();
        data.loadInner(tag);
        return data;
    }


    public void remove(AbstractUniqueBlockEntity entity) {
        Set<UniqueBlockEntityData> set = this.saved.computeIfAbsent(entity.type(), (c) -> new ObjectOpenHashSet<>());
        set.remove(new UniqueBlockEntityData(entity));
        this.setDirty();
    }

    public void add(AbstractUniqueBlockEntity entity) {
        Set<UniqueBlockEntityData> set = this.saved.computeIfAbsent(entity.type(), (c) -> new ObjectOpenHashSet<>());
        set.add(new UniqueBlockEntityData(entity));
        this.setDirty();
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<ResourceLocation, Set<UniqueBlockEntityData>> entry : this.saved.entrySet()) {
            ListTag list = new ListTag();
            for (UniqueBlockEntityData data : entry.getValue()) {
                list.add(data.toNBT());
            }
            tag.put(entry.getKey().toString(), list);
        }
        return tag;
    }

    private void loadInner(@NotNull CompoundTag tag) {
        this.setDirty(false);

        Set<String> keys = tag.getAllKeys();
        for (String key : keys) {
            ResourceLocation location = ResourceLocation.tryParse(key);

            if (location == null) {
                continue;
            }

            ListTag list = tag.getList(key, ListTag.TAG_COMPOUND);
            Set<UniqueBlockEntityData> collected = list.stream()
                    .map(CompoundTag.class::cast)
                    .map(UniqueBlockEntityData::fromNBT)
                    .collect(Collectors.toCollection(ObjectOpenHashSet::new));
            this.saved.put(location, collected);
        }
    }

    private record UniqueBlockEntityData(
            UUID uuid,
            BlockPos pos
    ) {
        public UniqueBlockEntityData(AbstractUniqueBlockEntity entity) {
            this(entity.getUUID(), entity.getBlockPos());
        }

        public CompoundTag toNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("uuid", this.uuid);
            tag.putIntArray("pos", new int[]{this.pos.getX(), this.pos.getY(), this.pos.getZ()});
            return tag;
        }

        @Nullable
        public static UniqueBlockEntityData fromNBT(CompoundTag tag) {
            if (!tag.contains("uuid", CompoundTag.TAG_INT_ARRAY) || !tag.contains("pos", CompoundTag.TAG_INT_ARRAY)) {
                return null;
            }

            UUID uuid = tag.getUUID("uuid");
            int[] array = tag.getIntArray("pos");
            return new UniqueBlockEntityData(
                    uuid,
                    new BlockPos(array[0], array[1], array[2])
            );
        }
    };
}
