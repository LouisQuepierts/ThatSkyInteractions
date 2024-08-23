package net.quepierts.thatskyinteractions.proxy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.astrolabe.AstrolabeManager;
import net.quepierts.thatskyinteractions.data.astrolabe.node.AstrolabeNode;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeManager;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.network.Packets;

public class CommonProxy {
    private final InteractTreeManager interactTreeManager;
    private final AstrolabeManager astrolabeManager;
    public CommonProxy(IEventBus bus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, this::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, this::onReload);
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, this::onDatapackSync);

        InteractTreeNode.register();
        AstrolabeNode.register();
        bus.addListener(RegisterPayloadHandlersEvent.class, Packets::onRegisterPayloadHandlers);
        interactTreeManager = new InteractTreeManager();
        astrolabeManager = new AstrolabeManager();
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

        RelationshipSavedData data = RelationshipSavedData.get(level);
        if (data == null) {
            ThatSkyInteractions.LOGGER.warn("TSI Data Error");
            return;
        }

        this.interactTreeManager.sync(event);
        this.astrolabeManager.sync(event);
        data.sync(event);
    }

    public InteractTreeManager getInteractTreeManager() {
        return interactTreeManager;
    }

    public AstrolabeManager getAstrolabeManager() {
        return astrolabeManager;
    }
}
