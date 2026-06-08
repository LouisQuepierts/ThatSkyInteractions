package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientCameraSystem {

    private static final CameraController CONTROLLER = new CameraController();

    public static void updateMaxZoom(final float distance) {
        CONTROLLER.updateMaxZoom(distance);
    }

    @SubscribeEvent
    public static void onScroll(final InputEvent.MouseScrollingEvent event) {
        if (CONTROLLER.isEnabled()) {
            return;
        }
        event.setCanceled(true);
        CONTROLLER.onScroll((float) event.getScrollDeltaY());
    }

    @SubscribeEvent
    public static void onLocalPlayerTurn(final LocalPlayerTurnEvent event) {
        if (CONTROLLER.isEnabled()) {
            return;
        }

        CONTROLLER.turn((float) event.getXo(), (float) event.getYo());

        if (!Minecraft.getInstance().hasAltDown()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(final ViewportEvent.ComputeCameraAngles event) {
        CONTROLLER.onComputeCameraAngles(
                event.getYaw(),
                event.getPitch(),
                Minecraft.getInstance().options.getCameraType().isMirrored(),
                (yaw, pitch) -> {
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                }
        );
    }

    @SubscribeEvent
    public static void onCalculateCameraDistance(final CalculateDetachedCameraDistanceEvent event) {
        CONTROLLER.onCalculateCameraDistance(
                event.getDistance(),
                event::setDistance
        );
    }
}