package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.client.gui.holder.FloatHolder;
import net.quepierts.thatskyinteractions.client.util.FakeClientPlayer;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;
import org.joml.Vector3f;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public final class FakePlayerIgniteW2SButton extends World2ScreenButton {
    public static final ResourceLocation TEXTURE = ThatSkyInteractions.getLocation("textures/gui/ignite.png");
    private final FakeClientPlayer bound;
    private final FloatHolder enterHolder;
    private boolean clicked = false;

    public FakePlayerIgniteW2SButton(FakeClientPlayer bound, FloatHolder enterHolder) {
        super(TEXTURE);
        this.bound = bound;
        this.enterHolder = enterHolder;
    }

    @Override
    public boolean shouldRemove() {
        return clicked || this.enterHolder.getValue() < 0.3f;
    }

    @Override
    public void invoke() {
        ClientProxy client = ThatSkyInteractions.getInstance().getClient();
        ClientTSIDataCache cache = client.getCache();
        UUID friendUUID = this.bound.getDisplayUUID();
        cache.sendLight(friendUUID, true);
        this.clicked = true;
    }

    @Override
    public void getWorldPos(Vector3f out) {
        Vec3 position = this.bound.position();
        out.set(
                position.x(),
                position.y() + 7.5f - enterHolder.getValue() * 5f,
                position.z()
        );
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
