package net.quepierts.thatskyinteractions.feature.expression.call;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.data.DataSyncManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

@Slf4j
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public final class PlayerVoiceTypeManager extends DataSyncManager<PlayerVoiceType> {

    public static final String FOLDER = "preference/voice";

    @Getter
    private static final PlayerVoiceTypeManager instance
            = new PlayerVoiceTypeManager();

    private @NonNull Map<Identifier, PlayerVoiceType> voices = Map.of();

    PlayerVoiceTypeManager() {
        super(
                PlayerVoiceType.CODEC,
                PlayerVoiceType.STREAM_CODEC,
                FOLDER
        );
    }

    @SubscribeEvent
    public static void onRegisterSyncManager(final RegisterSyncManagerEvent event) {
        event.register(instance);
    }

    @Override
    protected void apply(@NonNull final Map<Identifier, PlayerVoiceType> preparations) {
        final var builder = ImmutableMap.<Identifier, PlayerVoiceType>builderWithExpectedSize(preparations.size() + 1);
        builder.put(PlayerVoiceType.DEFAULT_ID, PlayerVoiceType.DEFAULT);
        builder.putAll(preparations);
        this.voices = builder.build();

        log.info("Loaded {} player voice types", preparations.size());
    }

    public @Nullable PlayerVoiceType get(final @NonNull Identifier identifier) {
        return this.voices.get(identifier);
    }

    public @NonNull PlayerVoiceType getNonNull(final @NonNull Identifier identifier) {
        return this.voices.getOrDefault(identifier, PlayerVoiceType.DEFAULT);
    }

    public @NonNull Collection<Identifier> identifiers() {
        return this.voices.keySet();
    }

    public boolean has(final @NonNull Identifier identifier) {
        return this.voices.containsKey(identifier);
    }
}
