package net.quepierts.thatskyinteractions.common.data.tree;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ByteArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.common.data.Codecs;
import net.quepierts.thatskyinteractions.common.data.PlayerPair;
import net.quepierts.thatskyinteractions.common.data.manager.InteractTreeManager;
import net.quepierts.thatskyinteractions.common.data.tree.node.InteractTreeNode;

import java.util.Map;

public class InteractTreeInstance {
    public static final Codec<InteractTreeInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerPair.CODEC.fieldOf("pair").forGetter(InteractTreeInstance::getPair),
            ResourceLocation.CODEC.fieldOf("tree").forGetter(InteractTreeInstance::getTreeLocation),
            Codecs.<String, Byte, Object2ByteMap<String>>map(Codec.STRING, Codec.BYTE, Object2ByteArrayMap::new).fieldOf("states").forGetter(InteractTreeInstance::getStates)
    ).apply(instance, InteractTreeInstance::new));

    public static final StreamCodec<ByteBuf, InteractTreeInstance> STREAM_CODEC = StreamCodec.composite(
            PlayerPair.STREAM_CODEC,
            InteractTreeInstance::getPair,
            ResourceLocation.STREAM_CODEC,
            InteractTreeInstance::getTreeLocation,
            ByteBufCodecs.map(Object2ByteOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.BYTE),
            InteractTreeInstance::getStates,
            InteractTreeInstance::new
    );

    public static boolean unlock(Pair<InteractTreeInstance, InteractTreeInstance> pair, String node, boolean onServer) {
        return unlock(pair.getFirst(), pair.getSecond(), node, onServer);
    }

    public static boolean unlock(InteractTreeInstance first, InteractTreeInstance second, String node, boolean onServer) {
        if (first.unlock(node) && second.unlock(node)) {
            InteractTreeNode tNode = first.getTree().get(node);
            tNode.onUnlock(first.pair, onServer);
            return true;
        }
        return false;
    }

    private static final String KEY_UNLOCKED = "nodes";
    private static final String KEY_TREE_ID = "id";
    private final PlayerPair pair;
    private final ResourceLocation tree;
    private final Object2ByteMap<String> states;

    public InteractTreeInstance(PlayerPair pair, ResourceLocation treeLocation) {
        this.pair = pair;
        this.tree = treeLocation;

        InteractTree interactTree = InteractTreeManager.INSTANCE.get(treeLocation);
        this.states = new Object2ByteOpenHashMap<>(interactTree.size());
        this.states.put(interactTree.getRootID(), (byte) NodeState.UNLOCKABLE.ordinal());
    }

    public InteractTreeInstance(PlayerPair pair, InteractTree tree, ResourceLocation location) {
        this.pair = pair;
        this.tree = location;
        this.states = new Object2ByteOpenHashMap<>(tree.size());

        this.states.put(tree.getRootID(), (byte) NodeState.UNLOCKABLE.ordinal());
    }

    public InteractTreeInstance(PlayerPair pair, InteractTree tree, CompoundTag tag) {
        this.pair = pair;
        this.states = new Object2ByteOpenHashMap<>(tree.size());
        this.tree = ResourceLocation.parse(tag.getString(KEY_TREE_ID));
        CompoundTag map = tag.getCompound(KEY_UNLOCKED);
        map.getAllKeys()
                .forEach(string -> states.put(string, map.getByte(string)));

        this.update(tree.getRootID());
    }

    public InteractTreeInstance(PlayerPair pair, ResourceLocation tree, Map<String, Byte> states) {
        this.pair = pair;
        this.tree = tree;
        this.states = new Object2ByteOpenHashMap<>(states);
    }

    public void update(String src) {
        InteractTree tree = InteractTreeManager.INSTANCE.get(this.tree);

        if (tree == null)
            return;

        ObjectArrayFIFOQueue<Pair<String, NodeState>> queue = new ObjectArrayFIFOQueue<>(tree.size());
        queue.enqueue(Pair.of(src, NodeState.byUnlocked(this.isUnlocked(src))));

        while (!queue.isEmpty()) {
            final Pair<String, NodeState> pair = queue.dequeue();
            String first = pair.getFirst();
            final NodeState state = NodeState.byUnlocked(this.isUnlocked(first), pair.getSecond());
            final InteractTreeNode node = tree.get(first);

            final NodeState nextState = NodeState.getNextState(state);

            if (state == NodeState.LOCKED) {
                this.states.removeByte(first);
            } else if (node.getPrice() < 1) {
                this.states.put(first, (byte) NodeState.UNLOCKED.ordinal());
            } else {
                this.states.put(first, (byte) state.ordinal());
            }

            if (node.hasMiddle()) {
                final String key = node.getMiddle();
                queue.enqueue(Pair.of(key, nextState));
            }

            if (node.hasLeft()) {
                final String key = node.getLeft();
                queue.enqueue(Pair.of(key, nextState));
            }

            if (node.hasRight()) {
                final String key = node.getRight();
                queue.enqueue(Pair.of(key, nextState));
            }
        }
    }

    public void reset() {
        this.states.clear();
        this.states.put(this.getTree().getRootID(), (byte) NodeState.UNLOCKABLE.ordinal());
    }

    public boolean isCompleted() {
        InteractTree tree = this.getTree();

        if (this.states.size() < tree.size()) {
            return false;
        }

        return this.states.values().intStream().sum() == tree.size() * NodeState.UNLOCKED.ordinal();
    }

    private InteractTreeInstance(PlayerPair pair, ResourceLocation tree, Object2ByteMap<String> map) {
        this.pair = pair;
        this.tree = tree;
        this.states = map;
        //this.update(getTree().getRootID());
    }

    public CompoundTag serializeNBT(CompoundTag tag) {
        CompoundTag map = new CompoundTag();
        this.states.forEach(map::putByte);
        tag.putString(KEY_TREE_ID, this.tree.toString());
        tag.put(KEY_UNLOCKED, map);
        return tag;
    }

    public boolean isUnlocked(String node) {
        return this.states.containsKey(node) && this.states.getByte(node) == NodeState.UNLOCKED.ordinal();
    }

    public boolean isUnlockable(String node) {
        return this.states.containsKey(node) && this.states.getByte(node) == NodeState.UNLOCKABLE.ordinal();
    }

    public boolean unlock(String node) {
        if (!this.isUnlockable(node))
            return false;

        this.states.put(node, (byte) NodeState.UNLOCKED.ordinal());
        this.update(node);

        return true;
    }

    public void unlockAll() {
        for (String node : this.getTree().getNodes().keySet()) {
            this.states.put(node, (byte) NodeState.UNLOCKED.ordinal());
        }

        this.update(this.getTree().getRootID());
    }

    public NodeState getNodeState(String node) {
        return NodeState.byOrdinal(this.states.getOrDefault(node, (byte) NodeState.LOCKED.ordinal()));
    }

    public static InteractTreeInstance fromNetwork(FriendlyByteBuf byteBuf) {
        PlayerPair pair = PlayerPair.fromNetwork(byteBuf);
        ResourceLocation tree = byteBuf.readResourceLocation();
        Object2ByteOpenHashMap<String> map = byteBuf.readMap(Object2ByteOpenHashMap::new, FriendlyByteBuf::readUtf, FriendlyByteBuf::readByte);
        return new InteractTreeInstance(pair, tree, map);
    }

    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
        this.pair.toNetwork(friendlyByteBuf);
        friendlyByteBuf.writeResourceLocation(this.tree);
        friendlyByteBuf.writeMap(this.states, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeByte);
    }

    public InteractTree getTree() {
        return InteractTreeManager.INSTANCE.get(this.tree);
    }

    public ResourceLocation getTreeLocation() {
        return this.tree;
    }

    private Object2ByteMap<String> getStates() {
        return states;
    }

    public PlayerPair getPair() {
        return this.pair;
    }
}
