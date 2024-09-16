package net.quepierts.thatskyinteractions.proxy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.TSIUserDataStorage;
import net.quepierts.thatskyinteractions.data.astrolabe.AstrolabeManager;
import net.quepierts.thatskyinteractions.data.astrolabe.node.AstrolabeNode;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeManager;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.network.Packets;
import net.quepierts.thatskyinteractions.registry.BlockEntities;
import net.quepierts.thatskyinteractions.registry.Blocks;
import net.quepierts.thatskyinteractions.registry.DataComponents;
import net.quepierts.thatskyinteractions.registry.Items;

import java.nio.file.Path;

public class CommonProxy {
    private final InteractTreeManager interactTreeManager;
    private final AstrolabeManager astrolabeManager;
    private final TSIUserDataStorage userDataManager;

    @SuppressWarnings("unused")
    public CommonProxy(IEventBus bus, ModContainer modContainer) {
        InteractTreeNode.register();
        AstrolabeNode.register();
        bus.addListener(RegisterPayloadHandlersEvent.class, Packets::onRegisterPayloadHandlers);
        interactTreeManager = new InteractTreeManager();
        astrolabeManager = new AstrolabeManager();
        userDataManager = new TSIUserDataStorage();

        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, this::onServerTick);
        NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, this::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, this::onReload);
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, this::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, this::onServerStopping);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.LoadFromFile.class, this.userDataManager::onLoadFromFile);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.SaveToFile.class, this.userDataManager::onSaveToFile);

        Blocks.REGISTER.register(bus);
        Items.REGISTER.register(bus);
        BlockEntities.REGISTER.register(bus);
        DataComponents.REGISTER.register(bus);
    }

    private void onServerTick(final ServerTickEvent.Post event) {

    }

    private void onServerStarted(final ServerStartedEvent event) {
        Path root = event.getServer().getWorldPath(LevelResource.ROOT);
        this.userDataManager.setRootPath(root);
    }

    private void onServerStopping(final ServerStoppingEvent event) {
        this.interactTreeManager.clear();
        this.astrolabeManager.clear();
        this.userDataManager.saveAndClear();
    }

    public void onReload(final AddReloadListenerEvent event) {
        event.addListener(this.interactTreeManager);
        event.addListener(this.astrolabeManager);
    }

    public void onLevelLoad(final LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
            RelationshipSavedData.init(level);
        }
    }

    public void onDatapackSync(final OnDatapackSyncEvent event) {
        ServerLevel level = event.getPlayerList().getServer().getLevel(Level.OVERWORLD);
        if (level == null)
            return;

        RelationshipSavedData data = RelationshipSavedData.getRelationTree(level);
        if (data == null) {
            ThatSkyInteractions.LOGGER.warn("TSI Data Error");
            return;
        }

        this.interactTreeManager.sync(event);
        this.astrolabeManager.sync(event);
        this.userDataManager.sync(event);
        data.sync(event);
    }

    public InteractTreeManager getInteractTreeManager() {
        return interactTreeManager;
    }

    public AstrolabeManager getAstrolabeManager() {
        return astrolabeManager;
    }

    public TSIUserDataStorage getUserDataManager() {
        return userDataManager;
    }
}
