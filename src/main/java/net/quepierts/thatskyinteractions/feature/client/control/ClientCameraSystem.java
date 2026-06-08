package net.quepierts.thatskyinteractions.feature.client.control;

import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculateDetachedCameraDistanceEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerMovedEvent;
import net.quepierts.thatskyinteractions.feature.client.control.event.LocalPlayerTurnEvent;
import net.quepierts.thatskyinteractions.feature.client.input.TsiKeys;
import org.lwjgl.glfw.GLFW;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class ClientCameraSystem {

    private static final CameraController CONTROLLER = new CameraController();

    public static void updateMaxZoom(final float distance) {
        CONTROLLER.updateMaxZoom(distance);
    }

    @SubscribeEvent
    public static void onScroll(final InputEvent.MouseScrollingEvent event) {
        if (CONTROLLER.isLocked()) {
            return;
        }
        event.setCanceled(true);
        CONTROLLER.onScroll((float) event.getScrollDeltaY());
    }

    @SubscribeEvent
    public static void onLocalPlayerTurn(final LocalPlayerTurnEvent event) {
        if (CONTROLLER.isLocked()) {
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLocalPlayerMoved(final LocalPlayerMovedEvent event) {
        if (CONTROLLER.isLocked()) {
            return;
        }

        if (!Minecraft.getInstance().hasAltDown()) {
            return;
        }

        final var cameraYRot    = CONTROLLER.getYRot();
        final var inputVector   = event.getMoveVector();
        // turn player by move vector
        final var player        = event.getPlayer();
        float delta             = (float) Math.toDegrees(Math.atan2(-inputVector.x, inputVector.y));
        float targetYRot        = Mth.wrapDegrees(cameraYRot + delta);
        float difference        = Mth.wrapDegrees(targetYRot - player.getYRot());

        player.turn(difference, 0.0f);

        final var origin    = event.getInput();
        final var forward   = new Input(
                true, false, false, false,
                origin.jump(),
                origin.shift(),
                origin.sprint()
        );
        event.redirect(
                forward,
                Vec2.UNIT_Y
        );
    }

    @SubscribeEvent
    public static void onKeyInput(final InputEvent.Key event) {

        final var matches = TsiKeys.KEY_UNLOCK_CAMERA.matches(event.getKeyEvent());

        if (matches && event.getAction() == GLFW.GLFW_PRESS) {
            CONTROLLER.toggle();
        }

    }

    @SubscribeEvent
    public static void onLoggedOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        CONTROLLER.toggle(false);
    }
}