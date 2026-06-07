package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.property.BooleanProperty;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingControl;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingControlConstructor;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.floating.FloatingButtonNode;
import net.quepierts.thatskyinteractions.feature.gui.FloatingControlHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public final class FloatingControlLayer implements GuiLayer {

    public static final FloatingControlLayer        INSTANCE    = new FloatingControlLayer();
    public static final Identifier                  IDENTIFIER  = ThatSkyInteractions.location("floating");

    private final Long2ObjectMap<FloatingControl>   controls    = new Long2ObjectOpenHashMap<>();
    private final ConcurrentLinkedDeque<Runnable>   pending     = new ConcurrentLinkedDeque<>();
    private final AtomicInteger                     nextId      = new AtomicInteger(0);

    private final Matrix4f                          projection  = new Matrix4f();
    private final Vector3f                          position    = new Vector3f();

    private final TweenScope                        tween       = TweenScope.create();
    private final TweenTickHandler                  tickHandler = (TweenTickHandler) this.tween;

    private long                                    selected    = -1;

    private FloatingControlLayer() {

        // test
        this.add(FloatingButton.fixed(
                Component.empty(),
                new Vector3f(0.0f, 64.0f, 0.0f),
                _ -> {}
        ).withVisualNode(FloatingButtonNode.texture(
                ThatSkyInteractions.location("textures/gui/ignite.png")
        )));


    }

    public FloatingControlHandle add(
            @NonNull FloatingControlConstructor constructor
    ) {

        final var id        = this.nextId.getAndIncrement();
        final var handle    = new FloatingControlHandle(id);

        final var control   = constructor.apply(handle, this.tween);
        this.pending.offer(() -> this.controls.put(control.getHandle().id(), control));

        return handle;
    }

    public void remove(
            @NonNull FloatingControlHandle handle
    ) {
        this.pending.offer(() -> {
            final var removed = this.controls.remove(handle.id());
            if (removed != null) {
                removed.onRemoved();
            }
        });
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

    public void interact() {
        if (this.selected != -1) {
            final var control = this.controls.get(this.selected);
            if (control != null) {
                control.onInteract();
            }
        }
    }

    void render(
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final DeltaTracker         tracker,
            final int                           mouseX,
            final int                           mouseY
    ) {

        final var delta = tracker.getRealtimeDeltaTicks();
        this.tickHandler.tick(delta * 0.05f);

        this.update(mouseX, mouseY);

        if (this.controls.isEmpty()) {
            return;
        }

        for (final var control : this.controls.values()) {
            control.extractRenderState(graphics, mouseX, mouseY, delta);
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


        this.selected           = -1;
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

        float nearest           = Float.MAX_VALUE;

        for (final var control : this.controls.values()) {
            control             .setFocused(false);

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

            if (useMouse) {
                if (this.selected != -1) {
                    continue;
                }

                final var d = control.distanceTo(mouseX, mouseY);

                if (d > 0) {
                    continue;
                }

                if (d < nearest) {
                    nearest = d;
                    this.selected = control.getHandle().id();
                }
            } else {
                if (Mth.abs(screenX - centerX) > (screenWidth >> 3)) {
                    continue;
                }

                final var d = Vector2f.distance(screenX, screenY, centerX, centerY);
                if (d < nearest) {
                    nearest = d;
                    this.selected = control.getHandle().id();
                }
            }
        }

        if (this.selected != -1) {
            final var control   = this.controls.get(this.selected);
            control             .setFocused(true);
        }

    }
}
