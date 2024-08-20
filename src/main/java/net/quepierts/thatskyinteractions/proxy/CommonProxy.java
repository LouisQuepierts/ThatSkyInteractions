package net.quepierts.thatskyinteractions.proxy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeManager;
import net.quepierts.thatskyinteractions.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.network.Packets;

public class CommonProxy {
    private final InteractTreeManager interactTreeManager;
    public CommonProxy(IEventBus bus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, this::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, this::onReload);
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, this::onDatapackSync);

        InteractTreeNode.register();
        bus.addListener(Packets::onRegisterPayloadHandlers);
        interactTreeManager = new InteractTreeManager();

    }

    public void onReload(final AddReloadListenerEvent event) {
        event.addListener(this.interactTreeManager);
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
        data.sync(event);
    }

    public InteractTreeManager getInteractTreeManager() {
        return interactTreeManager;
    }
}
