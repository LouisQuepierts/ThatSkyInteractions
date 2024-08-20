package net.quepierts.thatskyinteractions.client.gui.layer.interact;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.quepierts.simpleanimator.api.IInteractHandler;
import net.quepierts.thatskyinteractions.client.RenderUtils;
import org.joml.Vector3f;

public class InteractionRequestW2SButton extends World2ScreenButton {
    private final Vector3f position;
    private final Player other;
    public InteractionRequestW2SButton(ResourceLocation interaction, Player player) {
        super(RenderUtils.getInteractionIcon(interaction));
        this.position = player.position().toVector3f().add(0, 2.4f, 0);
        this.other = player;
    }

    @Override
    public void invoke() {
        ((IInteractHandler) Minecraft.getInstance().player).simpleanimator$acceptInteract(other, true, false);
    }

    @Override
    public Vector3f getWorldPos() {
        return this.position;
    }
}
