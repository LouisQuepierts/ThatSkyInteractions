package net.quepierts.thatskyinteractions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.core.PlayerUtils;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.client.ClientPlayerNavigator;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenGridLayer;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.network.packet.UnlockRelationshipPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class UnlockRelationshipHandler {
    private final Minecraft minecraft = Minecraft.getInstance();
    private PlayerPair pair;
    private String node;
    private Runnable onAccepted;
    private Runnable onCanceled;
    private boolean invite = false;


    public void invite(
            @NotNull UUID target,
            @NotNull String node,
            @NotNull Runnable onAccepted,
            @NotNull Runnable onCanceled
    ) {
        LocalPlayer player = minecraft.player;
        UUID uuid = player.getUUID();

        if (uuid.equals(target))
            return;

        this.pair = new PlayerPair(target, uuid);
        this.node = node;
        this.onAccepted = onAccepted;
        this.onCanceled = onCanceled;
        this.invite = true;

        SimpleAnimator.getNetwork().update(new UnlockRelationshipPacket.Invite(uuid, pair, node));
    }

    public void cancel() {
        if (!hasInvite())
            return;
        this.onCanceled.run();
        SimpleAnimator.getNetwork().update(new UnlockRelationshipPacket.Cancel(minecraft.player.getUUID(), pair, node));
        this.reset();
    }

    public void finish() {
        if (!hasInvite())
            return;

        this.onAccepted.run();
        this.reset();
        ((IAnimateHandler) minecraft.player).simpleanimator$stopAnimate(true);
    }

    public void accept(PlayerPair pair, String node) {
        UUID local = minecraft.player.getUUID();
        UUID other = pair.getOther(local);

        Player player = minecraft.level.getPlayerByUUID(other);
        if (player == null)
            return;

        this.cancel();
        this.invite = false;
        this.pair = pair;
        this.node = node;

        Vec3 positionWorldSpace = PlayerUtils.getRelativePositionWorldSpace(player, 2, 0);
        if (minecraft.player.distanceToSqr(positionWorldSpace) > 2) {
            ClientPlayerNavigator navigator = SimpleAnimator.getClient().getNavigator();
            navigator.navigateTo(player, 2, 0, () -> SimpleAnimator.getNetwork().update(new UnlockRelationshipPacket.Accept(local, pair, node)));
            return;
        }

        SimpleAnimator.getNetwork().update(new UnlockRelationshipPacket.Accept(local, pair, node));
    }

    public void accepted() {
        if (!hasAccept())
            return;

        UUID local = minecraft.player.getUUID();
        World2ScreenGridLayer.INSTANCE.remove(this.pair.getOther(local));
        SimpleAnimator.getNetwork().update(new UnlockRelationshipPacket.Finish(local, pair, node));
        this.reset();
    }

    public void reset() {
        this.pair = null;
        this.node = null;
        this.onAccepted = null;
        this.onCanceled = null;
    }

    public boolean hasInvite() {
        return invite && pair != null;
    }

    public boolean hasAccept() {
        return !invite && pair != null;
    }
}
