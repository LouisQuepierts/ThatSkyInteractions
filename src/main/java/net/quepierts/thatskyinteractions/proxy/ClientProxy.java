package net.quepierts.thatskyinteractions.proxy;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.quepierts.simpleanimator.api.IInteractHandler;
import net.quepierts.simpleanimator.api.event.client.ClientAnimatorStateEvent;
import net.quepierts.simpleanimator.api.event.common.*;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.Options;
import net.quepierts.thatskyinteractions.client.Particles;
import net.quepierts.thatskyinteractions.client.UnlockRelationshipHandler;
import net.quepierts.thatskyinteractions.client.data.BlockedPlayerList;
import net.quepierts.thatskyinteractions.client.data.RelationshipDataCache;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.CandleInfoLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenGridLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.PlayerInteractScreen;
import net.quepierts.thatskyinteractions.client.particle.ShorterFlameParticle;
import net.quepierts.thatskyinteractions.client.render.CandleLayer;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.network.packet.InteractButtonPacket;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Set;
import java.util.UUID;

public class ClientProxy extends CommonProxy {
    private static final ResourceLocation EMPTY_LOCATION = ResourceLocation.withDefaultNamespace("empty");
    public final Options options;
    private final RelationshipDataCache dataCache;
    private final UnlockRelationshipHandler unlockRelationshipHandler;
    private final Logger logger = LogUtils.getLogger();


    @Nullable private UUID target;
    private BlockedPlayerList blockedPlayerList;
    private static final Set<ResourceLocation> HELD_ANIMATIONS = ObjectSet.of(
            Animations.HELD_CANDLE,
            Animations.UNLOCK_INVITE
    );

    public ClientProxy(IEventBus modBus, ModContainer modContainer) {
        super(modBus, modContainer);

        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.EntityInteract.class, this::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(InputEvent.MouseScrollingEvent.class, this::onMouseScrolling);
        NeoForge.EVENT_BUS.addListener(InputEvent.Key.class, this::onKey);
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, this::onLoggingIn);
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, this::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, this::onClientTick);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, this::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, this::onWorldLoad);
        NeoForge.EVENT_BUS.addListener(RenderGuiEvent.Pre.class, this::onRenderGUI);

        SimpleAnimator.EVENT_BUS.addListener(AnimatorEvent.Play.class, this::onAnimatorPlay);
        SimpleAnimator.EVENT_BUS.addListener(AnimatorEvent.Stop.class, this::onAnimatorStop);
        SimpleAnimator.EVENT_BUS.addListener(ClientAnimatorStateEvent.Exit.class, this::onClientAnimatorExit);
        SimpleAnimator.EVENT_BUS.addListener(AnimateStopEvent.Pre.class, this::onStopAnimate);
        SimpleAnimator.EVENT_BUS.addListener(CancelInteractEvent.class, this::onCancelInteract);
        SimpleAnimator.EVENT_BUS.addListener(InteractInviteEvent.Pre.class, this::onInteractInvite);
        SimpleAnimator.EVENT_BUS.addListener(InteractAcceptEvent.Post.class, this::onInteractAccepted);

        options = new Options();
        dataCache = new RelationshipDataCache();
        unlockRelationshipHandler = new UnlockRelationshipHandler();

        modBus.addListener(RegisterGuiLayersEvent.class, this::onRegisterGuiLayers);
        modBus.addListener(RegisterParticleProvidersEvent.class, this::onRegisterParticleProviders);
        modBus.addListener(RegisterKeyMappingsEvent.class, options::register);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        Particles.REGISTER.register(modBus);
    }

    private void onRenderGUI(RenderGuiEvent.Pre event) {
        ScreenAnimator.GLOBAL.tick();
    }

    private void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        World2ScreenGridLayer.INSTANCE.remove(uuid);

        Minecraft minecraft = Minecraft.getInstance();
        if (this.target != null && this.target.equals(uuid) && minecraft.screen instanceof PlayerInteractScreen) {
            this.target = null;
        }
    }

    private void onStopAnimate(final AnimateStopEvent.Pre event) {
        if (this.unlockRelationshipHandler.hasInvite()) {
            this.unlockRelationshipHandler.cancel();
        }
    }

    private void onClientAnimatorExit(final ClientAnimatorStateEvent.Exit event) {
        if (!event.getOwner().equals(Minecraft.getInstance().player.getUUID()))
            return;

        switch (event.getCurState()) {
            case LOOP:
                if (event.getAnimationID().equals(Animations.UNLOCK_ACCEPT)) {
                    this.unlockRelationshipHandler.accepted();
                }
                break;
        }
    }

    private void onAnimatorPlay(final AnimatorEvent.Play event) {
        if (HELD_ANIMATIONS.contains(event.getAnimationID())) {
            CandleLayer.enable(event.getOwner());
        } else {
            CandleLayer.disable(event.getOwner());
        }
    }

    private void onAnimatorStop(final AnimatorEvent.Stop event) {
        if (HELD_ANIMATIONS.contains(event.getAnimationID())) {
            CandleLayer.disable(event.getOwner());
        }
    }

    private void onInteractInvite(final InteractInviteEvent.Pre event) {
        if (!event.getTarget().isLocalPlayer())
            return;

        if (!event.getInteractionID().getNamespace().equals(ThatSkyInteractions.MODID))
            return;

    }

    private void onInteractAccepted(final InteractAcceptEvent.Post event) {
        if (!event.getAcceptor().isLocalPlayer())
            return;

        World2ScreenGridLayer.INSTANCE.remove(event.getInviter().getUUID());
    }

    private void onCancelInteract(final CancelInteractEvent event) {
        if (!event.getPlayer().isLocalPlayer())
            return;

        if (!event.getInteractionID().getNamespace().equals(ThatSkyInteractions.MODID))
            return;

        SimpleAnimator.getNetwork().update(new InteractButtonPacket.Cancel(((IInteractHandler) event.getPlayer()).simpleanimator$getRequest().getTarget(), EMPTY_LOCATION));
    }

    private void onClientTick(final ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level != null) {
            World2ScreenGridLayer.INSTANCE.tick();
        }
    }

    private void onWorldLoad(final LevelEvent.Load event) {

    }

    private void onLoggingIn(final ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft minecraft = Minecraft.getInstance();
        ServerData server = minecraft.getCurrentServer();

        if (server != null) {
            this.blockedPlayerList = BlockedPlayerList.loadLocal(BlockedPlayerList.Type.SERVER, server.name);
        } else {
            ClientLevel level = minecraft.level;
            if (level != null && level.getServer() instanceof IntegratedServer integratedServer) {
                this.blockedPlayerList = BlockedPlayerList.loadLocal(BlockedPlayerList.Type.LOCAL, integratedServer.getWorldData().getLevelName());
            }
        }
    }

    private void onLoggingOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        this.dataCache.clear();
        this.unlockRelationshipHandler.reset();
        World2ScreenGridLayer.INSTANCE.reset();
        CandleLayer.reset();

        if (this.blockedPlayerList != null) {
            this.blockedPlayerList.save();
            this.blockedPlayerList = null;
        }
    }

    private void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAboveAll(CandleInfoLayer.LOCATION, CandleInfoLayer.INSTANCE);
        event.registerBelow(CandleInfoLayer.LOCATION, AnimateScreenHolderLayer.LOCATION, AnimateScreenHolderLayer.INSTANCE);
        event.registerBelow(AnimateScreenHolderLayer.LOCATION, World2ScreenGridLayer.LOCATION, World2ScreenGridLayer.INSTANCE);
    }

    private void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(Particles.SHORTER_FLAME.get(), ShorterFlameParticle.Provider::new);
    }

    public void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        if (!event.getEntity().isLocalPlayer())
            return;

        if (!this.options.keyEnabledInteract.get().isDown())
            return;

        Entity target = event.getTarget();
        if (target instanceof Player) {
            InteractTree tree = this.dataCache.getTree();
            if (tree != null) {
                UUID uuid = target.getUUID();
                InteractTreeInstance instance = this.dataCache.get(uuid);
                Minecraft.getInstance().setScreen(new PlayerInteractScreen(target, tree, instance));
                this.setTarget(uuid);
                event.setCanceled(true);
            }
        }
    }

    private void onMouseScrolling(final InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().level == null)
            return;

        if (!this.options.keyEnabledInteract.get().isDown())
            return;

        World2ScreenGridLayer.INSTANCE.scroll(event.getMouseY());
        event.setCanceled(true);
    }

    private void onKey(final InputEvent.Key event) {
        if (options.keyEnabledInteract.get().isDown()) {
            if (options.keyClickButton.get().isDown()) {
                World2ScreenGridLayer.INSTANCE.click();
            }
        }
    }

    public boolean blocked(UUID player) {
        return this.blockedPlayerList.contains(player);
    }

    public void block(UUID player) {
        this.blockedPlayerList.add(player);
        World2ScreenGridLayer.INSTANCE.remove(player);
    }

    public void unblock(UUID player) {
        this.blockedPlayerList.remove(player);
    }


    public void setTarget(@Nullable UUID target) {
        this.target = target;
    }

    @Nullable
    public UUID getTarget() {
        return this.target;
    }

    public RelationshipDataCache getCache() {
        return this.dataCache;
    }

    public UnlockRelationshipHandler getUnlockRelationshipHandler() {
        return this.unlockRelationshipHandler;
    }

}
