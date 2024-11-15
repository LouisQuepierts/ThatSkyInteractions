package net.quepierts.thatskyinteractions.common.data.attachment.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.common.data.astrolabe.AstrolabeMap;
import net.quepierts.thatskyinteractions.common.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.common.data.manager.AstrolabeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AstrolabeComponent implements IComponent<AstrolabeComponent> {
    public static final Codec<AstrolabeComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AstrolabeMap.CODEC.fieldOf("astrolabes").forGetter(AstrolabeComponent::getAstrolabes)
    ).apply(instance, AstrolabeComponent::new));

    public static final StreamCodec<ByteBuf, AstrolabeComponent> STREAM_CODEC = StreamCodec.composite(
            AstrolabeMap.STREAM_CODEC,
            AstrolabeComponent::getAstrolabes,
            AstrolabeComponent::new
    );

    private final Map<UUID, Pair<FriendAstrolabeInstance.NodeData, ResourceLocation>> cache;
    private final AstrolabeMap astrolabes;

    public AstrolabeComponent(AstrolabeMap astrolabes) {
        this.astrolabes = astrolabes;
        this.cache = new HashMap<>();

        for (Map.Entry<ResourceLocation, FriendAstrolabeInstance> entry : this.astrolabes.entrySet()) {
            for (FriendAstrolabeInstance.NodeData node : entry.getValue().getNodes()) {
                if (node == null)
                    continue;
                this.cache.put(node.getFriendData().getUuid(), Pair.of(node, entry.getKey()));
            }
        }
    }

    @Nullable
    @SuppressWarnings("all")
    public Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(Player player) {
        if (player == null)
            return null;

        if (this.isFriend(player.getUUID()))
            return null;

        @Nullable Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> data = this.astrolabes.addFriend(player);
        if (data != null) {
            this.cache.put(player.getUUID(), data);
        }
        return data;
    }

    /* debug */
    public @Nullable Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(UUID player) {
        if (player == null)
            return null;

        if (this.isFriend(player))
            return null;

        @Nullable Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> data = this.astrolabes.addFriend(player);
        if (data != null) {
            this.cache.put(player, data);
        }
        return data;
    }

    public boolean likeFriend(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(player);
        if (cache == null)
            return false;

        ResourceLocation astrolabe = this.astrolabes.getOrCreateBestFriendAstrolabe();

        if (astrolabe == null)
            return false;

        this.transfer(cache.getFirst(), cache.getSecond(), astrolabe);
        return true;
    }

    public boolean unlikeFriend(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(player);
        if (cache == null)
            return false;

        if (cache.getSecond().getPath().startsWith(AstrolabeManager.GENERATED_PREFIX))
            return false;

        ResourceLocation astrolabe = this.astrolabes.getOrCreateFriendAstrolabe();

        if (astrolabe == null)
            return false;

        this.transfer(cache.getFirst(), cache.getSecond(), astrolabe);
        return true;
    }

    private void transfer(@NotNull FriendAstrolabeInstance.NodeData data, @NotNull ResourceLocation srcLocation, @NotNull ResourceLocation destLocation) {
        FriendAstrolabeInstance src = this.astrolabes.get(srcLocation);
        FriendAstrolabeInstance dest = this.astrolabes.get(destLocation);
        src.peek(data);
        dest.put(data);

        this.cache.put(data.getFriendData().getUuid(), Pair.of(data, destLocation));
    }

    public void move(UUID friendUUID, ResourceLocation destLocation, int destIndex) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(friendUUID);
        if (cache == null)
            return;
        ResourceLocation srcLocation = cache.getSecond();
        FriendAstrolabeInstance src = this.astrolabes.get(srcLocation);
        int srcIndex = src.indexOf(cache.getFirst());
        if (this.astrolabes.move(srcLocation, destLocation, srcIndex, destIndex)) {
            this.cache.put(friendUUID, Pair.of(cache.getFirst(), destLocation));
        }
    }

    public void createAstrolabe(ResourceLocation location) {
        this.astrolabes.getOrCreate(location);
    }

    public boolean sendLight(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);

        if (pair == null)
            return false;

        FriendAstrolabeInstance.NodeData data = pair.getFirst();

        if (data == null)
            return false;

        if (data.hasFlag(FriendAstrolabeInstance.Flag.SENT))
            return false;
        data.setFlag(FriendAstrolabeInstance.Flag.SENT, true);
        return true;
    }

    public void awardLight(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);

        if (pair == null) {
            return;
        }

        FriendAstrolabeInstance.NodeData data = pair.getFirst();
        if (data != null) {
            data.setFlag(FriendAstrolabeInstance.Flag.RECEIVED, true);
        }
    }

    public boolean gainLight(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);

        if (pair == null)
            return false;

        FriendAstrolabeInstance.NodeData first = pair.getFirst();

        if (!first.hasFlag(FriendAstrolabeInstance.Flag.RECEIVED))
            return false;
        first.setFlag(FriendAstrolabeInstance.Flag.RECEIVED, false);
        return true;
    }

    public boolean isFriend(UUID player) {
        return this.cache.containsKey(player);
    }

    public static AstrolabeComponent createInstance() {
        return new AstrolabeComponent(new AstrolabeMap());
    }

    public AstrolabeMap getAstrolabes() {
        return astrolabes;
    }

    public Map<UUID, Pair<FriendAstrolabeInstance.NodeData, ResourceLocation>> getCache() {
        return cache;
    }

    @Nullable
    public FriendAstrolabeInstance.NodeData getNodeData(UUID target) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> cache = this.cache.get(target);
        if (cache == null)
            return null;
        return cache.getFirst();
    }

    public boolean isLiked(UUID player) {
        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.cache.get(player);
        return pair != null && !pair.getSecond().getPath().startsWith(AstrolabeManager.GENERATED_PREFIX);
    }

    @Override
    public void setInfo(AstrolabeComponent component) {
        if (component.astrolabes.isEmpty())
            return;

        this.astrolabes.clear();
        this.astrolabes.putAll(component.astrolabes);

        this.cache.clear();;
        for (Map.Entry<ResourceLocation, FriendAstrolabeInstance> entry : this.astrolabes.entrySet()) {
            for (FriendAstrolabeInstance.NodeData node : entry.getValue().getNodes()) {
                if (node == null)
                    continue;
                this.cache.put(node.getFriendData().getUuid(), Pair.of(node, entry.getKey()));
            }
        }
    }

    @Override
    public void update() {
        this.astrolabes.update();
    }
}

