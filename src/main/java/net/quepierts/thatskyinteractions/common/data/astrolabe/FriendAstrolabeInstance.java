package net.quepierts.thatskyinteractions.common.data.astrolabe;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.common.data.FriendData;
import net.quepierts.thatskyinteractions.common.data.manager.AstrolabeManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendAstrolabeInstance {
    public static final Codec<FriendAstrolabeInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Holder.CODEC.listOf().fieldOf("data").forGetter(FriendAstrolabeInstance::values)
    ).apply(instance, FriendAstrolabeInstance::new));

    public static final StreamCodec<ByteBuf, FriendAstrolabeInstance> STREAM_CODED = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, Holder.STREAM_CODEC),
            FriendAstrolabeInstance::values,
            FriendAstrolabeInstance::new
    );

    private static final String INDEX_PREFIX = "i";
    private final ImmutableList<Holder> data;
    private int count = 0;
    private int next = -1;
    private boolean dirty = false;

    private FriendAstrolabeInstance(List<Holder> data) {
        this.data = ImmutableList.copyOf(data);
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).exist()) {
                this.count++;
            } else if (next == -1) {
                this.next = i;
            }
        }

        if (this.next == -1) {
            this.next = 0;
        }
    }

    public FriendAstrolabeInstance(ResourceLocation first) {
        Astrolabe astrolabe = AstrolabeManager.INSTANCE.get(first);
        if (astrolabe == null) {
            throw new IllegalArgumentException("Unknown astrolabe " + first);
        }

        ImmutableList.Builder<Holder> builder = ImmutableList.builder();
        for (int i = 0; i < astrolabe.size(); i++) {
            builder.add(new Holder());
        }
        this.data = builder.build();
        this.next = 0;
    }

    public static FriendAstrolabeInstance fromNetwork(FriendlyByteBuf byteBuf) {
        return FriendAstrolabeInstance.STREAM_CODED.decode(byteBuf);
    }

    public static void toNetwork(FriendlyByteBuf byteBuf, FriendAstrolabeInstance instance) {
        FriendAstrolabeInstance.STREAM_CODED.encode(byteBuf, instance);
    }
    
    public static FriendAstrolabeInstance fromNBT(CompoundTag tag) {
        int length = tag.getInt("length");
        ImmutableList.Builder<Holder> builder = new ImmutableList.Builder<>();
        for (int i = 0; i < length; i++) {
            String key = INDEX_PREFIX + i;
            if (!tag.contains(key)) {
                builder.add(new Holder());
                continue;
            }
            CompoundTag dataTag = tag.getCompound(key);
            builder.add(new Holder(NodeData.deserializeNBT(dataTag)));
        }
        return new FriendAstrolabeInstance(builder.build());
    }
    
    public static void toNBT(CompoundTag tag, FriendAstrolabeInstance instance) {
        tag.putInt("length", instance.data.size());
        ImmutableList<Holder> array = instance.data;
        for (int i = 0; i < instance.data.size(); i++) {
            if (!array.get(i).exist())
                continue;
            CompoundTag dataTag = new CompoundTag();
            NodeData.serializeNBT(dataTag, array.get(i).getData());
            tag.put(INDEX_PREFIX + i, dataTag);
        }
    }

    public int indexOf(@NotNull NodeData data) {
        for (int i = 0; i < this.data.size(); i++) if (data == this.data.get(i).getData()) return i;
        return -1;
    }

    public boolean isFulled() {
        return this.count == this.data.size();
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

        NodeData add = new NodeData(
                new FriendData(player),
                (byte) 0
        );
        this.put(add);
        return add;
    }

    public List<NodeData> getNodes() {
        return this.data.stream().map(Holder::getData).toList();
    }

    private List<Holder> values() {
        return this.data;
    }

    /* unchecked */
    public NodeData get(int index) {
        return this.data.get(index).getData();
    }

    /* unchecked */
    public NodeData peek(int index) {
        @Nullable NodeData temp = this.data.get(index).getData();
        this.data.get(index).setData(null);
        this.count --;
        if (this.count != 0) {
            for (int i = 0; i < this.data.size(); i++) {
                if (!this.data.get(i).exist()) {
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
            this.data.get(i).setData(null);
            this.count--;
            if (this.count != 0) {
                for (int j = 0; j < this.data.size(); j++) {
                    if (!this.data.get(i).exist()) {
                        this.next = j;
                        break;
                    }
                }
            }
        }
        this.dirty = true;
    }

    public void swap(int a, int b) {
        Holder hA = this.data.get(a);
        Holder hB = this.data.get(b);
        NodeData temp = hA.getData();
        hA.setData(hB.getData());
        hB.setData(temp);
        this.dirty = true;
    }

    public boolean occupied(int index) {
        return this.data.get(index).exist();
    }

    public void put(NodeData data) {
        if (this.isFulled())
            return;

        this.data.get(next).setData(data);
        this.dirty = true;

        if (this.isFulled())
            return;
        for (int i = next; i < this.data.size(); i++) {
            if (!this.data.get(i).exist()) {
                this.next = i;
                break;
            }
        }
    }

    public void put(int index, NodeData data) {
        if (this.occupied(index))
            return;

        this.data.get(index).setData(data);
        this.dirty = true;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void update() {
        for (Holder holder : this.data) {
            if (!holder.exist()) {
                continue;
            }

            NodeData datum = holder.getData();
            datum.setFlag(Flag.SENT, false);
            datum.setFlag(Flag.RECEIVED, false);
        }
    }

    private static class Holder {
        public static final Codec<Holder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                NodeData.CODEC.optionalFieldOf("data").forGetter(Holder::toOptional)
        ).apply(instance, Holder::fromOptional));

        public static final StreamCodec<ByteBuf, Holder> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(NodeData.STREAM_CODEC),
                Holder::toOptional,
                Holder::fromOptional
        );

        private static Holder fromOptional(Optional<NodeData> data) {
            return new Holder(data.orElse(null));
        }

        @Nullable
        private NodeData data;

        public Holder() {
            this.data = null;
        }

        public Holder(@Nullable NodeData data) {
            this.data = data;
        }

        public void setData(@Nullable NodeData data) {
            this.data = data;
        }

        @Nullable
        public NodeData getData() {
            return data;
        }

        public Optional<NodeData> toOptional() {
            return Optional.ofNullable(data);
        }

        public boolean exist() {
            return data != null;
        }
    }

    public static class NodeData {
        public static final Codec<NodeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FriendData.CODEC.fieldOf("friendData").forGetter(NodeData::getFriendData),
                Codec.BYTE.fieldOf("flag").forGetter(NodeData::getFlag)
        ).apply(instance, NodeData::new));

        public static final StreamCodec<ByteBuf, NodeData> STREAM_CODEC = StreamCodec.composite(
                FriendData.STREAM_CODEC,
                NodeData::getFriendData,
                ByteBufCodecs.BYTE,
                NodeData::getFlag,
                NodeData::new
        );

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
                this.flag &= (byte) ~flag.mask;
            }
        }

        public boolean hasFlag(Flag flag) {
            return (this.flag & flag.mask) != 0;
        }

        public byte getFlag() {
            return flag;
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
