package net.quepierts.thatskyinteractions.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.attachment.component.AstrolabeComponent;
import net.quepierts.thatskyinteractions.common.data.attachment.component.RelationshipComponent;
import net.quepierts.thatskyinteractions.common.network.packet.BlockPlayerPacket;
import net.quepierts.thatskyinteractions.common.network.packet.astrolabe.AstrolabeModifyPacket;
import net.quepierts.thatskyinteractions.common.network.packet.astrolabe.AstrolabeOperationPacket;

import java.util.UUID;

public class ClientHelper {
    public static boolean blocked(UUID player) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return false;
        }

        return UserDataAttachment.getAttachment(localPlayer).getRelationship().isBlocked(player);
    }

    public static void toggleBlock(UUID player) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        RelationshipComponent relationship = UserDataAttachment.getAttachment(localPlayer).getRelationship();

        boolean blocked = relationship.isBlocked(player);
        if (blocked) {
            relationship.unblock(player);
        } else {
            relationship.block(player);
        }
        SimpleAnimator.getNetwork().update(new BlockPlayerPacket(player, !blocked));
    }

    public static void likeFriend(UUID uuid) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(localPlayer).getAstrolabe();
        if (astrolabe.likeFriend(uuid)) {
            SimpleAnimator.getNetwork().update(new AstrolabeModifyPacket.Like(uuid));
        }
    }

    public static void unlikeFriend(UUID uuid) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(localPlayer).getAstrolabe();
        if (astrolabe.unlikeFriend(uuid)) {
            SimpleAnimator.getNetwork().update(new AstrolabeModifyPacket.Unlike(uuid));
        }
    }

    public static void sendLight(UUID target) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(localPlayer).getAstrolabe();

        if (astrolabe.sendLight(target)) {
            SimpleAnimator.getNetwork().update(new AstrolabeOperationPacket.Ignite(target));
        }
    }

    public static void awardLight(UUID target) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(localPlayer).getAstrolabe();
        astrolabe.awardLight(target);
    }

    public static void gainLight(UUID uuid) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;

        if (localPlayer == null) {
            return;
        }

        AstrolabeComponent astrolabe = UserDataAttachment.getAttachment(localPlayer).getAstrolabe();

        if (astrolabe.gainLight(uuid)) {
            SimpleAnimator.getNetwork().update(new AstrolabeOperationPacket.Gain(uuid));
        }
    }
}
