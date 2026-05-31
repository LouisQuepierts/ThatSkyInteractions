package net.quepierts.thatskyinteractions.feature.client.animation;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.animation.DefaultMinecraftSkeletonPipeline;
import net.quepierts.thatskyinteractions.core.animation.model.PlayerBone;
import net.quepierts.thatskyinteractions.feature.animation.HumanoidAnimationState;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationSystem;
import net.quepierts.thatskyinteractions.feature.client.model.MinecraftModelAdaptor;
import net.quepierts.thatskyinteractions.feature.client.render.EntityModelExtension;
import net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor.ClientInputAccessor;
import org.joml.*;

@Slf4j
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public final class PlayerAnimationHook {

    public static void onRestrictPlayerTurn(
            final LocalPlayer   player,
            final double        xo,
            final double        yo
    ) {
        final var data          = PlayerAnimationSystem.getAnimationData(player);
        final var animation     = data.getAnimation();

        if (animation.isPlaying()) {
            final var definition    = animation.getDefinition();
            final var minecraft     = Minecraft.getInstance();

            final var firstPerson   = minecraft.options.getCameraType().isFirstPerson();
            final var lock          = !definition.unlock().contains(PlayerBone.HEAD);

            if (firstPerson && lock) {
                return;
            }


            final var deltaY        = (float) xo * 0.15f;
            final var headDiff0     = Mth.abs(Mth.wrapDegrees(player.getYRot() - player.yBodyRot));
            final var headDiff1     = Mth.abs(Mth.wrapDegrees(player.getYRot() - player.yBodyRot - deltaY));
            final var maxDiff       = player.isBlocking() ? 14f : 49f;

            if (headDiff0 > maxDiff && headDiff1 < headDiff0) {
                player.turn(0.0, yo);
                return;
            }
        }

        player.turn(xo, yo);
    }

    @SubscribeEvent
    public static void onRestrictPlayerMotion(final MovementInputUpdateEvent event) {
        final var player        = event.getEntity();
        final var input         = event.getInput();
        final var data          = PlayerAnimationSystem.getAnimationData(player);
        final var animation     = data.getAnimation();

        if (!animation.isPlaying()) {
            return;
        }

        final var definition    = animation.getDefinition();

        if (definition.restrictMotion()) {
            input.keyPresses    = Input.EMPTY;
            ((ClientInputAccessor) input).a4j$setMoveVector(Vec2.ZERO);
        }
    }

    public static boolean shouldLimitPlayerMotion() {

        final var animation = PlayerAnimationHook.getClientAnimationState();

        if (!animation.isPlaying()) {
            return false;
        }

        return animation.getDefinition().restrictMotion();

    }

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

    public static HumanoidAnimationState getClientAnimationState() {
        final var player        = Minecraft.getInstance().player;
        final var data          = PlayerAnimationSystem.getAnimationData(player);

        return data.getAnimation();
    }
}
