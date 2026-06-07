package net.quepierts.thatskyinteractions.feature.client.gui.layer;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingControl;
import net.quepierts.thatskyinteractions.feature.client.gui.component.floating.FloatingControlConstructor;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.floating.FloatingButtonNode;
import net.quepierts.thatskyinteractions.feature.gui.FloatingControlHandle;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class FloatingControlLayer implements GuiLayer {

    public static final FloatingControlLayer        INSTANCE    = new FloatingControlLayer();
    public static final Identifier                  IDENTIFIER  = ThatSkyInteractions.location("floating");

    private final Long2ObjectMap<FloatingControl>   controls    = new Long2ObjectOpenHashMap<>();
    private final ConcurrentLinkedDeque<Runnable>   pending     = new ConcurrentLinkedDeque<>();
    private final AtomicInteger                     nextId      = new AtomicInteger(0);

    private final Matrix4f projection                           = new Matrix4f();
    private final Vector3f position                             = new Vector3f();

    private final TweenScope tween                              = TweenScope.create();
    private final TweenTickHandler tickHandler                  = (TweenTickHandler) this.tween;

    private float counter;

    private FloatingControlLayer() {

        // test
        this.add(FloatingButton.fixed(
                Component.empty(),
                new Vector3f(0.0f, 64.0f, 0.0f),
                () -> {}
        ).withVisualNode(new FloatingButtonNode()));


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

    void render(
            @NonNull final GuiGraphicsExtractor graphics,
            @NonNull final DeltaTracker         tracker,
            final int                           mouseX,
            final int                           mouseY
    ) {

        final var delta = tracker.getRealtimeDeltaTicks();
        this.counter += delta;
        this.tickHandler.tick(delta * 0.05f);

        if (this.counter > 1.0f) {
            this.update();
            this.counter %= 1.0f;
        }

        if (this.controls.isEmpty()) {
            return;
        }

        for (final var control : this.controls.values()) {
            control.extractRenderState(graphics, mouseX, mouseY, delta);
        }

    }

    private void update() {

        Runnable op;
        while ((op = this.pending.poll()) != null) {
            op.run();
        }

        if (this.controls.isEmpty()) {
            return;
        }

        final var minecraft     = Minecraft.getInstance();
        final var renderer      = minecraft.gameRenderer;
        final var camera        = renderer.getMainCamera();
        final var cameraPos     = camera.position().toVector3f();

        final int screenWidth   = minecraft.getWindow().getGuiScaledWidth();
        final int screenHeight  = minecraft.getWindow().getGuiScaledHeight();

        final var projection    = camera.getViewRotationProjectionMatrix(this.projection);

        for (final var control : this.controls.values()) {
            final var position  = control.getWorldPosition(this.position).sub(cameraPos);
            final var vs        = new Vector4f(position, 1.0f).mul(projection);

            if (vs.w <= 0.0f) {
                vs.y = screenHeight;
                vs.x = -vs.x;
            }

            final var x         = (vs.x() / vs.z() * 0.5F + 0.5F) * screenWidth;
            final var y         = (1.0F - (vs.y() / vs.z() * 0.5F + 0.5F)) * screenHeight;

            if (control.isRestrictPosition()) {
                control.toPosition(
                        Mth.clamp(x, 0, screenWidth),
                        Mth.clamp(y, 0, screenHeight)
                );
            } else {
                control.toPosition(x, y);
            }
        }

    }
}
