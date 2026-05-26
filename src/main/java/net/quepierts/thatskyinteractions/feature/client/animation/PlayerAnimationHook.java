package net.quepierts.thatskyinteractions.feature.client.animation;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.neoforged.fml.common.EventBusSubscriber;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelSkeleton;
import net.quepierts.thatskyinteractions.feature.client.render.EntityModelExtension;
import org.joml.*;

@Slf4j
@EventBusSubscriber
public final class PlayerAnimationHook {

    public static void onSetupAnimation(
            final HumanoidAnimationState animation,
            final MinecraftModelAdaptor adaptor
    ) {
        if (!animation.isPlaying()) {
            return;
        }

        if (animation.isTicked() && !animation.isResolved()) {
            animation.getAnimation().resolve(
                    animation.getFsmState(),
                    animation.getExecutionState(),
                    animation,
                    adaptor.link(DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID)
            );
            animation.markResolved();
        }

        adaptor.setAlpha(animation.getAlpha());
        adaptor.accept(animation.getCache());
    }

    public static boolean onSetupCameraAnimation(
            final HumanoidAnimationState animation,
            final Camera camera,
            final Vector3d ioPosition,
            final Vector3f ioRotation,
            final float partialTicks
    ) {

        if (!animation.isPlaying()) {
            return false;
        }

        final var entity = camera.entity();

        if (!(entity instanceof AbstractClientPlayer player)) {
            return false;
        }

        final var minecraft = Minecraft.getInstance();

        final var firstPerson = minecraft.options.getCameraType().isFirstPerson();
        if (firstPerson) {
            animation.update(partialTicks);
        }

        final var renderer = minecraft.getEntityRenderDispatcher().getPlayerRenderer(player);
        final var adaptor = ((EntityModelExtension) renderer.getModel()).a4j$GetModelAdaptor();

        onSetupAnimation(animation, adaptor);

        final var cache = animation.getCache();
        final var root = cache.get(0);
        final var head = adaptor.getSkeleton()
                            .get("head")
                            .part();

        if (head == null) {
            return false;
        }

        var a           = animation.getAlpha() * 0.0625f;
        var position    = new Vector3f(
                        root.getTx() + head.x,
                        root.getTy() + head.y,
                        root.getTz() + head.z
                        )
                        .mul(a, -a, a);

        if (firstPerson) {
            var yRot = (player.yHeadRot - player.yBodyRot);
            var xRot = player.getXRot();
            final var alpha = animation.getAlpha();
            var rotation = new Vector3f(
                    head.xRot,
                    head.yRot,
                    -head.zRot
            ).mul(Mth.RAD_TO_DEG).sub(xRot, yRot, 0).mul(alpha);
            ioRotation.add(rotation);

        }

        ioPosition.add(position);

        return true;
    }

}
