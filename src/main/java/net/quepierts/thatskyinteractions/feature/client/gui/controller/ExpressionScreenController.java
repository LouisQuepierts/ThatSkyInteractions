package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerCallSystem;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerExpressionSystem;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionManager;
import net.quepierts.thatskyinteractions.feature.expression.PlayerPreferenceAttachment;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceType;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceTypeManager;
import net.quepierts.thatskyinteractions.feature.expression.call.packet.ChangeVoicePacket;

public final class ExpressionScreenController extends ScreenController<Void> {
    public ExpressionScreenController() {
        super(null);
    }

    public void onExpressionClicked(final int index, final int level) {
        final var manager       = PlayerExpressionManager.getInstance();
        final var ordinal       = manager.ordinal();

        if (index >= ordinal.size()) {
            return;
        }

        final var identifier    = ordinal.get(index);

        if (identifier == null) {
            return;
        }

        ClientPlayerExpressionSystem.perform(identifier, level);
    }

    public void onVoiceClicked(final Identifier identifier) {

        final var preference = ClientPlayerCallSystem.getPreference();

        final var type = PlayerVoiceTypeManager.getInstance().get(identifier);
        if (type == null) {
            return;
        }

        if (type.available()) {
            Minecraft.getInstance()
                    .getSoundManager()
                    .play(SimpleSoundInstance.forUI(type.getSound().value(), 1.0f));
        }
        if (preference.getVoice().equals(identifier)) {
            return;
        }

        ClientPacketDistributor.sendToServer(
                new ChangeVoicePacket(identifier)
        );

    }

    @Override
    public void onTick(final float delta) {
        super.onTick(delta);
    }
}
