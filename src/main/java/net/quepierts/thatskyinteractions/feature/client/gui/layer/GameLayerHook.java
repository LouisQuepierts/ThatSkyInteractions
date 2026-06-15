package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.interaction.event.PlayerInteractionEvent;
import org.lwjgl.glfw.GLFW;


@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class GameLayerHook {


    @SubscribeEvent
    public static void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAboveAll(FloatingControlLayer.IDENTIFIER, FloatingControlLayer.INSTANCE);
    }

    @SubscribeEvent
    public static void onLoggedOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        FloatingControlLayer.INSTANCE.reset();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onMousePressed(final ScreenEvent.MouseButtonPressed.Post event) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        if (event.getScreen().isPauseScreen()) {
            return;
        }

        if (event.wasClickHandled()) {
            return;
        }

        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return;
        }

        FloatingControlLayer.INSTANCE.interact();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onInput(final InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem()) {
            return;
        }

        if (FloatingControlLayer.INSTANCE.interact()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onInteractionAccepted(final PlayerInteractionEvent.Accept.Post event) {
        if (!event.isClient()) {
            return;
        }

        if (!event.getReceiver().isLocalPlayer()) {
            return;
        }

        FloatingControlLayer.INSTANCE.remove(event.getRequester().getUUID());
    }

    @SubscribeEvent
    public static void onInteractionCanceled(final PlayerInteractionEvent.Cancel event) {
        if (!event.isClient()) {
            return;
        }

        if (!event.getReceiver().isLocalPlayer()) {
            return;
        }

        FloatingControlLayer.INSTANCE.remove(event.getRequester().getUUID());
    }


    public static void onRenderLayer(
            final @NonNull GuiGraphicsExtractor graphics,
            final @NonNull DeltaTracker         tracker
    ) {

        final var minecraft = Minecraft.getInstance();
        final var mouseX    = (int)(
                            minecraft.mouseHandler.xpos()
                                    * (double)minecraft.getWindow().getGuiScaledWidth()
                                    / (double)minecraft.getWindow().getScreenWidth()
        );
        final var mouseY    = (int)(
                            minecraft.mouseHandler.ypos()
                                    * (double)minecraft.getWindow().getGuiScaledHeight()
                                    / (double)minecraft.getWindow().getScreenHeight()
        );

        AnimatableScreenLayer.INSTANCE.render(graphics, tracker, mouseX, mouseY);
    }

}
