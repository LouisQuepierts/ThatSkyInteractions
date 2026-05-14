package net.quepierts.thatskyinteractions.feature.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.SnifferRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.render.AvatarRenderStateExtension;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class PlayerAnimationSystem {

    @SubscribeEvent
    public static void onBeforeRenderPlayer(RenderPlayerEvent.Pre<AbstractClientPlayer> event) {
        final var state = event.getRenderState();

        if (state instanceof AvatarRenderStateExtension extension) {
            final var animationState = extension.a4j$GetAnimationState();
        }
    }

}
