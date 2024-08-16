package net.quepierts.thatskyinteractions.data.tree.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.RenderUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.tree.LockedButton;
import net.quepierts.thatskyinteractions.client.gui.component.tree.TreeNodeButton;
import net.quepierts.thatskyinteractions.data.tree.NodeState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class InteractTreeNode {
    public static final int DEFAULT_HEIGHT = 40;
    public static final int NODE_SIZE = 32;
    protected static final Factory DEFAULT_FACTORY = new Factory(InteractTreeNode::new, InteractTreeNode::new);
    protected static final Object2ObjectMap<String, Factory> FACTORIES = new Object2ObjectOpenHashMap<>();
    @NotNull protected final String id;
    @NotNull protected final String left;
    @NotNull protected final String middle;
    @NotNull protected final String right;

    protected final int price;
    protected int y;
    protected int x;

    protected InteractTreeNode(JsonObject json) {
        this.id = getString(json, "id");
        this.left = getString(json, "left");
        this.right = getString(json, "right");
        this.middle = getString(json, "middle");
        this.price = getInt(json, "price", 0);
        this.y = getInt(json, "height", DEFAULT_HEIGHT);
    }

    public InteractTreeNode(FriendlyByteBuf friendlyByteBuf) {
        this.id = friendlyByteBuf.readUtf();
        this.left = friendlyByteBuf.readUtf();
        this.right = friendlyByteBuf.readUtf();
        this.middle = friendlyByteBuf.readUtf();
        this.price = friendlyByteBuf.readVarInt();
        this.x = friendlyByteBuf.readVarInt();
        this.y = friendlyByteBuf.readVarInt();
    }

    protected static String getString(JsonObject object, String key) {
        JsonElement element = object.get(key);
        return element == null ? "" : element.getAsString();
    }

    protected static int getInt(JsonObject object, String key, int def) {
        JsonElement element = object.get(key);
        return element == null ? def : element.getAsInt();
    }

    public static InteractTreeNode serialize(JsonObject object) {
        String type = object.get("type").getAsString();
        return FACTORIES.getOrDefault(type, DEFAULT_FACTORY).fromJson().apply(object);
    }

    public static InteractTreeNode fromNetwork(FriendlyByteBuf friendlyByteBuf) {
        String type = friendlyByteBuf.readUtf();
        return FACTORIES.getOrDefault(type, DEFAULT_FACTORY).fromNetwork().apply(friendlyByteBuf);
    }

    public static void toNetwork(FriendlyByteBuf friendlyByteBuf, InteractTreeNode node) {
        node.write(friendlyByteBuf);
    }

    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(this.type());
        friendlyByteBuf.writeUtf(this.id);

        friendlyByteBuf.writeUtf(this.left);
        friendlyByteBuf.writeUtf(this.right);
        friendlyByteBuf.writeUtf(this.middle);

        friendlyByteBuf.writeVarInt(this.price);
        friendlyByteBuf.writeVarInt(this.x);
        friendlyByteBuf.writeVarInt(this.y);
    }

    public @NotNull String getLeft() {
        return left;
    }

    public @NotNull String getMiddle() {
        return middle;
    }

    public @NotNull String getRight() {
        return right;
    }

    public boolean hasLeft() {
        return !left.isEmpty();
    }

    public boolean hasMiddle() {
        return !middle.isEmpty();
    }

    public boolean hasRight() {
        return !right.isEmpty();
    }

    public @NotNull String getId() {
        return id;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getPrice() {
        return price;
    }

    protected String type() {
        return "";
    }

    public void updatePosition(InteractTreeNode other, Branch branch) {
        switch (branch) {
            case LEFT:
                this.y = other.y + 48;
                this.x += other.x - 56;
                break;
            case RIGHT:
                this.y = other.y + 48;
                this.x += other.x + 56;
                break;
            case MIDDLE:
                this.y += other.y + 32;
                this.x += other.x;
                break;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public @NotNull TreeNodeButton asButton(ScreenAnimator animator, NodeState state) {
        if (state == NodeState.LOCKED) {
            return new LockedButton(this.id, this.x, this.y, animator);
        }
        return new TreeNodeButton(id, this.x, this.price, Component.empty(), this.y, RenderUtils.DEFAULT_ICON, animator, state);
    }

    public Item getCurrency() {
        return Items.CANDLE;
    }

    public InteractTreeNode get() {
        return this;
    }

    public void asRoot() {
        this.y = 0;
    }

    public enum Branch {
        LEFT,
        RIGHT,
        MIDDLE
    }

    public static void register() {
        FACTORIES.put("default", DEFAULT_FACTORY);
        FACTORIES.put(RootNode.TYPE, new Factory(RootNode::new, RootNode::new));
        FACTORIES.put(LikeNode.TYPE, new Factory(LikeNode::new, LikeNode::new));
        FACTORIES.put(LockNode.TYPE, new Factory(LockNode::new, LockNode::new));
        FACTORIES.put(FriendNode.TYPE, new Factory(FriendNode::new, FriendNode::new));
        FACTORIES.put(InteractionNode.TYPE, new Factory(InteractionNode::new, InteractionNode::new));
    }

    protected record Factory(
            Function<JsonObject, InteractTreeNode> fromJson,
            Function<FriendlyByteBuf, InteractTreeNode> fromNetwork
    ) {};
}
