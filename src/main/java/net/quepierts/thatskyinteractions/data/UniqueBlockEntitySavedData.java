package net.quepierts.thatskyinteractions.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.AbstractUniqueBlockEntity;
import org.jetbrains.annotations.NotNull;

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

    private final Map<ResourceLocation, Set<UUID>> saved;

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
        Set<UUID> set = this.saved.computeIfAbsent(entity.type(), (c) -> new ObjectOpenHashSet<>());
        set.remove(entity.getUUID());
    }

    public void add(AbstractUniqueBlockEntity entity) {
        Set<UUID> set = this.saved.computeIfAbsent(entity.type(), (c) -> new ObjectOpenHashSet<>());
        set.add(entity.getUUID());
    }

    @NotNull
    @Override
    public CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<ResourceLocation, Set<UUID>> entry : this.saved.entrySet()) {
            ListTag list = new ListTag();
            for (UUID uuid : entry.getValue()) {
                list.add(NbtUtils.createUUID(uuid));
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

            ListTag list = tag.getList(key, ListTag.TAG_INT_ARRAY);
            Set<UUID> collected = list.stream()
                    .map(NbtUtils::loadUUID)
                    .collect(Collectors.toCollection(ObjectOpenHashSet::new));
            this.saved.put(location, collected);
        }
    }
}
