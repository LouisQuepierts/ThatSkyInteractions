package net.quepierts.thatskyinteractions.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.LerpNumberAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.animate.WaitAnimation;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.FakePlayerIgniteW2SButton;
import net.quepierts.thatskyinteractions.client.gui.component.w2s.FakePlayerLightW2SWidget;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;

import java.util.UUID;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class FakePlayerDisplayHandler {
    private final ClientProxy client;
    private final FloatHolder enterHolder = new FloatHolder(0.0f);
    private final LerpNumberAnimation enterAnimation = new LerpNumberAnimation(this.enterHolder, AnimateUtils.Lerp::smooth, 0, 1, 1.0f);
    private final WaitAnimation addButtonLater = new WaitAnimation(0.8f, this::addButton);
    private FakeClientPlayer player;
    private FakePlayerLightW2SWidget light;
    private FakePlayerIgniteW2SButton ignite;
    private boolean pushed = false;
    private boolean canRepos = false;
    private boolean canIgnite = false;

    public FakePlayerDisplayHandler(ClientProxy clientProxy) {
        this.client = clientProxy;
    }

    public void init(ClientLevel level) {
    }

    public void reset() {
        if (this.player != null) {
            World2ScreenWidgetLayer.INSTANCE.remove(this.player.getUUID());
        }
        this.player = null;
        this.light = null;
        this.ignite = null;
    }

    public void show(Vec3 pos, float yRot) {
        this.player = new FakeClientPlayer(Minecraft.getInstance().level, this);
        this.light = new FakePlayerLightW2SWidget(this.player, this.enterHolder);
        this.ignite = new FakePlayerIgniteW2SButton(this.player, this.enterHolder);
        this.player.setPos(pos);
        this.player.setYRot(yRot);
        this.player.setYBodyRot(yRot);
        this.player.setYHeadRot(yRot);
        this.enterAnimation.reset(0, 1);
        World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(this.player.getUUID(), light);

        UUID uuid = this.player.getDisplayUUID();
        FriendAstrolabeInstance.NodeData data = client.getCache().getUserData().getNodeData(uuid);
        this.canIgnite = (data != null && !data.hasFlag(FriendAstrolabeInstance.Flag.SENT));

        ScreenAnimator.GLOBAL.play(this.enterAnimation);
        if (this.canIgnite) {
            ScreenAnimator.GLOBAL.play(this.addButtonLater);
        }
    }

    public void hide() {
        this.enterAnimation.reset(1, 0);
        this.canRepos = true;
        ScreenAnimator.GLOBAL.play(this.enterAnimation);
        World2ScreenWidgetLayer.INSTANCE.lock(null);
    }

    public boolean isVisible() {
        return this.enterHolder.getValue() != 0.0f;
    }

    public void onRenderPlayerPre(final RenderPlayerEvent.Pre event) {
        if (!event.getEntity().equals(this.player))
            return;

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        float value = this.enterHolder.getValue();
        poseStack.translate(0, 5 - 5 * value, 0);
        poseStack.scale(value, value, value);

        pushed = true;
    }

    public void onRenderPlayerPost(final RenderPlayerEvent.Post event) {
        if (!pushed)
            return;

        RenderSystem.setShaderColor(1, 1, 1, 1);
        event.getPoseStack().popPose();
        pushed = false;
    }

    public void onClientTick(final ClientTickEvent.Post event) {
        if (!this.enterAnimation.isRunning()) {
            if (this.canRepos && this.player != null) {
                this.player.remove(Entity.RemovalReason.DISCARDED);
                this.canRepos = false;
            }
        }
    }

    public void setPlayerSkin(UUID uuid) {
        if (this.player != null) {
            this.player.setPlayerSkin(uuid);
        }
    }

    private void addButton() {
        if (this.player != null) {
            this.ignite.setClicked(false);
            World2ScreenWidgetLayer.INSTANCE.addWorldPositionObject(this.player.getUUID(), this.ignite);
            World2ScreenWidgetLayer.INSTANCE.lock(this.ignite);
            this.canIgnite = false;
        }
    }

}
