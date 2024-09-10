package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.CameraType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.component.button.LikeButton;
import net.quepierts.thatskyinteractions.client.gui.component.button.NicknameButton;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.client.util.CameraHandler;
import net.quepierts.thatskyinteractions.client.util.FakePlayerDisplayHandler;
import net.quepierts.thatskyinteractions.data.FriendData;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class FriendScreen extends RightPoopScreen {
    private final FakePlayerDisplayHandler fakePlayerDisplayHandler;
    private final CameraHandler cameraHandler;
    private final FriendData friendData;
    private final ClientProxy client;
    private boolean shown = false;
    public FriendScreen(@NotNull FriendData friendData) {
        super(Component.literal(friendData.getUsername()), 72);

        this.friendData = friendData;
        this.client = ThatSkyInteractions.getInstance().getClient();
        this.fakePlayerDisplayHandler = client.getFakePlayerDisplayHandler();
        this.fakePlayerDisplayHandler.setPlayerSkin(friendData.getUuid());

        this.cameraHandler = client.getCameraHandler();

    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new NicknameButton(20, 20, this.animator, this.friendData));
        this.addRenderableWidget(new LikeButton(20, 80, this.animator, this.friendData));
    }

    @Override
    public void enter() {
        super.enter();

        if (shown)
            return;

        CameraHandler.Entry rotation = this.cameraHandler.get(CameraHandler.Property.ROTATION);
        Vector3f rotationUnmodified = rotation.getUnmodified(new Vector3f());
        CameraType cameraType = minecraft.options.getCameraType();
        float rotY = rotationUnmodified.y;
        if (cameraType.isMirrored()) {
            rotY += 135;
        } else if (!cameraType.isFirstPerson()) {
            rotY -= 45;
        } else {
            rotY = 0;
        }
        rotation.toTarget(new Vector3f(0 - rotationUnmodified.x, rotY, 0), 1.0f);

        Entity camera = Objects.requireNonNull(minecraft.cameraEntity);
        CameraHandler.Entry position = this.cameraHandler.get(CameraHandler.Property.POSITION);
        float yRot = camera.getYRot() * Mth.DEG_TO_RAD;
        float cos = Mth.cos(yRot);
        float sin = Mth.sin(yRot);

        if (cameraType.isFirstPerson()) {
            position.toTarget(new Vector3f(0, 0, 0));
        } else {
            Vector3f left = new Vector3f(-cos, 0, -sin);
            position.toTarget(left);
        }

        Vec3 forward = new Vec3(-sin * 2, 0, cos * 2).add(camera.position());
        float rot = camera.getYHeadRot() - 180;
        this.fakePlayerDisplayHandler.show(forward, rot);
        this.shown = true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == client.options.keyClickButton.get().getKey().getValue()) {
            World2ScreenWidgetLayer.INSTANCE.click();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.fakePlayerDisplayHandler.hide();
    }
}
