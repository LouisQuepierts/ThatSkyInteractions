package net.quepierts.thatskyinteractions.feature.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.extensions.IRenderStateExtension;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
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
            controller.resolve(adaptor.link(DefaultMinecraftSkeletonPipeline.MODIFIED_HUMANOID));
        }

        if (controller.isResolved()) {
            controller.apply(adaptor);
        }
    }

    public static  void onSetupRootAnimation(
            final Object                            state,
            final PoseStack                         poseStack
    ) {

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

        final var root      = controller.getRootTransform();

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

        // todo: WIP, still have errors on rotation
        // todo: add part locator in animation controller, for further utilization
        if (!firstPerson) {
            return false;
        }

        if (controller.isUnlocked(PlayerBone.HEAD)) {
            return false;
        }

        controller.update(partialTicks);

        final var renderer = minecraft.getEntityRenderDispatcher().getPlayerRenderer(player);
        final var adaptor = ((EntityModelExtension) renderer.getModel()).a4j$GetModelAdaptor();

        onSetupAnimation(controller, adaptor);

        final var root = controller.getRootTransform();
        final var head = adaptor.getSkeleton()
                .get(1) // normally, head is the second bone: idx == 1
                .part();

        if (head == null) {
            return false;
        }

        final var alpha     = controller.getRootAlpha();

        final var vector    = new Vector3f();

        final var rootRot   = new Quaternionf(
                                root.getRx(),
                                root.getRy(),
                                root.getRz(),
                                root.getRw()
                            );

        final var factor    = 0.0625f * 0.9375f;
        final var blend     = alpha * factor;
        final var eyeHeight = entity.getEyeHeight() * 16.0f;
        final var yBodyRot  = entity.getPreciseBodyRotation(partialTicks);
        final var position  = new Matrix4f()
                            .rotateY((180F - yBodyRot) * Mth.DEG_TO_RAD)
                            .scale(-blend, -blend, blend)
                            .translate(root.getTx(), root.getTy() + eyeHeight, root.getTz())
                            .rotate(rootRot)
                            .translate(head.x(), head.y() - eyeHeight, head.z())
                            .transformPosition(vector);

        ioPosition          .add(position);

        var xRot            = player.getXRot(partialTicks);

        // todo: fix the rotation
        var yRot            = Mth.wrapDegrees(player.getYRot(partialTicks) - yBodyRot);
        var rotation        = rootRot
                            .rotateYXZ(
                                head.yRot(),
                                head.xRot(),
                                -head.zRot()
                             )
                            .getEulerAnglesYXZ(vector)
                            .mul(Mth.RAD_TO_DEG)
                            .sub(xRot, 0, 0)
                            .mul(alpha);

        ioRotation          .add(rotation);

        return true;
    }
}
