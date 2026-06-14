package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingControl;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingControlConstructor;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingTarget;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class FloatingControlLayer implements GuiLayer {

    public static final FloatingControlLayer                INSTANCE    = new FloatingControlLayer();
    public static final Identifier                          IDENTIFIER  = ThatSkyInteractions.location("floating");

    private final Map<FloatingTarget, FloatingControl>      controls    = new HashMap<>();
    private final ConcurrentLinkedDeque<Runnable>           pending     = new ConcurrentLinkedDeque<>();
    private final List<FloatingTarget>                      removing    = new ArrayList<>();

    private final Matrix4f                                  projection  = new Matrix4f();
    private final Vector3f                                  position    = new Vector3f();

    private final TweenScope                                tween       = TweenScope.create();
    private final TweenTickHandler                          tickHandler = (TweenTickHandler) this.tween;

    private FloatingTarget                                  selected    = null;

    private FloatingControlLayer() {}

    public void remove(
            final @NonNull FloatingTarget target
    ) {
        this.pending.offer(() -> {
            final var removed = this.controls.remove(target);
            if (removed != null) {
                removed.markRemoved();
            }
        });
    }

    public void remove(
            final @NonNull UUID uuid
    ) {
        this.remove(new FloatingTarget.Entity(uuid));
    }

    public void add(
            @NonNull FloatingTarget             target,
            @NonNull FloatingControlConstructor constructor
    ) {
        final var control = constructor.apply(target, this.tween);
        this.pending.offer(() -> this.controls.put(target, control));
    }

    @Override
    public void render(
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final DeltaTracker         tracker
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

        this.render(
                graphics,
                tracker,
                mouseX,
                mouseY
        );

    }

    public boolean interact() {
        if (this.selected != null) {
            final var control = this.controls.get(this.selected);
            if (control != null) {
                control.onInteract();
            }
            return true;
        }
        return false;
    }

    void render(
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final DeltaTracker         tracker,
            final int                           mouseX,
            final int                           mouseY
    ) {

        final var delta     = tracker.getRealtimeDeltaTicks();
        this.tickHandler.tick(delta * 0.05f);

        this.update(mouseX, mouseY);

        if (this.controls.isEmpty()) {
            return;
        }

        final var colors    = new ColorStack();

        for (final var control : this.controls.values()) {
            control.extractRenderState(graphics, colors, mouseX, mouseY, delta);
        }

    }

    private void update(
            final int                           mouseX,
            final int                           mouseY
    ) {

        Runnable op;
        while ((op = this.pending.poll()) != null) {
            op.run();
        }


        this.selected           = null;
        if (this.controls.isEmpty()) {
            return;
        }

        final var minecraft     = Minecraft.getInstance();
        final var renderer      = minecraft.gameRenderer;
        final var camera        = renderer.getMainCamera();
        final var cameraPos     = camera.position().toVector3f();

        final int screenWidth   = minecraft.getWindow().getGuiScaledWidth();
        final int screenHeight  = minecraft.getWindow().getGuiScaledHeight();

        final int centerX       = screenWidth / 2;
        final int centerY       = screenHeight / 2;

        final var projection    = camera.getViewRotationProjectionMatrix(this.projection);
        final var useMouse      = !minecraft.mouseHandler.isMouseGrabbed();

        float cx, cy;
        if (useMouse) {
            cx                  = mouseX;
            cy                  = mouseY;
        } else {
            cx                  = centerX;
            cy                  = centerY;
        }

        float nearest           = Float.MAX_VALUE;

        for (final var control : this.controls.values()) {
            control             .setFocused(false);

            if (control.isRemoved()) {
                this.removing.add(control.getTarget());
                continue;
            }

            final var position  = control.getWorldPosition(this.position).sub(cameraPos);
            final var distance  = position.length();

            control             .active = distance < control.getShowDistance().get();

            final var vs        = new Vector4f(position, 1.0f).mul(projection);

            if (vs.w <= 0.0f) {
                vs.y = screenHeight;
                vs.x = -vs.x;
            }

            final var x         = (vs.x() / vs.z() * 0.5F + 0.5F) * screenWidth;
            final var y         = (1.0F - (vs.y() / vs.z() * 0.5F + 0.5F)) * screenHeight;

            float screenX, screenY;

            if (control.isRestrictPosition()) {
                final var hw    = control.getWidth() / 2;
                final var hh    = control.getHeight() / 2;
                screenX         = Mth.clamp(x, hw, screenWidth - hw);
                screenY         = Mth.clamp(y, hh, screenHeight - hh);
            } else {
                screenX         = x;
                screenY         = y;
            }

            control             .updatePosition(screenX, screenY);

            if (!control.active) {
                continue;
            }

            if (this.selected != null) {
                continue;
            }

            final var d = control.distanceTo(cx, cy);

            if (d > 0) {
                continue;
            }

            if (d < nearest) {
                nearest = d;
                this.selected = control.getTarget();
            }

            /*if (useMouse) {
                if (this.selected != null) {
                    continue;
                }

                final var d = control.distanceTo(mouseX, mouseY);

                if (d > 0) {
                    continue;
                }

                if (d < nearest) {
                    nearest = d;
                    this.selected = control.getTarget();
                }
            } else {
                if (Mth.abs(screenX - centerX) > (screenWidth >> 3)) {
                    continue;
                }

                final var d = Vector2f.distance(screenX, screenY, centerX, centerY);
                if (d < nearest) {
                    nearest = d;
                    this.selected = control.getTarget();
                }
            }*/
        }

        if (this.selected != null) {
            final var control   = this.controls.get(this.selected);
            control             .setFocused(true);
        }

        for (final var target : this.removing) {
            final var removed = this.controls.remove(target);
            if (removed != null) {
                removed.onRemoved();
            }
        }

        this.removing.clear();

    }

    public void reset() {
        this.tween      .clear();
        this.controls   .clear();
        this.pending    .clear();
        this.selected   = null;
    }
}
