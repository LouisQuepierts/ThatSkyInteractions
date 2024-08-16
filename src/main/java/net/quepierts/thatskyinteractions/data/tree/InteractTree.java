package net.quepierts.thatskyinteractions.data.tree;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayFIFOQueue;
import net.minecraft.network.FriendlyByteBuf;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import org.apache.commons.lang3.stream.Streams;

import java.io.Reader;
import java.util.Map;
import java.util.stream.Collectors;

public class InteractTree {
    private final Object2ObjectMap<String, InteractTreeNode> nodes;
    private final String root;

    private InteractTree(Map<String, InteractTreeNode> nodes, String root) {
        this.nodes = new Object2ObjectOpenHashMap<>(nodes);
        this.root = root;
    }


    public static InteractTree fromStream(Reader reader) {
        JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
        return serialize(object);
    }

    public static InteractTree serialize(JsonObject object) {
        JsonArray array = object.getAsJsonArray("nodes");
        String rootID = object.has("root") ? object.get("root").getAsString() : "root";

        Map<String, InteractTreeNode> nodes = Streams.of(array.iterator())
                .map(JsonElement::getAsJsonObject)
                .map(InteractTreeNode::serialize)
                .collect(Collectors.toUnmodifiableMap(
                        InteractTreeNode::getId,
                        InteractTreeNode::get
                ));

        InteractTreeNode root = nodes.get(rootID);

        if (root == null) {
            throw new RuntimeException("Unknown root name " + rootID);
        }

        root.asRoot();
        ObjectArrayFIFOQueue<InteractTreeNode> queue = new ObjectArrayFIFOQueue<>();
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            final InteractTreeNode node = queue.dequeue();
            InteractTreeNode child;

            if (node.hasLeft()) {
                child = nodes.get(node.getLeft());
                child.updatePosition(node, InteractTreeNode.Branch.LEFT);
                queue.enqueue(child);
            }

            if (node.hasMiddle()) {
                child = nodes.get(node.getMiddle());
                child.updatePosition(node, InteractTreeNode.Branch.MIDDLE);
                queue.enqueue(child);
            }

            if (node.hasRight()) {
                child = nodes.get(node.getRight());
                child.updatePosition(node, InteractTreeNode.Branch.RIGHT);
                queue.enqueue(child);
            }
        }

        return new InteractTree(nodes, rootID);
    }


    public Object2ObjectMap<String, InteractTreeNode> getNodes() {
        return Object2ObjectMaps.unmodifiable(this.nodes);
    }

    public InteractTreeNode getRoot() {
        return this.nodes.get(this.root);
    }

    public String getRootID() {
        return this.root;
    }

    public int size() {
        return this.nodes.size();
    }

    public boolean contains(String string) {
        return this.nodes.containsKey(string);
    }

    public static InteractTree fromNetwork(FriendlyByteBuf byteBuf) {
        Object2ObjectOpenHashMap<String, InteractTreeNode> map = byteBuf.readMap(
                Object2ObjectOpenHashMap::new,
                FriendlyByteBuf::readUtf,
                InteractTreeNode::fromNetwork
        );
        String root = byteBuf.readUtf();
        return new InteractTree(map, root);
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, InteractTree tree) {
        byteBuf.writeMap(
                tree.nodes,
                FriendlyByteBuf::writeUtf,
                InteractTreeNode::toNetwork
        );
        byteBuf.writeUtf(tree.root);
    }

    public InteractTreeNode get(String first) {
        return this.nodes.get(first);
    }
}
