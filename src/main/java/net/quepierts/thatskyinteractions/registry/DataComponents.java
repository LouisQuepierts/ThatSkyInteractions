package net.quepierts.thatskyinteractions.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

public class DataComponents {
    public static final DeferredRegister.DataComponents REGISTER = DeferredRegister.createDataComponents(ThatSkyInteractions.MODID);

    public static final Codec<Vec3i> VEC3I_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(Vec3i::getX),
                    Codec.INT.fieldOf("y").forGetter(Vec3i::getY),
                    Codec.INT.fieldOf("z").forGetter(Vec3i::getZ)
            ).apply(instance, Vec3i::new)
    );

    public static final StreamCodec<ByteBuf, Vec3i> VEC3I_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Vec3i::getX,
            ByteBufCodecs.VAR_INT, Vec3i::getY,
            ByteBufCodecs.VAR_INT, Vec3i::getZ,
            Vec3i::new
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Vec3i>> VEC3I = REGISTER.registerComponentType(
            "block_pos",
            builder -> builder
                    .persistent(VEC3I_CODEC)
                    .networkSynchronized(VEC3I_STREAM_CODEC)
    );
}
