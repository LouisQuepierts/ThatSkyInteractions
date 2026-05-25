package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.render.GameRendererUpdateEvent;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientTickHandler {

    private static final List<Task> ticks = new ArrayList<>();

    public static void register(@NonNull final Task task) {
        ClientTickHandler.ticks.add(task);
    }

    @SubscribeEvent
    public static void onClientTick(final GameRendererUpdateEvent event) {
        final var minecraft = Minecraft.getInstance();
        final var tracker   = minecraft.getDeltaTracker();

        final var delta     = tracker.getRealtimeDeltaTicks() * 0.05f;

        for (final var task : ticks) {
            task            .tick(delta);
        }
    }

    @FunctionalInterface
    public interface Task {
        void tick(final float delta);
    }

}
