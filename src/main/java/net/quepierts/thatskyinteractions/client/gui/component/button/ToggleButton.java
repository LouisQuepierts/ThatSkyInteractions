package net.quepierts.thatskyinteractions.client.gui.component.button;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ToggleButton extends BounceButton {
    private final ResourceLocation activeIcon;
    private boolean active = false;
    public ToggleButton(int x, int y, int scale, Component message, ScreenAnimator animator, ResourceLocation off, ResourceLocation on) {
        super(x, y, scale, message, animator, off);
        this.activeIcon = on;
    }

    @Override
    public void onPress() {
        this.active = !this.active;
    }

    @Override
    @NotNull
    public ResourceLocation getIcon() {
        return this.active ? this.activeIcon : this.icon;
    }
}
