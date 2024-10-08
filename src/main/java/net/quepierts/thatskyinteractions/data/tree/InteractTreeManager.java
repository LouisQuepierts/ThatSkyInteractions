package net.quepierts.thatskyinteractions.data.tree;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.packet.batch.PacketCache;
import net.quepierts.thatskyinteractions.network.packet.BatchInteractTreePacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class InteractTreeManager implements PreparableReloadListener {
    public static final FileToIdConverter INTERACT_TREE_LISTER = FileToIdConverter.json("interact_trees");
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PacketCache cache = new PacketCache();
    private Object2ObjectMap<ResourceLocation, InteractTree> byPath;

    public InteractTree get(ResourceLocation location) {
        return byPath.get(location);
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller1, Executor executor, Executor executor1) {
        CompletableFuture<List<Pair<ResourceLocation, InteractTree>>> load = this.load(resourceManager, executor);
        LOGGER.info("Reload Interact Tree Data...");
        return CompletableFuture.allOf(load)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync((v) -> {
                    this.byPath = new Object2ObjectOpenHashMap<>();
                    load.join().forEach(pair -> byPath.put(pair.getFirst(), pair.getSecond()));

                    this.cache.reset(new BatchInteractTreePacket(Object2ObjectMaps.unmodifiable(this.byPath)));
                });
    }

    private CompletableFuture<List<Pair<ResourceLocation, InteractTree>>> load(ResourceManager pResourceManager, Executor pBackgroundExecutor) {
        return CompletableFuture.supplyAsync(() -> INTERACT_TREE_LISTER.listMatchingResourceStacks(pResourceManager), pBackgroundExecutor).thenCompose(map -> {
            List<CompletableFuture<Pair<ResourceLocation, InteractTree>>> list = new ArrayList<>(map.size());

            for (Map.Entry<ResourceLocation, List<Resource>> entry : map.entrySet()) {
                ResourceLocation location = entry.getKey();
                ResourceLocation resourceLocation = INTERACT_TREE_LISTER.fileToId(location);

                for (Resource resource : entry.getValue()) {
                    list.add(CompletableFuture.supplyAsync(() -> {
                        try (Reader reader = resource.openAsReader()) {
                            return Pair.of(resourceLocation, InteractTree.fromStream(reader));
                        } catch (IOException e) {
                            LOGGER.warn("Couldn't read interact tree {} from {} in data pack {}", resourceLocation, location, resource.sourcePackId());
                            return null;
                        }
                    }));
                }
            }

            return Util.sequence(list)
                    .thenApply(result -> result.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
                    );
        });
    }

    public void sync(OnDatapackSyncEvent event) {
        if (!this.cache.ready())
            return;

        if (event.getPlayer() != null) {
            SimpleAnimator.getNetwork().sendToPlayer(this.cache, event.getPlayer());
        } else {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                SimpleAnimator.getNetwork().sendToPlayer(this.cache, player);
            }
        }
    }

    public void handleUpdateInteractTree(final BatchInteractTreePacket batchInteractTreePacket) {
        this.byPath = batchInteractTreePacket.getInteractTrees();
    }

    public void clear() {
        this.byPath = null;
    }
}
