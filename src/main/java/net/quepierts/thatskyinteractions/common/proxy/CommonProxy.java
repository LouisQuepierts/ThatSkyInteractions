package net.quepierts.thatskyinteractions.common.proxy;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.simpleanimator.core.network.INetwork;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.data.astrolabe.node.AstrolabeNode;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;
import net.quepierts.thatskyinteractions.common.data.global.TSIGlobalData;
import net.quepierts.thatskyinteractions.common.data.manager.AstrolabeManager;
import net.quepierts.thatskyinteractions.common.data.manager.InteractTreeManager;
import net.quepierts.thatskyinteractions.common.data.tree.node.InteractTreeNode;
import net.quepierts.thatskyinteractions.common.network.Packets;
import net.quepierts.thatskyinteractions.common.network.packet.UpdateDailyPickupPacket;
import net.quepierts.thatskyinteractions.common.registry.*;

public class CommonProxy {
    @SuppressWarnings("unused")
    public CommonProxy(IEventBus bus, ModContainer modContainer) {
        InteractTreeNode.register();
        AstrolabeNode.register();
        bus.addListener(RegisterPayloadHandlersEvent.class, Packets::onRegisterPayloadHandlers);

        NeoForge.EVENT_BUS.addListener(ServerTickEvent.Post.class, this::onServerTick);
        NeoForge.EVENT_BUS.addListener(LevelEvent.Load.class, this::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, this::onReload);
        NeoForge.EVENT_BUS.addListener(OnDatapackSyncEvent.class, this::onDatapackSync);
        NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, this::onServerStopping);
//        NeoForge.EVENT_BUS.addListener(PlayerEvent.LoadFromFile.class, this.userDataManager::onLoadFromFile);
//        NeoForge.EVENT_BUS.addListener(PlayerEvent.SaveToFile.class, this.userDataManager::onSaveToFile);

        Blocks.REGISTER.register(bus);
        Items.REGISTER.register(bus);
        BlockEntities.REGISTER.register(bus);
        DataComponents.REGISTER.register(bus);
        Particles.REGISTER.register(bus);
        CreativeModeTabs.REGISTER.register(bus);
        TriggerTypes.REGISTER.register(bus);
        AttachmentTypes.REGISTER.register(bus);
    }

    private long day = 0L;
    private void onServerTick(final ServerTickEvent.Post event) {
        ServerLevel level = event.getServer().getLevel(Level.OVERWORLD);
        assert level != null;

        if (level.getGameTime() % 1000L == 0) {
            long day = level.getGameTime() / 24000L;

            if (this.day == day) {
                return;
            }

            this.day = day;

            INetwork network = SimpleAnimator.getNetwork();
            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                if (UserDataAttachment.getAttachment(player).tryUpdateDaily(day)) {
                    network.sendToPlayer(new UpdateDailyPickupPacket(day), player);
                }
            }

            TSIGlobalData.getGlobalRelationData(level).update(day);
        }
    }

    private void onServerStopping(final ServerStoppingEvent event) {
        InteractTreeManager.INSTANCE.clear();
        AstrolabeManager.INSTANCE.clear();
        //this.userDataManager.saveAndClear();
    }

    public void onReload(final AddReloadListenerEvent event) {
        event.addListener(InteractTreeManager.INSTANCE);
        event.addListener(AstrolabeManager.INSTANCE);
    }

    public void onLevelLoad(final LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
            TSIGlobalData.init(level);
        }
    }

    public void onDatapackSync(final OnDatapackSyncEvent event) {
        ServerLevel level = event.getPlayerList().getServer().getLevel(Level.OVERWORLD);
        if (level == null)
            return;

        TSIGlobalData data = TSIGlobalData.getGlobalRelationData(level);
        if (data == null) {
            ThatSkyInteractions.LOGGER.warn("TSI Data Error");
            return;
        }

        InteractTreeManager.INSTANCE.sync(event);
        AstrolabeManager.INSTANCE.sync(event);
    }
}
