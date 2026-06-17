package net.quepierts.thatskyinteractions.feature.expression.call;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntImmutableList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jspecify.annotations.NonNull;

public final class PlayerVoiceType implements Comparable<PlayerVoiceType> {

    //                                                                   C  D   E  G   A
    public static final IntList         DEFAULT_NOTES   = IntList.of(6, 8, 10, 13, 15);
    public static final Identifier      DEFAULT_ID      = ThatSkyInteractions.location("none");
    public static final Identifier      DEFAULT_ICON    = ThatSkyInteractions.location("voice/none");
    public static final PlayerVoiceType DEFAULT         = new PlayerVoiceType(
                                                                DEFAULT_NOTES,
                                                                DEFAULT_ICON,
                                                                SoundEvents.NOTE_BLOCK_PLING,
                                                                -100
                                                        );

    public static final Codec<PlayerVoiceType> CODEC
            = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.listOf(1, 16).<IntList>xmap(
                            IntImmutableList::new,
                            i -> i
                    ).optionalFieldOf("notes", DEFAULT_NOTES).forGetter(PlayerVoiceType::getNoteList),
                    Identifier.CODEC.fieldOf("icon").forGetter(PlayerVoiceType::getIcon),
                    SoundEvent.CODEC.fieldOf("sound").forGetter(PlayerVoiceType::getSound),
                    Codec.INT.optionalFieldOf("priority", 0).forGetter(PlayerVoiceType::getPriority)
            ).apply(instance, PlayerVoiceType::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVoiceType> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.collection(IntArrayList::new, ByteBufCodecs.INT),
                    PlayerVoiceType::getNoteList,
                    Identifier.STREAM_CODEC,
                    PlayerVoiceType::getIcon,
                    SoundEvent.STREAM_CODEC,
                    PlayerVoiceType::getSound,
                    ByteBufCodecs.VAR_INT,
                    PlayerVoiceType::getPriority,
                    PlayerVoiceType::new
            );

    private final IntList               notes;

    @Getter
    private final Identifier            icon;

    @Getter
    private final Holder<SoundEvent>    sound;

    @Getter
    private final int                   priority;

    public int getNote(
            final @NonNull RandomSource random
    ) {
        return this.notes.getInt(random.nextInt(this.notes.size()));
    }

    private PlayerVoiceType(
            final @NonNull IntList              notes,
            final @NonNull Identifier           icon,
            final @NonNull Holder<SoundEvent>   sound,
            final          int                  priority
    ) {
        this.notes                              = notes;
        this.icon                               = icon;
        this.sound                              = sound;
        this.priority                           = priority;
    }

    private IntList getNoteList() {
        return this.notes;
    }

    public boolean available() {
        return this != DEFAULT && this.sound.isBound();
    }

    @Override
    public int compareTo(final @NonNull PlayerVoiceType o) {
        return Integer.compare(this.priority, o.priority);
    }
}
