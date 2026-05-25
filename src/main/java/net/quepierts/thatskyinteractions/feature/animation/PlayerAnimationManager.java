package net.quepierts.thatskyinteractions.feature.animation;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.animation.humanoid.PlayerAnimation;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.IntFunction;

@Slf4j
public final class PlayerAnimationManager extends DataSyncManager<PlayerAnimationDefinition> {

    public static final Identifier IDENTIFIER
            = ThatSkyInteractions.location("animation/definition");

    private static final String FOLDER
            = "animation/definition";

    private static final StreamCodec<ByteBuf, Map<Identifier, PlayerAnimationDefinition>> STREAM_CODEC
            = ByteBufCodecs.map(
                    (IntFunction<Map<Identifier, PlayerAnimationDefinition>>) Object2ObjectOpenHashMap::new,
                    ByteBufCodecs.STRING_UTF8.map(
                            Identifier::parse,
                            Identifier::toString
                    ),
                    PlayerAnimationParser.ANIMATION_STREAM_CODEC
            );

    private static final PlayerAnimationManager instance
            = DataSyncSystem.register(PlayerAnimationManager::new);

    public static @NonNull PlayerAnimationManager getInstance() {
        return instance;
    }

    private Map<Identifier, Holder> map = Map.of();

    PlayerAnimationManager() {
        super(
                PlayerAnimationParser.ANIMATION_CODEC,
                FileToIdConverter.json(FOLDER),
                IDENTIFIER
        );
    }

    @Override
    protected void apply(@NonNull final Map<Identifier, PlayerAnimationDefinition> preparations) {
        var builder = ImmutableMap.<Identifier, Holder>builder();
        for (var entry : preparations.entrySet()) {
            var id = entry.getKey();
            var definition = entry.getValue();
            builder.put(id, new Holder(definition));
        }
        this.map = builder.build();

        log.info("Loaded {} player animations", this.map.size());
    }

    @Override
    protected @NonNull StreamCodec<ByteBuf, Map<Identifier, PlayerAnimationDefinition>> getStreamCodec() {
        return STREAM_CODEC;
    }

    public PlayerAnimation get(Identifier id) {
        var holder = this.map.get(id);
        if (holder == null) {
            log.error("Animation not found: {}", id);
            return null;
        }
        return holder.get();
    }

    public Iterable<Identifier> identifiers() {
        return this.map.keySet();
    }


    @RequiredArgsConstructor
    private static final class Holder {

        private final PlayerAnimationDefinition definition;
        private PlayerAnimation             animation;

        public PlayerAnimation get() {
            if (this.animation == null) {
                this.initialize();
            }

            return this.animation;
        }

        private void initialize() {

            switch (this.definition.type()) {
                case "simple":
                    this.animation = PlayerAnimation.simple(this.definition);
                    break;
                case "sequence":
                    this.animation = PlayerAnimation.sequence(this.definition);
                    break;
                default:
                    log.error("Unknown animation type: {}", this.definition.type());
            }
        }

    }
}
