package net.quepierts.thatskyinteractions.data.astrolabe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.*;
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
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.astrolabe.node.AstrolabeNode;
import net.quepierts.thatskyinteractions.data.astrolabe.node.FriendNode;
import net.quepierts.thatskyinteractions.network.packet.BatchAstrolabePacket;
import org.apache.commons.lang3.stream.Streams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class AstrolabeManager implements PreparableReloadListener {
    public static final FileToIdConverter ASTROLABE_LISTER = FileToIdConverter.json("astrolabes");
    public static final int GENERATED_ASTROLABES_AMOUNT = 8;
    public static final String GENERATED_PREFIX = "builtin_";
    public static final ImmutableList<ResourceLocation> GENERATED_ASTROLABES;
    private static final int MAX_ASTROLABE_NODES = 10;
    private static final int GENERATED_SIZE = 20;
    private static final int GRID_UNIT = 20;
    private static final int ASTROLABE_WIDTH = 280;
    private static final int ASTROLABE_HEIGHT = 160;
    private static final int GRID_WIDTH = ASTROLABE_WIDTH / GRID_UNIT;
    private static final int GRID_HEIGHT = ASTROLABE_HEIGHT / GRID_UNIT;

    private static final String FRIEND_ASTROLABES_PATH = "friend_astrolabes.json";
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PacketCache cache = new PacketCache();
    private ObjectList<ResourceLocation> bestFriendAstrolabes;
    private ObjectList<ResourceLocation> friendAstrolabes;
    private Object2ObjectMap<ResourceLocation, Astrolabe> byPath;
    private ObjectList<Astrolabe> generated;

    @Nullable
    public Astrolabe get(ResourceLocation location) {
        return this.byPath.get(location);
    }

    public Astrolabe getGenerated(int i) {
        if (i > GENERATED_ASTROLABES_AMOUNT)
            throw new IllegalArgumentException("Generated Astrolabes Amount = " + GENERATED_ASTROLABES_AMOUNT);

        return this.generated.get(i);
    }

    public ResourceLocation getGeneratedLocation(int i) {
        return ThatSkyInteractions.getLocation(GENERATED_PREFIX + i);
    }

    public ObjectSet<Map.Entry<ResourceLocation, Astrolabe>> getAll() {
        return this.byPath.entrySet();
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, @NotNull ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller1, Executor executor, Executor executor1) {
        CompletableFuture<List<Pair<ResourceLocation, Astrolabe>>> load = this.load(resourceManager, executor);
        CompletableFuture<List<ResourceLocation>> listFriendAstrolabes = loadFriendAstrolabeList(resourceManager, executor);
        LOGGER.info("Reload Astrolabes Data...");
        return CompletableFuture.allOf(load)
                .thenCompose(preparationBarrier::wait)
                .thenAcceptAsync((v) -> {
                    List<Pair<ResourceLocation, Astrolabe>> join = load.join();
                    Map<ResourceLocation, Astrolabe> generated = this.generate();
                    this.byPath = new Object2ObjectOpenHashMap<>(join.size() + generated.size());
                    this.byPath.putAll(generated);
                    join.forEach(pair -> byPath.put(pair.getFirst(), pair.getSecond()));
                    this.byPath = Object2ObjectMaps.unmodifiable(this.byPath);

                    this.generated = new ObjectImmutableList<>(generated.values());

                    List<ResourceLocation> friendAstrolabesList = listFriendAstrolabes.join();
                    this.bestFriendAstrolabes = new ObjectArrayList<>(friendAstrolabesList);
                    this.bestFriendAstrolabes = ObjectLists.unmodifiable(this.bestFriendAstrolabes);
                    this.friendAstrolabes = new ObjectArrayList<>(friendAstrolabesList.size() + GENERATED_SIZE);
                    this.friendAstrolabes.addAll(friendAstrolabesList);
                    this.friendAstrolabes.addAll(GENERATED_ASTROLABES);
                    this.friendAstrolabes = ObjectLists.unmodifiable(this.friendAstrolabes);

                    this.cache.reset(new BatchAstrolabePacket(
                            this.byPath,
                            this.bestFriendAstrolabes,
                            this.friendAstrolabes
                    ));
                });
    }

    private Map<ResourceLocation, Astrolabe> generate() {
        Map<ResourceLocation, Astrolabe> generated = new Object2ObjectOpenHashMap<>(GENERATED_ASTROLABES_AMOUNT);
        final Random random = new Random(42L);
        boolean[] grid = new boolean[GRID_WIDTH * GRID_HEIGHT];

        for (int i = 0; i < GENERATED_ASTROLABES_AMOUNT; i++) {
            Astrolabe generate = this.generate(grid, random);
            generated.put(this.getGeneratedLocation(i), generate);
        }

        return generated;
    }

    private Astrolabe generate(boolean[] grid, Random random) {
        Arrays.fill(grid, false);
        List<AstrolabeNode> nodes = new ArrayList<>(GENERATED_SIZE);

        int x, y;
        int i = 0;
        while (i < GENERATED_SIZE) {
            x = random.nextInt(GRID_WIDTH);
            y = random.nextInt(GRID_HEIGHT);

            if (!grid[x + y * GRID_WIDTH]) {
                i++;

                AstrolabeNode node = new FriendNode(
                        x * GRID_UNIT - ASTROLABE_WIDTH / 2 + random.nextInt(5) - random.nextInt(5),
                        y * GRID_UNIT - ASTROLABE_HEIGHT / 2 + random.nextInt(5) - random.nextInt(5)
                );
                nodes.add(node);
                grid[x + y * GRID_WIDTH] = true;
            }
        }

        return new Astrolabe(nodes, ObjectLists.emptyList());
    }

    private CompletableFuture<List<ResourceLocation>> loadFriendAstrolabeList(ResourceManager pResourceManager, Executor pBackgroundExecutor) {
        return CompletableFuture.supplyAsync(pResourceManager::getNamespaces)
                .thenApply(list -> {
                    List<ResourceLocation> locations = new ArrayList<>(8);
                    for (String namespace : list) {
                        try {
                            for (Resource resource : pResourceManager.getResourceStack(ResourceLocation.fromNamespaceAndPath(namespace, FRIEND_ASTROLABES_PATH))) {
                                BufferedReader reader = resource.openAsReader();
                                JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
                                Streams.of(array.iterator())
                                        .map(JsonElement::getAsString)
                                        .map(ResourceLocation::parse)
                                        .forEach(locations::add);
                            }
                        } catch (IOException e) {
                            LOGGER.warn("Couldn't read friend astrolabe from namespace {}", namespace, e);
                        }
                    }

                    return locations;
                });
    }

    private CompletableFuture<List<Pair<ResourceLocation, Astrolabe>>> load(ResourceManager pResourceManager, Executor pBackgroundExecutor) {
        return CompletableFuture.supplyAsync(() -> ASTROLABE_LISTER.listMatchingResourceStacks(pResourceManager), pBackgroundExecutor).thenCompose(map -> {
            List<CompletableFuture<Pair<ResourceLocation, Astrolabe>>> list = new ArrayList<>(map.size());

            for (Map.Entry<ResourceLocation, List<Resource>> entry : map.entrySet()) {
                ResourceLocation location = entry.getKey();
                ResourceLocation resourceLocation = ASTROLABE_LISTER.fileToId(location);

                for (Resource resource : entry.getValue()) {
                    list.add(CompletableFuture.supplyAsync(() -> {
                        try (Reader reader = resource.openAsReader()) {
                            return Pair.of(resourceLocation, Astrolabe.fromStream(reader));
                        } catch (IOException e) {
                            LOGGER.warn("Couldn't read astrolabe {} from {} in data pack {}", resourceLocation, location, resource.sourcePackId());
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

    public void handleUpdateAstrolabe(BatchAstrolabePacket packet) {
        this.byPath = packet.getAstrolabes();
        this.bestFriendAstrolabes = ObjectLists.unmodifiable(packet.getBestFriendAstrolabes());
        this.friendAstrolabes = ObjectLists.unmodifiable(packet.getFriendAstrolabes());
    }

    public ObjectList<ResourceLocation> getBestFriendAstrolabes() {
        return bestFriendAstrolabes;
    }

    public ObjectList<ResourceLocation> getFriendAstrolabes() {
        return friendAstrolabes;
    }

    static {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for (int i = 0; i < MAX_ASTROLABE_NODES; i++) {
            builder.add(ThatSkyInteractions.getLocation(GENERATED_PREFIX + i));
        }
        GENERATED_ASTROLABES = builder.build();
    }

    public boolean contains(ResourceLocation location) {
        return this.byPath.containsKey(location);
    }

    public void clear() {
        this.byPath = null;
        this.bestFriendAstrolabes = null;
        this.friendAstrolabes = null;
    }
}
