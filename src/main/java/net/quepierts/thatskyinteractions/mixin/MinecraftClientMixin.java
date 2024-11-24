package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.AnimatableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(
            method = "setScreen",
            at = @At("HEAD")
    )
    public void tsi$setScreen(Screen guiScreen, CallbackInfo ci) {
        if (guiScreen != null && !(guiScreen instanceof AnimatableScreen)) {
            AnimateScreenHolderLayer.INSTANCE.reset();
        }
    }
}
