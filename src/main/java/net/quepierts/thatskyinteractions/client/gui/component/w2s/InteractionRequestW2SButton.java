package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IInteractHandler;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
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
    public void getWorldPos(Vector3f out) {
        out.set(this.position);
    }
}
