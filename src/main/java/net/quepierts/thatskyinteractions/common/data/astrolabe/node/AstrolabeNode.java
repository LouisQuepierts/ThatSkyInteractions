package net.quepierts.thatskyinteractions.common.data.astrolabe.node;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;

public class AstrolabeNode {
    public static final Codec<AstrolabeNode> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(AstrolabeNode::type),
            Codec.INT.fieldOf("x").forGetter(AstrolabeNode::getX),
            Codec.INT.fieldOf("y").forGetter(AstrolabeNode::getY),
            DescriptionPosition.CODEC.optionalFieldOf("position").forGetter(node -> Optional.of(node.namePosition))
    ).apply(instance, (type, x, y, position) -> construct(type, x, y, position.orElse(DescriptionPosition.DOWN))));

    public static final StreamCodec<ByteBuf, AstrolabeNode> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            AstrolabeNode::type,
            ByteBufCodecs.VAR_INT,
            AstrolabeNode::getX,
            ByteBufCodecs.VAR_INT,
            AstrolabeNode::getY,
            DescriptionPosition.STREAM_CODEC,
            AstrolabeNode::getNamePosition,
            AstrolabeNode::construct
    );

    private static final Factory DEFAULT_FACTORY = new Factory(AstrolabeNode::new, AstrolabeNode::new);
    private static final Object2ObjectMap<String, Factory> FACTORIES = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<String, Constructor> CONSTRUCTORS = new Object2ObjectOpenHashMap<>();
    public final int x;
    public final int y;
    public final DescriptionPosition namePosition;

    protected AstrolabeNode(int x, int y, DescriptionPosition descriptionPosition) {
        this.x = x;
        this.y = y;
        this.namePosition = descriptionPosition;
    }

    private static AstrolabeNode construct(String type, int x, int y, DescriptionPosition position) {
        return CONSTRUCTORS.getOrDefault(type, AstrolabeNode::new).construct(x, y, position);
    }

    public static AstrolabeNode serialize(JsonObject obj) {
        String type = obj.get("type").getAsString();
        return FACTORIES.getOrDefault(type, DEFAULT_FACTORY).fromJson.apply(obj);
    }

    protected String type() {
        return "";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public DescriptionPosition getNamePosition() {
        return namePosition;
    }

    public AstrolabeNode(JsonObject jsonObject) {
        this.x = jsonObject.get("x").getAsInt();
        this.y = jsonObject.get("y").getAsInt();

        if (jsonObject.has("name_position")) {
            this.namePosition = DescriptionPosition.fromString(jsonObject.get("name_position").getAsString());
        } else {
            this.namePosition = DescriptionPosition.DOWN;
        }
    }

    public AstrolabeNode(FriendlyByteBuf byteBuf) {
        this.x = byteBuf.readVarInt();
        this.y = byteBuf.readVarInt();
        this.namePosition = byteBuf.readEnum(DescriptionPosition.class);
    }

    protected record Factory(
            Function<JsonObject, AstrolabeNode> fromJson,
            Function<FriendlyByteBuf, AstrolabeNode> fromNetwork
    ) {}

    public static void register() {
        FACTORIES.put("", DEFAULT_FACTORY);
        FACTORIES.put("friend", new Factory(FriendNode::new, FriendNode::new));

        CONSTRUCTORS.put("", AstrolabeNode::new);
        CONSTRUCTORS.put("friend", FriendNode::new);
    }

    @FunctionalInterface
    public interface Constructor {
        AstrolabeNode construct(int x, int y, DescriptionPosition position);
    }

    public enum DescriptionPosition implements StringRepresentable {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        public static final IntFunction<DescriptionPosition> BY_ID = ByIdMap.continuous(
                DescriptionPosition::ordinal,
                DescriptionPosition.values(),
                ByIdMap.OutOfBoundsStrategy.ZERO
        );
        public static final Codec<DescriptionPosition> CODEC = StringRepresentable.fromEnum(DescriptionPosition::values);
        public static final StreamCodec<ByteBuf, DescriptionPosition> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, DescriptionPosition::ordinal);

        public static DescriptionPosition fromString(String value) {
            return REF.getOrDefault(value.toLowerCase(Locale.ROOT), DOWN);
        }

        private static final ImmutableMap<String, DescriptionPosition> REF;

        static {
            ImmutableMap.Builder<String, DescriptionPosition> builder = ImmutableMap.builder();
            for (DescriptionPosition value : DescriptionPosition.values()) {
                builder.put(value.name().toLowerCase(Locale.ROOT), value);
            }
            REF = builder.build();
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return name().toLowerCase();
        }
    }
}
