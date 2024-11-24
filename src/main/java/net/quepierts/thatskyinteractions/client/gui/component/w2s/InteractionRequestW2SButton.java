package net.quepierts.thatskyinteractions.client.gui.component.w2s;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.api.IInteractHandler;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.gui.Palette;
import net.quepierts.thatskyinteractions.client.util.RenderUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

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

    @Override
    @NotNull
    public Component getPrompt(boolean byMouse) {
        return byMouse ?
                Component.translatable(
                        "gui.thatskyinteractions.prompt.w2s.mouse.request"
                ).withColor(Palette.NORMAL_TEXT_COLOR)  :
                Component.translatable(
                        "gui.thatskyinteractions.prompt.w2s.world.request",
                        Component.translatable(ThatSkyInteractions.getInstance().getClient().options.keyEnabledInteract.get().getKey().getName()).withColor(Palette.HIGHLIGHT_TEXT_COLOR),
                        Component.translatable(InputConstants.Type.MOUSE.getOrCreate(GLFW.GLFW_MOUSE_BUTTON_RIGHT).getName()).withColor(Palette.HIGHLIGHT_TEXT_COLOR)
                ).withColor(Palette.NORMAL_TEXT_COLOR) ;
    }

    @NotNull
    public String getPromptType() {
        return "request";
    }
}
