package net.quepierts.thatskyinteractions.mixin.animata;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.quepierts.simpleanimator.core.animation.Animator;
import net.quepierts.simpleanimator.core.proxy.CommonProxy;
import net.quepierts.simpleanimator.neoforge.proxy.NeoForgeCommonProxy;
import org.spongepowered.asm.mixin.*;

import java.util.UUID;

@Mixin(NeoForgeCommonProxy.class)
public class CommonEventListenerMixin {
    @Shadow @Final
    private CommonProxy proxy;

    /**
     * @author Louis_Quepierts
     * @reason Wrong event listener
     */
    @SubscribeEvent
    @Overwrite
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
    }

    /**
     * @author Louis_Quepierts
     * @reason Wrong event listener
     */
    @SubscribeEvent
    @Overwrite
    public void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof Player player) {
            UUID uuid = player.getUUID();
            Animator animator = this.proxy.getAnimatorManager().getAnimator(uuid);
            if (animator != null) {
                animator.reset(false);
            }
            this.proxy.getInteractionManager().cancel(uuid);
        }
    }

    @SubscribeEvent
    @Unique
    public void tsi$onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            this.proxy.getAnimatorManager().sync(serverPlayer);
        }
    }

    @SubscribeEvent
    @Unique
    public void tsi$onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        this.proxy.getAnimatorManager().remove(uuid);
        this.proxy.getInteractionManager().cancel(uuid);
    }
}
