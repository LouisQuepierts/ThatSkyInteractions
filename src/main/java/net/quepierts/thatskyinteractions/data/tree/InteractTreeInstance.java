package net.quepierts.thatskyinteractions.data.tree;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;

public class InteractTreeInstance {
    private static final String KEY_UNLOCKED = "nodes";
    private static final String KEY_TREE_ID = "id";
    private final PlayerPair pair;
    private final ResourceLocation tree;
    private final Object2ByteMap<String> states;

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

    public boolean unlock(String node, boolean onServer) {
        if (!this.isUnlockable(node))
            return false;

        this.states.put(node, (byte) NodeState.UNLOCKED.ordinal());
        this.update(node);

        InteractTreeNode tNode = this.getTree().get(node);
        tNode.onUnlock(this.pair, onServer);

        return true;
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
        return ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().get(this.tree);
    }

    public PlayerPair getPair() {
        return this.pair;
    }
}
