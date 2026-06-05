package net.quepierts.thatskyinteractions.feature.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.extensions.IRenderStateExtension;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationController;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import net.quepierts.thatskyinteractions.feature.client.render.EntityModelExtension;
import net.quepierts.thatskyinteractions.feature.client.renderstate.AnimationStateModifier;
import org.joml.*;

@Slf4j
@UtilityClass
public class PlayerAnimationHook {

    public static void onSetupAnimation(
            final PlayerAnimationController controller,
            final MinecraftModelAdaptor     adaptor
    ) {
        if (!controller.isPlaying()) {
            return;
        }

        if (controller.isTicked() && !controller.isResolved()) {
            controller.getAnimation().resolve(
                    controller.getFsmState(),
                    controller.getExecutionState(),
                    controller.getState(),
                    controller.getCache(),
                    adaptor.link(DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID));
            controller.markResolved();
        }

        if (controller.isResolved()) {
            adaptor.setAlpha(controller.getAlpha());
            adaptor.accept(controller.getCache());
        }
    }

    public static <S> void onSetupRootAnimation(
            final SubmitNodeStorage.ModelSubmit<S>  submit,
            final PoseStack                         poseStack
    ) {

        final var state     = submit.state();
        if (!(state instanceof IRenderStateExtension extension)) {
            return;
        }

        final var controller = extension.getRenderData(AnimationStateModifier.CONTEXT_KEY);
        if (controller == null) {
            return;
        }

        if (!controller.isPlaying() || !controller.isResolved()) {
            return;
        }

        final var cache     = controller.getCache();
        final var root      = cache.get(0);

        final var quat      = new Quaternionf(
                root.getRx(),
                root.getRy(),
                root.getRz(),
                root.getRw()
        );

        final var factor =  (0.0625f);
        final var xo = root.getTx() * factor;
        final var yo = root.getTy() * factor + 1;
        final var zo = root.getTz() * factor;

        poseStack.translate(xo, yo, zo);
        poseStack.mulPose(quat);
        poseStack.translate(0, -1, 0);
    }

    public static boolean onSetupCameraAnimation(
            final PlayerAnimationController controller,
            final Camera                    camera,
            final Vector3d                  ioPosition,
            final Vector3f                  ioRotation,
            final float                     partialTicks
    ) {

        if (!controller.isPlaying()) {
            return false;
        }

        final var entity = camera.entity();

        if (!(entity instanceof AbstractClientPlayer player)) {
            return false;
        }

        final var minecraft = Minecraft.getInstance();

        final var firstPerson = minecraft.options.getCameraType().isFirstPerson();
        if (firstPerson) {
            controller.update(partialTicks);

            final var renderer = minecraft.getEntityRenderDispatcher().getPlayerRenderer(player);
            final var adaptor = ((EntityModelExtension) renderer.getModel()).a4j$GetModelAdaptor();

            onSetupAnimation(controller, adaptor);

            final var cache = controller.getCache();
            final var root = cache.get(0);

            final var head = adaptor.getSkeleton()
                    .get("head")
                    .part();

            if (head == null) {
                return false;
            }

            var a           = controller.getAlpha() * 0.0625f;
            var position    = new Vector3f(
                    root.getTx(),
                    root.getTy(),
                    root.getTz()
            );

            var xRot = player.getXRot();
            final var alpha = controller.getAlpha();
            var rotation = new Vector3f(
                    head.xRot,
                    head.yRot,
                    -head.zRot
            ).mul(Mth.RAD_TO_DEG).sub(xRot, 0, 0).mul(alpha);
            ioRotation.add(rotation);
            position.add(
                    head.x,
                    head.y,
                    head.z
            );

            position.mul(a, -a, a);
            ioPosition.add(position);
        }

        return true;
    }
}
