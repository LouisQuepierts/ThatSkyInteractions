package net.quepierts.thatskyinteractions.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.BiPacket;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenGridLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.interact.InteractionRequestW2SButton;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class InteractButtonPacket extends BiPacket {
    public static Type<InteractButtonPacket> TYPE = NetworkPackets.createType(InteractButtonPacket.class);
    private static final byte INVITE = 0;
    private static final byte CANCEL = 1;
    protected UUID other;
    protected final ResourceLocation interaction;
    protected final byte code;

    protected InteractButtonPacket(UUID sender, ResourceLocation interaction, byte code) {
        this.other = sender;
        this.interaction = interaction;
        this.code = code;
    }

    public static InteractButtonPacket decode(FriendlyByteBuf byteBuf) {
        UUID sender = byteBuf.readUUID();
        ResourceLocation interaction = byteBuf.readResourceLocation();
        byte code = byteBuf.readByte();

        return switch (code) {
            case INVITE -> new Invite(sender, interaction);
            case CANCEL -> new Cancel(sender, interaction);
            default -> null;
        };
    }

    @Override
    protected void update(@NotNull ServerPlayer serverPlayer) {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        ServerPlayer player = server.getPlayerList().getPlayer(other);
        if (player == null)
            return;

        this.other = serverPlayer.getUUID();
        SimpleAnimator.getNetwork().sendToPlayer(this, player);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUUID(other);
        friendlyByteBuf.writeResourceLocation(interaction);
        friendlyByteBuf.writeByte(code);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Invite extends InteractButtonPacket {
        public Invite(UUID other, ResourceLocation interaction) {
            super(other, interaction, INVITE);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(other);

            if (player == null)
                return;

            World2ScreenGridLayer.INSTANCE.addWorldPositionObject(other, new InteractionRequestW2SButton(
                    this.interaction,
                    player
            ));
        }
    }

    public static class Cancel extends InteractButtonPacket {
        public Cancel(UUID other, ResourceLocation interaction) {
            super(other, interaction, CANCEL);
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        protected void sync() {
            World2ScreenGridLayer.INSTANCE.remove(this.other);
        }
    }
}
