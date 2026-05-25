package net.quepierts.thatskyinteractions.feature.animation.bedrock;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.animation.bedrock.BedrockAnimation;
import net.quepierts.thatskyinteractions.core.model.animation.bedrock.BedrockAnimationDefinition;
import net.quepierts.thatskyinteractions.feature.network.PacketCache;
import net.quepierts.thatskyinteractions.feature.network.SyncDatapackPacket;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.function.IntFunction;

@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class BedrockAnimationManager extends SimpleJsonResourceReloadListener<BedrockAnimationDefinition> {

    public static final Identifier IDENTIFIER = ThatSkyInteractions.location("animation/source");

    private static final String FOLDER = "animation/source";

    private static BedrockAnimationManager instance;

    private static final StreamCodec<ByteBuf, Map<Identifier, BedrockAnimationDefinition>> STREAM_CODEC =
            ByteBufCodecs.map(
                    (IntFunction<Map<Identifier, BedrockAnimationDefinition>>) Object2ObjectOpenHashMap::new,
                    ByteBufCodecs.STRING_UTF8.map(
                            Identifier::parse,
                            Identifier::toString
                    ),
                    BedrockAnimationParser.ANIMATION_DEFINITION_STREAM_CODEC
            );

    @SubscribeEvent
    public static void onAddReloadListeners(final AddServerReloadListenersEvent event) {
        if (instance != null) {
            instance.cache.free();
        }
        event.addListener(
                IDENTIFIER,
                instance = new BedrockAnimationManager()
        );
    }

    @SubscribeEvent
    public static void onDatapackSync(final OnDatapackSyncEvent event) {
        if (instance == null || !instance.cache.ready()) {
            return;
        }

        event.getRelevantPlayers().forEach(player -> {
            PacketDistributor.sendToPlayer(
                    player,
                    new SyncDatapackPacket(
                            "animation_source",
                            instance.cache
                    )
            );
        });
    }

    public static @NonNull BedrockAnimationManager getInstance() {
        if (instance == null) {
            instance = new BedrockAnimationManager();
        }
        return instance;
    }

    private final PacketCache                           cache       = new PacketCache();

    private Map<Identifier, BedrockAnimationDefinition> definitions = Map.of();
    private Map<Identifier, BedrockAnimation>           animations  = Map.of();

    private BedrockAnimationManager() {
        super(
                BedrockAnimationParser.ANIMATION_DEFINITION_CODEC,
                FileToIdConverter.json(FOLDER)
        );
    }

    @Override
    protected void apply(
            final           Map<Identifier, BedrockAnimationDefinition> preparations,
            final @NonNull  ResourceManager                             manager,
            final @NonNull  ProfilerFiller                              profiler
    ) {
        this.sync(preparations);

        NeoForge.EVENT_BUS.post(new BedrockAnimationReloadedEvent(this));

        this.cache.encode(
                STREAM_CODEC,
                this.definitions
        );
    }

    public BedrockAnimationDefinition getDefinition(Identifier identifier) {
        return this.definitions.get(identifier);
    }

    public BedrockAnimation getAnimation(Identifier identifier) {
        return this.animations.get(identifier);
    }

    public void sync(final Map<Identifier, BedrockAnimationDefinition> definitions) {
        var builder         = ImmutableMap.<Identifier, BedrockAnimationDefinition>builder();
        var builder2        = ImmutableMap.<Identifier, BedrockAnimation>builder();

        for (final var entry : definitions.entrySet()) {
            final var identifier = entry.getKey();
            builder.put(identifier, entry.getValue());

            for (final var entry2 : entry.getValue().animations().entrySet()) {
                builder2.put(
                        identifier.withSuffix("." + entry2.getKey()),
                        entry2.getValue()
                );
            }
        }

        this.definitions    = builder.build();
        this.animations     = builder2.build();
    }

    public void sync(final PacketCache cache) {
        this.sync(cache.decode(STREAM_CODEC));
    }

    public record Holder(
            Identifier identifier,
            BedrockAnimationDefinition definition
    ) { }
}
