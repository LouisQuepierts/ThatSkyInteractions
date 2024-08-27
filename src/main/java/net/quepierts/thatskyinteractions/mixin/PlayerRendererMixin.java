package net.quepierts.thatskyinteractions.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    private static final ResourceLocation BLOCKED = ThatSkyInteractions.getLocation("textures/entity/blocked.png");
    @Inject(
            method = "getTextureLocation",
            at = @At("HEAD"),
            cancellable = true
    )
    public void tsi$blockedSkin(AbstractClientPlayer player, CallbackInfoReturnable<ResourceLocation> ci) {
        if (ThatSkyInteractions.getInstance().getClient().blocked(player.getUUID())) {
            ci.setReturnValue(BLOCKED);
            ci.cancel();
        }
    }
}
