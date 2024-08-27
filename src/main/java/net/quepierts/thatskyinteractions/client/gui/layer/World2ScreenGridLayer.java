package net.quepierts.thatskyinteractions.client.gui.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.World2ScreenWidget;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class World2ScreenGridLayer implements LayeredDraw.Layer {
    public static final World2ScreenGridLayer INSTANCE = new World2ScreenGridLayer();
    public static final ResourceLocation LOCATION = ThatSkyInteractions.getLocation("world_screen_grid");
    protected static final int FADE_BEGIN_DISTANCE = 32 * 32;
    protected static final int FADE_DISTANCE = 8;

    private final Minecraft minecraft = Minecraft.getInstance();
    private final Map<UUID, World2ScreenWidget> objects = new Object2ObjectOpenHashMap<>();
    private final List<World2ScreenWidget> inRange = new ObjectArrayList<>();
    //private final World2ScreenButton[] grid = new World2ScreenButton[64 * 64];
    private final FloatHolder click = new FloatHolder(0.0f);
    private final LerpNumberAnimation animation = new LerpNumberAnimation(this.click, AnimateUtils.Lerp::smooth, 0, 1, 0.5f);

    private World2ScreenWidget highlight;
    private World2ScreenWidget locked;
    private double scroll = 0;
    World2ScreenGridLayer() {
        reset();
    }

    public void reset() {
        objects.clear();
        inRange.clear();
        highlight = null;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        //RenderUtils.drawHalo(guiGraphics, 10, 10, 200);

        update();

        float deltaTicks = deltaTracker.getGameTimeDeltaTicks();

        //Arrays.fill(grid, null);
        for (Iterator<World2ScreenWidget> iterator = objects.values().iterator(); iterator.hasNext(); ) {
            World2ScreenWidget object = iterator.next();
            if (!object.isComputed())
                continue;

            boolean highlight1 = locked != null ? object == locked : object == highlight;
            if (object.shouldRemove() && (!highlight1 || !this.animation.isRunning())) {
                if (object == locked) {
                    locked = null;
                }
                iterator.remove();
                continue;
            }
            float d0 = Mth.lerp(deltaTicks, object.xO, object.x);
            float d1 = Mth.lerp(deltaTicks, object.yO, object.y);

            object.xO = d0;
            object.yO = d1;

            if (highlight1) {
                object.render(guiGraphics, true, this.click.getValue());
            } else {
                object.render(guiGraphics, false, 0);
            }
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    public void update() {
        if (this.minecraft.level == null)
            return;

        final Camera camera = this.minecraft.gameRenderer.getMainCamera();
        final Vec3 cameraPos = camera.getPosition();

        final Matrix4f projectionMatrix = this.minecraft.gameRenderer.getProjectionMatrix(this.minecraft.options.fov().get());

        final Matrix4f viewMatrix = new Matrix4f()
                .rotation(camera.rotation().conjugate(new Quaternionf()))
                .translate((float) -cameraPos.x, (float) -cameraPos.y, (float) -cameraPos.z);

        final Matrix4f mat = new Matrix4f().mul(projectionMatrix).mul(viewMatrix);
        final int screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        final int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();

        final float half = screenWidth / 2f;
        final float left = half - 32;
        final float right = half + 32;

        inRange.clear();
        //Arrays.fill(grid, null);

        final Vector3f pos = new Vector3f();
        for (World2ScreenWidget object : objects.values()) {
            object.setComputed();
            object.getWorldPos(pos);

            Vector4f cameraSpacePos = new Vector4f(pos, 1.0f)
                    .mul(mat);

            if (cameraSpacePos.w < 0.0f) {
                cameraSpacePos.y = screenHeight;
                cameraSpacePos.x = -cameraSpacePos.x;
            }

            object.x = Mth.clamp((int) ((cameraSpacePos.x() / cameraSpacePos.z() * 0.5F + 0.5F) * screenWidth), 16, screenWidth - 16);
            object.y = Mth.clamp((int) ((1.0F - (cameraSpacePos.y() / cameraSpacePos.z() * 0.5F + 0.5F)) * screenHeight), 16, screenHeight - 16);

            //tryMove(getGridPosition(object.x, object.y), object);
            //apply(getGridPosition(object.x, object.y), object);

            float distance = Vector3f.distanceSquared(pos.x, pos.y, pos.z, (float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
            object.fade = (float) AnimateUtils.Lerp.smooth(0, 1, 1.0f - Math.max(distance - FADE_BEGIN_DISTANCE, 0) / FADE_DISTANCE);
            if (distance > FADE_DISTANCE + FADE_BEGIN_DISTANCE)
                continue;

            if (locked == null && object.x > left && object.x < right && object.selectable) {
                inRange.add(object);
            }
        }

        if (!this.animation.isRunning()) {
            if (!inRange.isEmpty()) {
                this.highlight = inRange.get((int) scroll % inRange.size());
            } else {
                this.highlight = null;
            }
        }
    }

//    private void tryMove(Vector4i gridPos, World2ScreenButton button) {
//        for (int x = gridPos.x; x < gridPos.y; x ++) {
//            for (int y = gridPos.z; y < gridPos.w; y++) {
//                final World2ScreenButton btn = get(x, y);
//
//                if (btn == null)
//                    continue;
//                button.moveIfOverlapped(btn);
//            }
//        }
//    }
//
//    private void apply(Vector4i gridPos, World2ScreenButton button) {
//        for (int x = gridPos.x; x < gridPos.y; x ++) {
//            for (int y = gridPos.z; y < gridPos.w; y++) {
//                set(x, y, button);
//            }
//        }
//    }
//
//    private Vector4i getGridPosition(float x, float y) {
//        return new Vector4i(
//                (int) ((x - 16) / 32),
//                (int) ((x + 15) / 32),
//                (int) ((y - 16) / 32),
//                (int) ((y + 15) / 32)
//        );
//    }
//
//    private World2ScreenButton get(int x, int y) {
//        if (x < 0 || y < 0 || x > 64 || y > 64)
//            return null;
//        return grid[x + y * 64];
//    }
//
//    private void set(int x, int y, World2ScreenButton button) {
//        if (x < 0 || y < 0 || x > 64 || y > 64)
//            return;
//        grid[x + y * 64] = button;
//    }

    public void addWorldPositionObject(UUID uuid, World2ScreenWidget button) {
        if (ThatSkyInteractions.getInstance().getClient().blocked(uuid))
            return;
        this.objects.put(uuid, button);
    }

    public void remove(UUID other) {
        this.objects.remove(other);
    }

    public void scroll(double mouseY) {
        if (this.inRange.isEmpty()) {
            this.scroll = 0;
        } else {
            this.scroll = (this.scroll + mouseY) % this.inRange.size();
        }
    }

    public void click() {
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        if (this.locked != null) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            (ThatSkyInteractions.RANDOM.nextFloat() - ThatSkyInteractions.RANDOM.nextFloat()) * 0.35F + 0.9F
                    )
            );
            ScreenAnimator.GLOBAL.play(this.animation);
            this.locked.invoke();
        } else if (this.highlight != null) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(
                            SoundEvents.EXPERIENCE_ORB_PICKUP,
                            (ThatSkyInteractions.RANDOM.nextFloat() - ThatSkyInteractions.RANDOM.nextFloat()) * 0.35F + 0.9F
                    )
            );
            ScreenAnimator.GLOBAL.play(this.animation);
            this.highlight.invoke();
        }
    }

    public void lock(World2ScreenWidget locked) {
        this.locked = locked;
    }
}
