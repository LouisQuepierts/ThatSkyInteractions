package net.quepierts.thatskyinteractions.client.gui.screen;

import net.minecraft.client.CameraType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.util.CameraHandler;
import net.quepierts.thatskyinteractions.client.util.FakePlayerDisplayHandler;
import net.quepierts.thatskyinteractions.data.FriendData;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Objects;

public class FriendScreen extends RightPoopScreen {
    private final FakePlayerDisplayHandler fakePlayerDisplayHandler;
    private final CameraHandler cameraHandler;
    public FriendScreen(@NotNull FriendData friendData) {
        super(Component.literal(friendData.getNickname()), 160);

        this.fakePlayerDisplayHandler = ThatSkyInteractions.getInstance().getClient().getFakePlayerDisplayHandler();
        this.fakePlayerDisplayHandler.setPlayerSkin(friendData.getUuid());

        this.cameraHandler = ThatSkyInteractions.getInstance().getClient().getCameraHandler();
    }

    @Override
    public void enter() {
        super.enter();

        CameraHandler.Entry rotation = this.cameraHandler.get(CameraHandler.Property.ROTATION);
        Vector3f rotationUnmodified = rotation.getUnmodified(new Vector3f());
        Vector3f rotationModified = rotation.getModified(new Vector3f());
        CameraType cameraType = minecraft.options.getCameraType();
        float rotY = rotationModified.y;
        if (!cameraType.isFirstPerson()) {
            rotY -= 45;
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
    }

    @Override
    public void hide() {
        super.hide();
        this.fakePlayerDisplayHandler.hide();
    }
}
