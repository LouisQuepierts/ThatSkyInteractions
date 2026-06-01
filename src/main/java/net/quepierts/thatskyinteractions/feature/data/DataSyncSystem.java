package net.quepierts.thatskyinteractions.feature.data;

import lombok.experimental.UtilityClass;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.ParentOverrideManager;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationManager;
import net.quepierts.thatskyinteractions.feature.animation.bedrock.BedrockAnimationManager;
import net.quepierts.thatskyinteractions.feature.data.event.RegisterSyncManagerEvent;
import net.quepierts.thatskyinteractions.feature.data.friendship.FriendshipTreeManager;
import net.quepierts.thatskyinteractions.feature.network.SyncDatapackPacket;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@UtilityClass
@SuppressWarnings("unused")
@EventBusSubscriber(modid = ThatSkyInteractions.MODID)
public class DataSyncSystem {

    private static final List<DataSyncManager<?>> MANAGERS = new ArrayList<>();

    private static boolean initialized = false;

    @SubscribeEvent
    public static void onAddReloadListeners(final AddServerReloadListenersEvent event) {
        for (final var manager : MANAGERS) {
            manager.getCache().free();
            event.addListener(
                    manager.getIdentifier(),
                    manager
            );
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(final OnDatapackSyncEvent event) {
        event.getRelevantPlayers().forEach(DataSyncSystem::sync);
    }

    public static void register() {
        if (initialized) {
            return;
        }

        initialized = true;

        final var event = new RegisterSyncManagerEvent(MANAGERS);
        NeoForge.EVENT_BUS.post(event);
        event.register();
    }

    private static void sync(@NonNull ServerPlayer player) {
        final DataSyncManager<?> first = MANAGERS.getFirst();
        var payload  = new SyncDatapackPacket(
                0,
                first.getCache()
        );
        var payloads = new SyncDatapackPacket[MANAGERS.size() - 1];
        for (int i = 1; i < MANAGERS.size(); i++) {
            payloads[i - 1] = new SyncDatapackPacket(
                   i,
                    MANAGERS.get(i).getCache()
            );
        }
        PacketDistributor.sendToPlayer(
                player,
                payload,
                payloads
        );
    }

    public static void handle(final SyncDatapackPacket packet) {
        final DataSyncManager<?> manager = MANAGERS.get(packet.id());
        manager.handle(packet);
    }

}
