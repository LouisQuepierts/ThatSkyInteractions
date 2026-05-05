package net.quepierts.thatskyinteractions.core.model.friendship;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class FriendshipTree {
    private final Object2ObjectMap<String, TreeNode> nodes;
    private final String root;

    private FriendshipTree(Map<String, TreeNode> nodes, String root) {
        this.nodes = new Object2ObjectOpenHashMap<>(nodes);
        this.root = root;
    }

    public Object2ObjectMap<String, TreeNode> getNodes() {
        return Object2ObjectMaps.unmodifiable(this.nodes);
    }

    public TreeNode getRoot() {
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

    public TreeNode get(String first) {
        return this.nodes.get(first);
    }
}
