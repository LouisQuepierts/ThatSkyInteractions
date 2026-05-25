package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.ClientHooks;
import net.quepierts.thatskyinteractions.feature.client.gui.layer.AnimatableScreenLayer;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.AnimatableScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientHooks.class)
public class ClientHooksMixin {

    @Inject(
            method = "pushGuiLayer",
            at = @At("TAIL")
    )
    private static void tsi$pushGuiLayer(
            final Minecraft minecraft,
            final Screen screen,
            final CallbackInfo ci
    ) {
        if (screen instanceof AnimatableScreen animatable) {
            AnimatableScreenLayer.INSTANCE.push(animatable);
        }
    }

}
