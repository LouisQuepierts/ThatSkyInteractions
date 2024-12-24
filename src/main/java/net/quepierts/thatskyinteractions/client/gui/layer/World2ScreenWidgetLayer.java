package net.quepierts.thatskyinteractions.client.gui.layer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.ClientHelper;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.mixin.accessor.GameRendererAccessor;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class World2ScreenWidgetLayer implements LayeredDraw.Layer {
    public static final World2ScreenWidgetLayer INSTANCE = new World2ScreenWidgetLayer();
    public static final ResourceLocation LOCATION = ThatSkyInteractions.getLocation("world_screen_grid");
    public static final int FADE_BEGIN_DISTANCE = 32 * 32;
    public static final int FADE_DISTANCE = 8;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final Map<UUID, World2ScreenWidget> objects = Maps.newConcurrentMap();
    private final Set<UUID> toRemove = Sets.newConcurrentHashSet();
    private final List<World2ScreenWidget> inRange = new ObjectArrayList<>();
    //private final World2ScreenButton[] grid = new World2ScreenButton[64 * 64];
    private final FloatHolder click = new FloatHolder(0.0f);
    private final LerpNumberAnimation animation = new LerpNumberAnimation(this.click, AnimateUtils.Lerp::smooth, 0, 1, 0.5f);

    private World2ScreenWidget highlight;
    private World2ScreenWidget nearest;
    private World2ScreenWidget locked;
    private double scroll = 0;
    private int prompt = 0;
    private int lastFrameCount = 0;

    World2ScreenWidgetLayer() {
        reset();
    }

    public void reset() {
        objects.clear();
        inRange.clear();
        highlight = null;
        locked = null;
        nearest = null;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        float deltaTicks = deltaTracker.getGameTimeDeltaTicks();
        update(deltaTicks);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0, 0, 100);

        //Arrays.fill(grid, null);
        this.lastFrameCount = 0;
        for (Map.Entry<UUID, World2ScreenWidget> entry : objects.entrySet()) {
            World2ScreenWidget object = entry.getValue();
            if (!object.isComputed())
                continue;

            if (!object.shouldRender())
                continue;

            boolean highlight1 = locked != null ? object == locked : object == nearest;
            boolean shouldRemove = object.shouldRemove();
            if (shouldRemove && (!highlight1 || !this.animation.isRunning())) {
                if (object == locked) {
                    locked = null;
                }
                this.toRemove.add(entry.getKey());
                continue;
            }
            float d0 = object.x;
            float d1 = object.y;

            if (object.shouldSmoothPosition()) {
                d0 = Mth.lerp(deltaTicks, object.xO, object.x);
                d1 = Mth.lerp(deltaTicks, object.yO, object.y);
            }

            object.xO = d0;
            object.yO = d1;

            if (highlight1) {
                object.render(guiGraphics, true, this.click.getValue(), deltaTicks);
            } else {
                object.render(guiGraphics, false, 0, deltaTicks);
            }
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();

        pose.popPose();
    }

    public void update(float deltaTicks) {
        if (this.minecraft.level == null)
            return;

        if (!this.toRemove.isEmpty()) {
            this.toRemove.forEach(this.objects::remove);
            this.toRemove.clear();
        }

        if (this.objects.isEmpty()) {
            this.highlight = null;
            this.locked = null;
            this.inRange.clear();
            return;
        }

        final GameRenderer gameRenderer = this.minecraft.gameRenderer;
        final Camera camera = gameRenderer.getMainCamera();
        final Vec3 cameraPos = camera.getPosition();

        final double fov = ((GameRendererAccessor) gameRenderer).tsi$getFov(camera, deltaTicks, true);
        final Matrix4f projectionMatrix = gameRenderer.getProjectionMatrix(fov);

        final Matrix4f viewMatrix = new Matrix4f()
                .rotation(camera.rotation().conjugate(new Quaternionf()))
                .translate((float) -cameraPos.x, (float) -cameraPos.y, (float) -cameraPos.z);

        final Matrix4f mat = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
        final int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        final int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
        final Vector2f center = this.getCenter();

        float minDistance = Float.MAX_VALUE;
        this.inRange.clear();
        //Arrays.fill(grid, null);

        final Vector3f pos = new Vector3f();
        this.highlight = null;
        this.nearest = null;
        for (World2ScreenWidget object : objects.values()) {
            if (object.shouldSkip())
                continue;

            object.setComputed();
            object.getWorldPos(pos);

            Vector4f cameraSpacePos = new Vector4f(pos, 1.0f)
                    .mul(mat);

            if (cameraSpacePos.w <= 0.0f) {
                cameraSpacePos.y = screenHeight;
                cameraSpacePos.x = -cameraSpacePos.x;
            }

            float x = (int) ((cameraSpacePos.x() / cameraSpacePos.z() * 0.5F + 0.5F) * screenWidth);
            float y = (int) ((1.0F - (cameraSpacePos.y() / cameraSpacePos.z() * 0.5F + 0.5F)) * screenHeight);

            if (object.limitInScreen()) {
                x = Mth.clamp(x, 16, screenWidth - 16);
                y = Mth.clamp(y, 16, screenHeight - 16);
            }

            object.setInScreen(
                    x > -object.scale
                            && y > -object.scale
                            && x < screenWidth + object.scale
                            && y < screenHeight + object.scale
            );

            object.setScreenPos(x, y);

            //tryMove(getGridPosition(object.x, object.y), object);
            //apply(getGridPosition(object.x, object.y), object);

            float distance = Vector3f.distanceSquared(pos.x, pos.y, pos.z, (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
            object.calculateRenderScale(distance);
            if (object.scale < 1.0f)
                continue;

            float distanceSquared = center.distanceSquared(object.x, object.y);
            if (distanceSquared < 64 * 64 && object.selectable && !this.animation.isRunning()) {
                if (distanceSquared < minDistance) {
                    minDistance = distanceSquared;
                    this.nearest = object;

                    if (distanceSquared < 16 * 16) {
                        this.highlight = object;
                    }
                }
            }
        }

        if (this.highlight != null) {
            this.prompt++;

            if (this.prompt > 40) {
                PromptMessageLayer.INSTANCE.setOrContinue(
                        () -> this.highlight.getPrompt(this.minecraft.screen != null),
                        this.highlight.getPromptType() + "$" + (this.minecraft.screen != null),
                        60
                );
            }
        } else {
            this.prompt = 0;
        }
    }

    private Vector2f getCenter() {
        if (this.minecraft.screen == null) {
            return new Vector2f(
                    this.minecraft.getWindow().getGuiScaledWidth() / 2f,
                    this.minecraft.getWindow().getGuiScaledHeight() / 2f
            );
        } else if (!this.minecraft.screen.isPauseScreen()) {
            return new Vector2f((float) (
                    minecraft.mouseHandler.xpos()
                            * (double)minecraft.getWindow().getGuiScaledWidth()
                            / (double)minecraft.getWindow().getScreenWidth()),
                    (float)(
                    minecraft.mouseHandler.ypos()
                            * (double)minecraft.getWindow().getGuiScaledHeight()
                            / (double)minecraft.getWindow().getScreenHeight())
            );
        }

        return new Vector2f(-128);
    }

    private void updateSingle(World2ScreenWidget object) {
        final GameRenderer gameRenderer = this.minecraft.gameRenderer;
        final Camera camera = gameRenderer.getMainCamera();
        final Vec3 cameraPos = camera.getPosition();

        final double fov = ((GameRendererAccessor) gameRenderer).tsi$getFov(camera, 0.0f, true);
        final Matrix4f projectionMatrix = gameRenderer.getProjectionMatrix(fov);

        final Matrix4f viewMatrix = new Matrix4f()
                .rotation(camera.rotation().conjugate(new Quaternionf()))
                .translate((float) -cameraPos.x, (float) -cameraPos.y, (float) -cameraPos.z);

        final Matrix4f mat = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
        final int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        final int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

        final Vector3f pos = new Vector3f();
        object.setComputed();
        object.getWorldPos(pos);

        Vector4f cameraSpacePos = new Vector4f(pos, 1.0f)
                .mul(mat);

        if (cameraSpacePos.w < 0.0f) {
            cameraSpacePos.y = screenHeight;
            cameraSpacePos.x = -cameraSpacePos.x;
        }

        float x = (int) ((cameraSpacePos.x() / cameraSpacePos.z() * 0.5F + 0.5F) * screenWidth);
        float y = (int) ((1.0F - (cameraSpacePos.y() / cameraSpacePos.z() * 0.5F + 0.5F)) * screenHeight);

        if (object.limitInScreen()) {
            x = Mth.clamp(x, 16, screenWidth - 16);
            y = Mth.clamp(y, 16, screenHeight - 16);
        }

        object.setInScreen(
                x > 0 && y > 0 && x < screenWidth && y < screenHeight
        );

        object.setScreenPos(x, y);

        float distance = Vector3f.distanceSquared(pos.x, pos.y, pos.z, (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
        object.calculateRenderScale(distance);
    }

    public void addWorldPositionObject(UUID uuid, World2ScreenWidget widget) {
        if (widget == null) {
            this.toRemove.add(uuid);
            return;
        }
        if (ClientHelper.blocked(uuid))
            return;

        if (!this.objects.containsKey(uuid)) {
            this.updateSingle(widget);
        }
        this.objects.put(uuid, widget);
    }

    public void addWorldPositionObjectForced(UUID uuid, World2ScreenWidget widget) {
        if (!this.objects.containsKey(uuid)) {
            this.objects.put(uuid, widget);
        }

        this.objects.put(uuid, widget);
    }

    public void remove(UUID other) {
        this.toRemove.add(other);
    }

    public boolean scroll(double mouseY) {
        if (this.inRange.isEmpty()) {
            this.scroll = 0;
            return false;
        } else {
            this.scroll = (this.scroll + mouseY) % this.inRange.size();
            return true;
        }
    }

    public boolean click() {
        if (this.minecraft.level == null) {
            return false;
        }

        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return false;

        if (this.locked != null) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            (ThatSkyInteractions.RANDOM.nextFloat() - ThatSkyInteractions.RANDOM.nextFloat()) * 0.35F + 0.9F
                    )
            );
            ScreenAnimator.GLOBAL.play(this.animation);
            this.locked.invoke();
            return true;
        } else if (this.highlight != null) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            (ThatSkyInteractions.RANDOM.nextFloat() - ThatSkyInteractions.RANDOM.nextFloat()) * 0.35F + 0.9F
                    )
            );
            ScreenAnimator.GLOBAL.play(this.animation);
            this.highlight.invoke();
            return true;
        }

        return false;
    }

    public void lock(World2ScreenWidget locked) {
        this.locked = locked;
    }
}
