package net.quepierts.thatskyinteractions.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.component.astrolabe.FriendAstrolabeWidget;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.util.CameraHandler;
import net.quepierts.thatskyinteractions.data.astrolabe.Astrolabe;
import net.quepierts.thatskyinteractions.data.astrolabe.AstrolabeManager;
import net.quepierts.thatskyinteractions.data.astrolabe.AstrolabeMap;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class FriendAstrolabeScreen extends AnimatedScreen {
    private static final int MAX_ASTROLABE_AMOUNT = 16;
    private int index;
    private final CameraHandler cameraHandler;
    private final FriendAstrolabeWidget[] astrolabes = new FriendAstrolabeWidget[MAX_ASTROLABE_AMOUNT];
    private final FloatHolder closerHolder = new FloatHolder(0.7f);
    private final LerpNumberAnimation closeAnimation = new LerpNumberAnimation(closerHolder, AnimateUtils.Lerp::smooth, 0, 0, 1.0f);

    private int astrolabeAmount;
    private int astrolabeAmountHalf;

    public FriendAstrolabeScreen() {
        super(Component.empty());
        this.cameraHandler = ThatSkyInteractions.getInstance().getClient().getCameraHandler();
    }

    @Override
    protected void init() {
        ClientProxy client = ThatSkyInteractions.getInstance().getClient();
        AstrolabeManager astrolabeManager = client.getAstrolabeManager();
        AstrolabeMap astrolabes = client.getCache().getUserData().astrolabes();

        ObjectList<ResourceLocation> friendAstrolabes = astrolabeManager.getFriendAstrolabes();
        int i = 0;

        for (ResourceLocation location : friendAstrolabes) {
            Astrolabe astrolabe = astrolabeManager.get(location);
            if (astrolabe == null)
                continue;

            FriendAstrolabeInstance instance = astrolabes.get(location);
            if (instance == null)
                continue;

            this.astrolabes[i] = new FriendAstrolabeWidget(this, location, instance);
            this.astrolabes[i].init(astrolabe);

            if (++i == MAX_ASTROLABE_AMOUNT)
                break;
        }

        this.astrolabeAmount = i;
        this.astrolabeAmountHalf = i / 2;
        /*for (ResourceLocation location : friendAstrolabes) {
            Astrolabe astrolabe = astrolabeManager.get(location);
            if (astrolabe == null)
                continue;

            this.astrolabes[i] = new AstrolabeWidget(this, 0, 0, location);
            FriendAstrolabeInstance instance = astrolabes.computeIfAbsent(location, (l) -> new FriendAstrolabeInstance());
            this.astrolabes[i].reset(astrolabe, instance);

            if (++i == MAX_ASTROLABE_AMOUNT)
                break;
        }*/
    }

    @Override
    public void enter() {
        super.enter();
        CameraHandler.Entry rotation = this.cameraHandler.get(CameraHandler.Property.ROTATION);
        Vector3f unmodified = new Vector3f();
        Vector3f unmodifiedRotation = rotation.getUnmodified(unmodified);
        final float yRot = (this.index - this.astrolabeAmountHalf) * 45;
        if (this.minecraft.options.getCameraType().isMirrored()) {
            rotation.toTarget(new Vector3f(45 - unmodifiedRotation.x, yRot + 180, 0), 1.0f);
        } else {
            rotation.toTarget(new Vector3f(-45 - unmodifiedRotation.x, yRot, 0), 1.0f);
        }

        CameraHandler.Entry position = this.cameraHandler.get(CameraHandler.Property.POSITION);
        position.toTarget(new Vector3f(0, 2, 0), 0.5f, 0.2f);

        CameraHandler.Entry dayTime = this.cameraHandler.get(CameraHandler.Property.DAY_TIME);
        float unmodifiedDayTime = dayTime.getUnmodified(unmodified).x;
        dayTime.toTarget(new Vector3f(0.5f - unmodifiedDayTime));

        this.closeAnimation.reset(0.7f, 1.0f);
        this.animator.play(this.closeAnimation, 0.5f);

        this.astrolabes[this.index].enter();
    }

    @Override
    public void hide() {
        super.hide();
        this.closeAnimation.reset(1.0f, 0.5f);
        this.animator.play(this.closeAnimation);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.cameraHandler.get(CameraHandler.Property.ROTATION).toDefault(0.75f, 0.2f);
        this.cameraHandler.get(CameraHandler.Property.DAY_TIME).toDefault(0.5f, 0.2f);
        this.cameraHandler.get(CameraHandler.Property.POSITION).toDefault(0.75f, 0.4f);
    }

    @Override
    public void irender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        CameraHandler.Entry rotation = this.cameraHandler.get(CameraHandler.Property.ROTATION);
        Vector3f cameraRotation = rotation.getModified(new Vector3f());
        float destRotX = rotation.getDest(new Vector3f()).x;

        float xHalf = this.width / 2.0f;
        float yHalf = this.height / 2.0f;

        float alpha = Palette.getShaderAlpha();
        float enterValue = this.enter.getValue();
        float enterY = (1 - enterValue) * Mth.cos((destRotX - cameraRotation.x()) * Mth.DEG_TO_RAD) * 200f;

        int localMouseX = (int) (mouseX - xHalf);
        int localMouseY = (int) (mouseY - yHalf);

        Palette.setShaderAlpha(enterValue);
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(xHalf, yHalf, 0);

        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, localMouseX, localMouseY, partialTick);
        }

        pose.translate(0, -yHalf, 0);

        float yRot = cameraRotation.y;
        if (minecraft.options.getCameraType().isMirrored()) {
            yRot -= 180;
        }
        float scale = this.closerHolder.getValue();
        pose.scale(scale, scale, 1.0f);

        for (int i = 0; i < this.astrolabes.length; i++) {
            FriendAstrolabeWidget astrolabe = this.astrolabes[i];
            if (astrolabe == null)
                continue;

            final int diff = i - this.astrolabeAmountHalf;
            float rot = yRot - diff * 45;

            if (Mth.abs(rot) > 60.0f)
                continue;

            pose.pushPose();
            pose.translate(0, -this.height, 300);
            pose.mulPose(Axis.ZP.rotationDegrees(rot));
            pose.translate(-rot, this.height + yHalf - enterY, -300);

            astrolabe.renderAstrolabe(guiGraphics, localMouseX, localMouseY, partialTick, rot);

            pose.popPose();
        }

        pose.popPose();
        Palette.setShaderAlpha(alpha);

        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Vector3f target = this.cameraHandler.get(CameraHandler.Property.ROTATION).getDest(new Vector3f());
        FriendAstrolabeWidget astrolabe = this.astrolabes[this.index];
        boolean rotated = false;
        switch (keyCode) {
            case GLFW.GLFW_KEY_A:
                if (--this.index < 0) {
                    this.index = this.astrolabeAmount - 1;
                }
                rotated = true;
                break;
            case GLFW.GLFW_KEY_D:
                this.index++;
                this.index %= this.astrolabeAmount;
                rotated = true;
                break;
        }

        if (rotated) {
            if (astrolabe != null) {
                astrolabe.hide();
            }
            astrolabe = this.astrolabes[this.index];
            if (astrolabe != null) {
                astrolabe.enter();
            }
            target.y = (this.index - this.astrolabeAmountHalf) * 45;
            if (this.minecraft.options.getCameraType().isMirrored())
                target.y += 180;
            this.cameraHandler.get(CameraHandler.Property.ROTATION).toTarget(target, 0.75f, 0.0f);
            return true;
        }

        if (astrolabe != null && astrolabe.keyPressed(keyCode, scanCode, modifiers))
            return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        FriendAstrolabeWidget astrolabe = this.astrolabes[this.index];
        double localMouseX = mouseX - this.width / 2.0;
        double localMouseY = mouseY - this.height / 2.0;

        if (astrolabe != null && astrolabe.mouseClicked(localMouseX, localMouseY, button)) {
            return true;
        }

        return super.mouseClicked(localMouseX, localMouseY, button);
    }
}
