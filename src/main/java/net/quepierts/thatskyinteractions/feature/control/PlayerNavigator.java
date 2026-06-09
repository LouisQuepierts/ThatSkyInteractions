package net.quepierts.thatskyinteractions.feature.control;

import lombok.Getter;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.feature.animation.tween.PhysicalTweenAttachment;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import net.quepierts.thatskyinteractions.feature.utils.TsiInterpolators;
import net.quepierts.thatskyinteractions.feature.utils.PlayerUtils;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import net.quepierts.veynir.core.fsm.FSMParameter;
import net.quepierts.veynir.core.fsm.FSMState;
import net.quepierts.veynir.core.fsm.FiniteStateMachine;
import org.jspecify.annotations.NonNull;

// just for simply move play to a walkTarget
@SuppressWarnings("DataFlowIssue")
public final class PlayerNavigator {

    public static final int                 TIMEOUT = 1000;
    public static final double              RANGE   = 64.0;
    public static final double              RANGE2  = RANGE * RANGE;

    private static final FiniteStateMachine FSM;
    private static final FSMParameter       PARAMETER;

    private static final int    STATE_TURNING       = 0;
    private static final int    STATE_NAVIGATING    = 1;
    private static final int    STATE_FINISHING     = 2;

    private final FSMState      state               = new FSMState();

    private final Player        player;

    @Getter
    private boolean             navigating;

    private float               timeout;
    private Vec3                walkTarget;
    private Vec3                lookTarget;

    private TweenHandle         lookHandle;
    private float               rotTarget;

    public static PlayerNavigator get(@NonNull final Player player) {
        return player.getData(AttachmentTypes.PLAYER_NAVIGATOR);
    }

    public PlayerNavigator(final Player player) {
        this.player             = player;
    }

    public void to(
            @NonNull final Player target,
            final float forward,
            final float left
    ) {

        final var self = this.player;
        if (!PlayerUtils.isRiding(self) && !PlayerUtils.inSameDimension(self, target)) {
            return;
        }

        final var position = PlayerUtils.getRelativePositionWorldSpace(target, forward, left);
        final var lookTarget = EntityAnchorArgument.Anchor.EYES.apply(target);
        this.to(position, lookTarget);

    }

    public void to(
            @NonNull Vec3 walkTarget,
            @NonNull Vec3 lookTarget
    ) {

        if (this.player.distanceToSqr(walkTarget) > RANGE2) {
            return;
        }

        FSM.start(this.state);
        this.state.setUniform(PARAMETER);
        this.navigating = true;

        this.timeout    = TIMEOUT;
        this.walkTarget = walkTarget;
        this.lookTarget = lookTarget;

    }

    public void exit() {
        if (this.state.getCurrentState() == STATE_NAVIGATING) {
            this.lookAt(this.lookTarget.x(), this.lookTarget.z());
            FSM.event(this.state, STATE_FINISHING);
        }
    }

    private void cleanup() {
        FSM.reset(this.state);
        this.navigating = false;
        this.rotTarget  = 0.0f;

        /*if (this.lookHandle != null) {
            this.lookHandle.cancel();
            this.lookHandle = null;
        }*/
    }

    public void tick() {

        if (!this.navigating) {
            return;
        }

        FSM.update(this.state, 0.05f);

        if (this.state.isFinished()) {
            this.cleanup();
            return;
        }

        switch (this.state.getCurrentState()) {
            case STATE_TURNING: {

                this.turning();

                break;
            }
            case STATE_NAVIGATING: {

                this.navigate();

                break;
            }
            case STATE_FINISHING: {

                break;
            }
        }

    }

    private void navigate() {

        if (this.timeout -- < 0) {
            this.exit();
            return;
        }

        final var player    = this.player;
        if (player.distanceToSqr(this.walkTarget) < 1e-3) {
            player.setPos(this.walkTarget);
            this.exit();
            return;
        }

        final var position  = this.player.position();
        final var speed     = player.getAttribute(Attributes.MOVEMENT_SPEED).getValue();

        final var subtract  = new Vec3(
                this.walkTarget.x() - position.x(),
                0.0,
                this.walkTarget.z() - position.z()
        );

        final var threshold = 2.0;
        final var l         = subtract.length();
        final var scale     = (l < 1e-4) ? 0.0 : speed * Math.min(1.0, l / threshold) / l;
        final var move      = subtract.scale(scale);

        final var delta     = l < move.length() ? subtract : move;
        final var stand     = position.add(delta);

        if (!PlayerUtils.canPositionStand(stand, player.level(), 0.5f)) {
            player.setDeltaMovement(0.0, 0.0, 0.0);
            this.exit();
            return;
        }

        player.addDeltaMovement(delta);
        this.lookAt(this.lookTarget.x(), this.lookTarget.z());

    }

    private void turning() {

        this.lookAt(this.walkTarget.x(), this.walkTarget.z());
        if (this.lookHandle == null
                || this.lookHandle.isFinished()
                || this.lookHandle.isCancelled()) {
            FSM.event(this.state, STATE_NAVIGATING);
        }

    }

    private void lookAt(double x, double z) {

        final var player    = this.player;

        final var xd        = x - player.getX();
        final var zd        = z - player.getZ();

        final var yRot      = (float)(Mth.atan2(zd, xd) * Mth.RAD_TO_DEG - 90.0);

        if (this.lookHandle != null && !this.lookHandle.isFinished()) {
            if (Mth.abs(Mth.wrapDegrees(yRot - this.rotTarget)) < 1.0) {
                return;
            }
            this.lookHandle.cancel();
        }

        final var diff      = Mth.wrapDegrees(yRot - player.getYHeadRot());
        final var abs       = Math.abs(diff);

        if (abs < 1.0) {
            player.setYHeadRot(yRot);
            return;
        }

        final var client    = player.isLocalPlayer();
        final var target    = player.getYHeadRot() + diff;
        this.rotTarget      = target;
        this.lookHandle = (client ? Tween.GLOBAL : PhysicalTweenAttachment.getAttachment(player.level()).tween()).to(
                (f) -> {
                    player.setYRot(f);
                    player.setYHeadRot(f);
                },
                player.getYHeadRot(),
                target,
                Math.max(abs * 0.01f, 0.6f),
                TsiInterpolators.DEGREE,
                Eases.QUAD_OUT
        );

    }

    static {
        FSM         = FiniteStateMachine.compiler()

                    .withState("turning")
                    .withState("navigating")
                    .withState("finishing")

                    .compile();
        PARAMETER   = FSM.uniform();
        PARAMETER.duration()[0] = 1.0f;
        PARAMETER.duration()[1] = 0.25f;

    }

}
