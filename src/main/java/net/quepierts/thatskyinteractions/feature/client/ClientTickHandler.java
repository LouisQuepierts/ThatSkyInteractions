package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientTickHandler {

    private static final List<Task> tasks = new ArrayList<>();
    private static float            timer;

    public static void register(@NonNull final Task task) {
        ClientTickHandler.tasks.add(task);
    }

    @SubscribeEvent
    public static void onClientTick(final ClientTickEvent.Pre event) {
        final var minecraft = Minecraft.getInstance();
        final var tracker   = minecraft.getDeltaTracker();

        final var delta     = tracker.getRealtimeDeltaTicks() * 0.05f;

        for (final var task : tasks) {
            task            .tick(delta);
        }
    }

    @FunctionalInterface
    public interface Task {
        void tick(final float delta);
    }

}
