package net.quepierts.thatskyinteractions.feature.expression.packet;

import dev.anvilcraft.lib.v2.network.packet.IClientboundPacket;
import dev.anvilcraft.lib.v2.network.packet.IPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.expression.Expression;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionAttachment;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionManager;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ExpressionControlPacket(
        Operation operation,
        UUID playerUUID,
        Identifier identifier
) implements IClientboundPacket {

    public static final Type<ExpressionControlPacket> TYPE
            = IPacket.type(ThatSkyInteractions.location("expression/control"));

    public static final StreamCodec<ByteBuf, ExpressionControlPacket> STREAM_CODEC
            = StreamCodec.composite(
                    ByteBufCodecs.BYTE.map(Operation::decode, Operation::encode),
                    ExpressionControlPacket::operation,
                    UUIDUtil.STREAM_CODEC,
                    ExpressionControlPacket::playerUUID,
                    Identifier.STREAM_CODEC,
                    ExpressionControlPacket::identifier,
                    ExpressionControlPacket::new
            );

    public static ExpressionControlPacket perform(@NonNull UUID playerUUID, @NonNull Identifier id) {
        return new ExpressionControlPacket(Operation.PERFORM, playerUUID, id);
    }

    public static ExpressionControlPacket cancel(@NonNull UUID playerUUID, @NonNull Identifier id) {
        return new ExpressionControlPacket(Operation.CANCEL, playerUUID, id);
    }

    public static ExpressionControlPacket finished(@NonNull UUID playerUUID, @NonNull Identifier id) {
        return new ExpressionControlPacket(Operation.FINISHED, playerUUID, id);
    }

    @Override
    public void handleOnClient(@NonNull Player player) {
        final var level = player.level();
        final var target = level.getPlayerByUUID(this.playerUUID());

        if (target == null) {
            return;
        }

        final var attachment = PlayerExpressionAttachment.getAttachment(target);

        switch (this.operation()) {
            case PERFORM: {
                final var identifier = this.identifier();
                attachment.start(identifier, target.level().getGameTime());
                final var expression = PlayerExpressionManager.getInstance().get(identifier);
                if (expression != null) {
                    expression.onClientPerform(player);
                }
                break;
            }
            case CANCEL:
            case FINISHED:
                attachment.clear();
                break;
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Operation {
        PERFORM,
        CANCEL,
        FINISHED;

        static final Operation[] VALUES = values();

        public static Operation decode(byte id) {
            return VALUES[id];
        }

        public byte encode() {
            return (byte) ordinal();
        }
    }
}
