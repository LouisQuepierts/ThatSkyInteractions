package net.quepierts.thatskyinteractions.data.tree;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;

public class InteractTreeInstance {
    private static final String KEY_UNLOCKED = "nodes";
    private static final String KEY_TREE_ID = "id";
    private final ResourceLocation tree;
    private final Object2ByteMap<String> states;

    public InteractTreeInstance(InteractTree tree, ResourceLocation location) {
        this.tree = location;
        this.states = new Object2ByteOpenHashMap<>(tree.size());

        this.states.put(tree.getRootID(), (byte) NodeState.UNLOCKABLE.ordinal());
    }

    public InteractTreeInstance(InteractTree tree, CompoundTag tag) {
        this.states = new Object2ByteOpenHashMap<>(tree.size());
        this.tree = ResourceLocation.parse(tag.getString(KEY_TREE_ID));
        CompoundTag map = tag.getCompound(KEY_UNLOCKED);
        map.getAllKeys()
                .forEach(string -> states.put(string, map.getByte(string)));

        this.update(tree.getRootID());
    }

    public void update(String src) {
        InteractTree tree = ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().get(this.tree);

        if (tree == null)
            return;

        ObjectArrayFIFOQueue<Pair<String, NodeState>> queue = new ObjectArrayFIFOQueue<>(tree.size());
        queue.enqueue(Pair.of(src, NodeState.byUnlocked(this.isUnlocked(src))));

        while (!queue.isEmpty()) {
            final Pair<String, NodeState> pair = queue.dequeue();
            String first = pair.getFirst();
            final NodeState state = NodeState.byUnlocked(this.isUnlocked(first), pair.getSecond());
            final InteractTreeNode node = tree.get(first);

            final NodeState nextState = NodeState.getNextState(pair.getSecond());

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

    private InteractTreeInstance(ResourceLocation tree, ObjectSet<String> strings) {
        this.tree = tree;
        this.states = new Object2ByteOpenHashMap<>();
        for (String s : strings) {
            this.states.put(s, (byte) NodeState.UNLOCKED.ordinal());
        }
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

    public NodeState getNodeState(String node) {
        return NodeState.byOrdinal(this.states.getOrDefault(node, (byte) NodeState.LOCKED.ordinal()));
    }

    public static InteractTreeInstance fromNetwork(FriendlyByteBuf byteBuf) {
        ResourceLocation tree = byteBuf.readResourceLocation();
        ObjectOpenHashSet<String> strings = byteBuf.readCollection(ObjectOpenHashSet::new, FriendlyByteBuf::readUtf);
        return new InteractTreeInstance(tree, strings);
    }

    public void toNetwork(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(this.tree);
        friendlyByteBuf.writeCollection(this.states.keySet(), FriendlyByteBuf::writeUtf);
    }

    public InteractTree getTree() {
        return ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().get(this.tree);
    }
}
