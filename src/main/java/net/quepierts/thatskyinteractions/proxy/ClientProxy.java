package net.quepierts.thatskyinteractions.proxy;

import com.mojang.blaze3d.pipeline.RenderTarget;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.quepierts.simpleanimator.api.IInteractHandler;
import net.quepierts.simpleanimator.api.animation.AnimationState;
import net.quepierts.simpleanimator.api.event.client.ClientAnimatorStateEvent;
import net.quepierts.simpleanimator.api.event.common.*;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.client.Options;
import net.quepierts.thatskyinteractions.client.data.ClientTSIDataCache;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.CandleInfoLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.FriendAstrolabeScreen;
import net.quepierts.thatskyinteractions.client.gui.screen.PlayerInteractScreen;
import net.quepierts.thatskyinteractions.client.particle.ShorterFlameParticle;
import net.quepierts.thatskyinteractions.client.registry.BlockEntityRenderers;
import net.quepierts.thatskyinteractions.client.registry.Particles;
import net.quepierts.thatskyinteractions.client.registry.PostEffects;
import net.quepierts.thatskyinteractions.client.registry.RenderTypes;
import net.quepierts.thatskyinteractions.client.render.CandleLayer;
import net.quepierts.thatskyinteractions.client.util.CameraHandler;
import net.quepierts.thatskyinteractions.client.util.FakePlayerDisplayHandler;
import net.quepierts.thatskyinteractions.client.util.UnlockRelationshipHandler;
import net.quepierts.thatskyinteractions.data.FriendData;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.mixin.accessor.LevelRendererAccessor;
import net.quepierts.thatskyinteractions.network.packet.InteractButtonPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ClientProxy extends CommonProxy {
    private static final ResourceLocation EMPTY_LOCATION = ResourceLocation.withDefaultNamespace("empty");
    public final Options options;
    @NotNull private final ClientTSIDataCache dataCache;
    @NotNull private final UnlockRelationshipHandler unlockRelationshipHandler;
    @NotNull private final FakePlayerDisplayHandler fakePlayerDisplayHandler;
    @NotNull private final CameraHandler cameraHandler;

    @Nullable private UUID target;
    private static final Set<ResourceLocation> HELD_ANIMATIONS = ObjectSet.of(
            Animations.HELD_CANDLE,
            Animations.UNLOCK_INVITE
    );

    public ClientProxy(IEventBus modBus, ModContainer modContainer) {
        super(modBus, modContainer);

        options = new Options();
        dataCache = new ClientTSIDataCache();
        unlockRelationshipHandler = new UnlockRelationshipHandler();
        fakePlayerDisplayHandler = new FakePlayerDisplayHandler(this);
        cameraHandler = new CameraHandler();

        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.EntityInteract.class, this::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(InputEvent.MouseScrollingEvent.class, this::onMouseScrolling);
        NeoForge.EVENT_BUS.addListener(InputEvent.Key.class, this::onKey);
        NeoForge.EVENT_BUS.addListener(InputEvent.MouseButton.Post.class, this::onMouseButton);
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingIn.class, this::onLoggingIn);
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, this::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, this::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedOutEvent.class, this::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, this::onWorldLoad);
        NeoForge.EVENT_BUS.addListener(RenderGuiEvent.Pre.class, this::onRenderGUI);
        NeoForge.EVENT_BUS.addListener(RenderNameTagEvent.class, this::onRenderNameTag);
        NeoForge.EVENT_BUS.addListener(RenderLevelStageEvent.class, this::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(ClientChatReceivedEvent.Player.class, this::onChatReceivedPlayer);
        NeoForge.EVENT_BUS.addListener(ViewportEvent.ComputeCameraAngles.class, cameraHandler::onComputeCameraAngles);
        NeoForge.EVENT_BUS.addListener(RenderPlayerEvent.Pre.class, fakePlayerDisplayHandler::onRenderPlayerPre);
        NeoForge.EVENT_BUS.addListener(RenderPlayerEvent.Post.class, fakePlayerDisplayHandler::onRenderPlayerPost);
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, fakePlayerDisplayHandler::onClientTick);

        SimpleAnimator.EVENT_BUS.addListener(AnimatorEvent.Play.class, this::onAnimatorPlay);
        SimpleAnimator.EVENT_BUS.addListener(AnimatorEvent.Stop.class, this::onAnimatorStop);
        SimpleAnimator.EVENT_BUS.addListener(ClientAnimatorStateEvent.Exit.class, this::onClientAnimatorExit);
        SimpleAnimator.EVENT_BUS.addListener(AnimateStopEvent.Pre.class, this::onStopAnimate);
        SimpleAnimator.EVENT_BUS.addListener(CancelInteractEvent.class, this::onCancelInteract);
        SimpleAnimator.EVENT_BUS.addListener(InteractInviteEvent.Pre.class, this::onInteractInvite);
        SimpleAnimator.EVENT_BUS.addListener(InteractAcceptEvent.Post.class, this::onInteractAccepted);

        modBus.addListener(RegisterGuiLayersEvent.class, this::onRegisterGuiLayers);
        modBus.addListener(RegisterParticleProvidersEvent.class, this::onRegisterParticleProviders);
        modBus.addListener(RegisterKeyMappingsEvent.class, options::register);
        modBus.addListener(EntityRenderersEvent.AddLayers.class, this::onAddLayers);
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, BlockEntityRenderers::onRegisterBER);
        modBus.addListener(RegisterRenderBuffersEvent.class, RenderTypes::onRegisterRenderBuffers);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        Particles.REGISTER.register(modBus);
    }

    private void onRenderLevelStage(final RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            RenderTarget bloomTarget = PostEffects.getBloomTarget();
            bloomTarget.setClearColor(0, 0, 0, 0);
            bloomTarget.clear(Minecraft.ON_OSX);
            Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            RenderBuffers buffers = ((LevelRendererAccessor) event.getLevelRenderer()).tsi$getRenderBuffers();
            PostEffects.applyWOLBloom(event.getPartialTick());
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            PostEffects.doWOLBloom();
        }
    }

    private void onChatReceivedPlayer(final ClientChatReceivedEvent.Player event) {
        UUID sender = event.getSender();
        if (this.dataCache.isFriend(sender)) {
            FriendData friendData = this.dataCache.getUserData().getNodeData(sender).getFriendData();
            Component decorated = event.getPlayerChatMessage().decoratedContent();
            event.setMessage(Component.translatable(ChatType.DEFAULT_CHAT_DECORATION.translationKey(), friendData.getNickname(), decorated));
        }
    }

    private void onRenderNameTag(final RenderNameTagEvent event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUUID();

        if (this.blocked(uuid)) {
            event.setCanRender(TriState.FALSE);
        } else if (this.dataCache.isFriend(uuid)) {
            FriendData friendData = this.dataCache.getUserData().getNodeData(uuid).getFriendData();
            if (friendData.getUsername().equals(friendData.getNickname()))
                return;
            event.setContent(Component.literal(friendData.getNickname()));
        }
    }

    private void onRenderGUI(RenderGuiEvent.Pre event) {
        ScreenAnimator.GLOBAL.tick();
    }


    private void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        //this.dataCache.setOnline(event.getEntity(), true);
    }

    private void onPlayerLoggedOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        UUID uuid = player.getUUID();
        World2ScreenWidgetLayer.INSTANCE.remove(uuid);

        //this.dataCache.setOnline(player, false);
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
        if (!event.getOwner().equals(Minecraft.getInstance().player.getUUID())) {
            return;
        }

        if (Objects.requireNonNull(event.getCurState()) == AnimationState.LOOP) {
            if (event.getAnimationID().equals(Animations.UNLOCK_ACCEPT)) {
                this.unlockRelationshipHandler.accepted();
            }
        }
    }

    private void onAnimatorPlay(final AnimatorEvent.Play event) {

    }

    private void onAnimatorStop(final AnimatorEvent.Stop event) {

    }

    private void onInteractInvite(final InteractInviteEvent.Pre event) {
        if (!event.getTarget().isLocalPlayer()) {
            return;
        }

        if (!event.getInteractionID().getNamespace().equals(ThatSkyInteractions.MODID)) {
            return;
        }

    }

    private void onInteractAccepted(final InteractAcceptEvent.Post event) {
        if (!event.getAcceptor().isLocalPlayer()) {
            return;
        }

        World2ScreenWidgetLayer.INSTANCE.remove(event.getInviter().getUUID());
    }

    private void onCancelInteract(final CancelInteractEvent event) {
        if (!event.getPlayer().isLocalPlayer()) {
            return;
        }

        if (!event.getInteractionID().getNamespace().equals(ThatSkyInteractions.MODID)) {
            return;
        }

        SimpleAnimator.getNetwork().update(new InteractButtonPacket.Cancel(((IInteractHandler) event.getPlayer()).simpleanimator$getRequest().getTarget(), EMPTY_LOCATION));
    }

    private void onWorldLoad(final LevelEvent.Load event) {

    }

    private void onLoggingIn(final ClientPlayerNetworkEvent.LoggingIn event) {
        this.fakePlayerDisplayHandler.init(event.getPlayer().clientLevel);
    }

    private void onLoggingOut(final ClientPlayerNetworkEvent.LoggingOut event) {
        this.dataCache.clear();
        this.unlockRelationshipHandler.reset();
        this.cameraHandler.cleanup();
        this.fakePlayerDisplayHandler.reset();
        AnimateScreenHolderLayer.INSTANCE.reset();
        World2ScreenWidgetLayer.INSTANCE.reset();
    }

    private void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAboveAll(CandleInfoLayer.LOCATION, CandleInfoLayer.INSTANCE);
        event.registerBelow(CandleInfoLayer.LOCATION, AnimateScreenHolderLayer.LOCATION, AnimateScreenHolderLayer.INSTANCE);
        event.registerBelow(VanillaGuiLayers.DEBUG_OVERLAY, World2ScreenWidgetLayer.LOCATION, World2ScreenWidgetLayer.INSTANCE);
    }

    private void onRegisterParticleProviders(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(Particles.SHORTER_FLAME.get(), ShorterFlameParticle.Provider::new);
    }

    public void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        if (!player.isLocalPlayer()) {
            return;
        }

        if (!this.options.keyEnabledInteract.get().isDown()) {
            return;
        }

        Entity target = event.getTarget();
        if (target instanceof Player) {
            InteractTree tree = this.dataCache.getTree();
            UUID uuid = target.getUUID();
            this.setTarget(uuid);
            InteractTreeInstance instance = this.dataCache.get(uuid);
            AnimateScreenHolderLayer.INSTANCE.push(new PlayerInteractScreen(target, tree, instance));
            event.setCanceled(true);
        }
        /*InteractTree tree = this.dataCache.getTree();
        if (tree != null) {
            UUID uuid = target.getUUID();
            InteractTreeInstance instance = this.dataCache.get(uuid);
            Minecraft.getInstance().setScreen(new PlayerInteractScreen(target, tree, instance));
            this.setTarget(uuid);
            event.setCanceled(true);
        }*/
    }

    private void onAddLayers(EntityRenderersEvent.AddLayers event) {
        this.addLayers(event.getSkin(PlayerSkin.Model.WIDE), event.getContext().getBlockRenderDispatcher());
        this.addLayers(event.getSkin(PlayerSkin.Model.SLIM), event.getContext().getBlockRenderDispatcher());
    }

    private void addLayers(EntityRenderer<? extends Player> renderer, BlockRenderDispatcher dispatcher) {
        if (renderer instanceof PlayerRenderer playerRenderer) {
            //playerRenderer.addLayer(new EffectDistributeLayer(playerRenderer));
            playerRenderer.addLayer(new CandleLayer(playerRenderer, dispatcher));
        }
    }

    private void onMouseScrolling(final InputEvent.MouseScrollingEvent event) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        if (!this.options.keyEnabledInteract.get().isDown()) {
            return;
        }

        World2ScreenWidgetLayer.INSTANCE.scroll(event.getMouseY());
        event.setCanceled(true);
    }

    private void onKey(final InputEvent.Key event) {
        if (options.keyEnabledInteract.get().isDown()) {
            if (options.keyClickButton.get().isDown()) {
                World2ScreenWidgetLayer.INSTANCE.click();
                return;
            }

            if (Minecraft.getInstance().screen == null) {
                if (options.keyOpenFriendAstrolabe.get().isDown()) {
                    AnimateScreenHolderLayer.INSTANCE.push(new FriendAstrolabeScreen());
                }
            }
        }
    }

    private void onMouseButton(final InputEvent.MouseButton.Post event) {

    }

    public boolean blocked(UUID player) {
        return this.dataCache.getUserData().isBlocked(player);
    }

    public void block(UUID player) {
        this.dataCache.getUserData().block(player);
        World2ScreenWidgetLayer.INSTANCE.remove(player);
    }

    public void unblock(UUID player) {
        this.dataCache.getUserData().unblock(player);
    }


    public void setTarget(@Nullable UUID target) {
        this.target = target;
    }

    @Nullable
    public UUID getTarget() {
        return this.target;
    }

    public ClientTSIDataCache getCache() {
        return this.dataCache;
    }

    public @NotNull UnlockRelationshipHandler getUnlockRelationshipHandler() {
        return this.unlockRelationshipHandler;
    }

    public @NotNull CameraHandler getCameraHandler() {
        return this.cameraHandler;
    }

    public @NotNull FakePlayerDisplayHandler getFakePlayerDisplayHandler() {
        return this.fakePlayerDisplayHandler;
    }
}
