package net.quepierts.thatskyinteractions.client.gui.layer;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.layer.interact.World2ScreenButton;
import org.joml.*;

import java.util.Arrays;
import java.util.Set;

public class World2ScreenGridLayer implements LayeredDraw.Layer {
    public static final World2ScreenGridLayer INSTANCE = new World2ScreenGridLayer();
    public static final ResourceLocation LOCATION = ThatSkyInteractions.getLocation("world_screen_grid");

    private final Minecraft minecraft = Minecraft.getInstance();
    private final Set<World2ScreenButton> objects = new ObjectOpenHashSet<>();
    private final World2ScreenButton[] grid = new World2ScreenButton[64 * 64];
    World2ScreenGridLayer() {
        objects.add(new Position(new Vector3f(0, 70, 0)));
        objects.add(new Position(new Vector3f(20, 96, 0)));
        objects.add(new Position(new Vector3f(0, 64, 30)));
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        float screenWidth = this.minecraft.getWindow().getGuiScaledWidth();
        float screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
        float deltaTicks = deltaTracker.getGameTimeDeltaTicks();

        Arrays.fill(grid, null);
        for (World2ScreenButton object : objects) {
            float d0 = Mth.lerp(deltaTicks, object.xO, Mth.clamp(object.x, 0, screenWidth));
            float d1 = Mth.lerp(deltaTicks, object.yO, Mth.clamp(object.y, 0, screenHeight));


            object.xO = d0;
            object.yO = d1;
            object.render(guiGraphics, deltaTicks);
        }
    }

    public void tick() {
        if (Minecraft.getInstance().level == null)
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

        Arrays.fill(grid, null);
        for (World2ScreenButton object : objects) {
            Vector4f cameraSpacePos = new Vector4f(object.getWorldPos(), 1.0f)
                    .mul(mat);

            if (cameraSpacePos.w < 0.0f) {
                cameraSpacePos.y = screenHeight;
                cameraSpacePos.x = -cameraSpacePos.x;
            }

            object.x = Mth.clamp((int) ((cameraSpacePos.x() / cameraSpacePos.z() * 0.5F + 0.5F) * screenWidth), 16, screenWidth - 16);
            object.y = Mth.clamp((int) ((1.0F - (cameraSpacePos.y() / cameraSpacePos.z() * 0.5F + 0.5F)) * screenHeight), 16, screenHeight - 16);

            tryMove(getGridPosition(object.x, object.y), object);
            apply(getGridPosition(object.x, object.y), object);
        }
    }

    private void tryMove(Vector4i gridPos, World2ScreenButton button) {
        for (int x = gridPos.x; x < gridPos.y; x ++) {
            for (int y = gridPos.z; y < gridPos.w; y++) {
                final World2ScreenButton btn = get(x, y);

                if (btn == null)
                    continue;
                button.moveIfOverlapped(btn);
            }
        }
    }

    private void apply(Vector4i gridPos, World2ScreenButton button) {
        for (int x = gridPos.x; x < gridPos.y; x ++) {
            for (int y = gridPos.z; y < gridPos.w; y++) {
                set(x, y, button);
            }
        }
    }

    private Vector4i getGridPosition(float x, float y) {
        return new Vector4i(
                (int) ((x - 16) / 32),
                (int) ((x + 15) / 32),
                (int) ((y - 16) / 32),
                (int) ((y + 15) / 32)
        );
    }

    private World2ScreenButton get(int x, int y) {
        if (x < 0 || y < 0 || x > 64 || y > 64)
            return null;
        return grid[x + y * 64];
    }

    private void set(int x, int y, World2ScreenButton button) {
        if (x < 0 || y < 0 || x > 64 || y > 64)
            return;
        grid[x + y * 64] = button;
    }

    public void addWorldPositionObject() {

    }

    private static class Position extends World2ScreenButton {
        private final Vector3f pos;
        public Position(Vector3f pos) {
            super(ThatSkyInteractions.getLocation("textures/icon/none.png"));
            this.pos = pos;
        }

        @Override
        public void invoke() {

        }

        @Override
        public Vector3f getWorldPos() {
            return pos;
        }
    }
}
