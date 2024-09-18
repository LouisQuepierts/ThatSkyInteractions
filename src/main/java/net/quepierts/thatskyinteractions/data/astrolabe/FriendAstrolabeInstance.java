package net.quepierts.thatskyinteractions.data.astrolabe;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.FriendData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FriendAstrolabeInstance {
    private static final String INDEX_PREFIX = "i";
    private final NodeData[] data;
    private int count = 0;
    private int next = -1;
    private boolean dirty = false;

    public FriendAstrolabeInstance(ResourceLocation first) {
        Astrolabe astrolabe = ThatSkyInteractions.getInstance().getProxy().getAstrolabeManager().get(first);
        if (astrolabe == null) {
            throw new IllegalArgumentException("Unknown astrolabe " + first);
        }

        this.data = new NodeData[astrolabe.size()];
        this.next = 0;
    }

    public static FriendAstrolabeInstance fromNetwork(FriendlyByteBuf byteBuf) {
        int length = byteBuf.readVarInt();
        NodeData[] array = new NodeData[length];

        for (int i = 0; i < length; i++) {
            if (byteBuf.readBoolean()) {
                array[i] = NodeData.fromNetwork(byteBuf);
            }
        }
        return new FriendAstrolabeInstance(array);
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, FriendAstrolabeInstance instance) {
        byteBuf.writeVarInt(instance.data.length);
        for (NodeData data : instance.data) {
            boolean exist = data != null;
            byteBuf.writeBoolean(exist);
            if (exist) {
                NodeData.toNetwork(byteBuf, data);
            }
        }
    }
    
    public static FriendAstrolabeInstance fromNBT(CompoundTag tag) {
        int length = tag.getInt("length");
        NodeData[] array = new NodeData[length];
        for (int i = 0; i < length; i++) {
            String key = INDEX_PREFIX + i;
            if (!tag.contains(key))
                continue;
            CompoundTag dataTag = tag.getCompound(key);
            array[i] = NodeData.deserializeNBT(dataTag);
        }
        return new FriendAstrolabeInstance(array);
    }
    
    public static void toNBT(CompoundTag tag, FriendAstrolabeInstance instance) {
        tag.putInt("length", instance.data.length);
        NodeData[] array = instance.data;
        for (int i = 0; i < instance.data.length; i++) {
            if (array[i] == null)
                continue;
            CompoundTag dataTag = new CompoundTag();
            NodeData.serializeNBT(dataTag, array[i]);
            tag.put(INDEX_PREFIX + i, dataTag);
        }
    }

    public int indexOf(@NotNull NodeData data) {
        for (int i = 0; i < this.data.length; i++) if (data == this.data[i]) return i;
        return -1;
    }

    public FriendAstrolabeInstance(NodeData[] data) {
        this.data = data;
        for (NodeData datum : this.data) {
            if (datum != null) {
                this.count++;
            } else if (this.next == -1) {
                this.next = count;
            }
        }

        if (this.next == -1) {
            this.next = 0;
        }
    }

    public boolean isFulled() {
        return this.count == this.data.length;
    }

    public NodeData addFriend(UUID uuid) {
        if (this.isFulled())
            return null;
        NodeData add = new NodeData(new FriendData(uuid, "", ""), (byte) 0);
        this.put(add);
        return add;
    }

    @Nullable
    public NodeData addFriend(Player player) {
        if (this.isFulled())
            return null;

        String name = player.getName().getString();
        NodeData add = new NodeData(
                new FriendData(player.getUUID(), name, name),
                (byte) 0
        );
        this.put(add);
        return add;
    }

    public List<NodeData> getNodes() {
        return new ObjectArrayList<>(this.data);
    }

    /* unchecked */
    public NodeData get(int index) {
        return this.data[index];
    }

    /* unchecked */
    public NodeData peek(int index) {
        NodeData temp = this.data[index];
        this.data[index] = null;
        this.count --;
        if (this.count != 0) {
            for (int i = 0; i < this.data.length; i++) {
                if (this.data[i] == null) {
                    this.next = i;
                    break;
                }
            }
        }
        this.dirty = true;
        return temp;
    }

    public void peek(NodeData first) {
        int i = this.indexOf(first);
        if (i != -1) {
            this.data[i] = null;
            this.count--;
            if (this.count != 0) {
                for (int j = 0; j < this.data.length; j++) {
                    if (this.data[j] == null) {
                        this.next = j;
                        break;
                    }
                }
            }
        }
        this.dirty = true;
    }

    public void swap(int a, int b) {
        NodeData temp = this.data[a];
        this.data[a] = this.data[b];
        this.data[b] = temp;
        this.dirty = true;
    }

    public boolean occupied(int index) {
        return this.data[index] != null;
    }

    public void put(NodeData data) {
        if (this.isFulled())
            return;

        this.data[next] = data;
        this.dirty = true;

        if (this.isFulled())
            return;
        for (int i = next; i < this.data.length; i++) {
            if (this.data[i] == null) {
                this.next = i;
                break;
            }
        }
    }

    public void put(int index, NodeData data) {
        if (this.occupied(index))
            return;

        this.data[index] = data;
        this.dirty = true;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void update() {
        for (NodeData datum : this.data) {
            if (datum == null) {
                continue;
            }

            datum.setFlag(Flag.SENT, false);
            datum.setFlag(Flag.RECEIVED, false);
        }
    }

    public static class NodeData {
        private final FriendData friendData;
        private byte flag;

        public NodeData(FriendData friendData) {
            this.friendData = friendData;
        }

        public NodeData(FriendData friendData, byte flag) {
            this.friendData = friendData;
            this.flag = flag;
        }

        public static NodeData fromNetwork(FriendlyByteBuf byteBuf) {
            FriendData uuid = FriendData.fromNetwork(byteBuf);
            byte flag = byteBuf.readByte();
            return new NodeData(uuid, flag);
        }

        public static void toNetwork(FriendlyByteBuf byteBuf, NodeData data) {
            FriendData.toNetwork(byteBuf, data.friendData);
            byteBuf.writeByte(data.flag);
        }

        public static void serializeNBT(CompoundTag dataTag, NodeData nodeData) {
            FriendData.toNBT(dataTag, nodeData.friendData);
            dataTag.putByte("flag", nodeData.flag);
        }

        public static NodeData deserializeNBT(CompoundTag dataTag) {
            FriendData uuid = FriendData.fromNBT(dataTag);
            byte flag = dataTag.getByte("flag");
            return new NodeData(uuid, flag);
        }

        public FriendData getFriendData() {
            return friendData;
        }

        public void setFlag(Flag flag, boolean yes) {
            if (yes) {
                this.flag |= flag.mask;
            } else {
                this.flag ^= flag.mask;
            }
        }

        public boolean hasFlag(Flag flag) {
            return (this.flag & flag.mask) != 0;
        }
    }

    public enum Flag {
        ONLINE((byte) 1),
        SENT((byte) 2),
        RECEIVED((byte) 4);

        final byte mask;

        Flag(byte mask) {
            this.mask = mask;
        }
    }
}
