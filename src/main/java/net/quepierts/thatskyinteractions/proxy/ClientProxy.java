package net.quepierts.thatskyinteractions.proxy;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.quepierts.thatskyinteractions.client.Options;
import net.quepierts.thatskyinteractions.client.RelationshipDataCache;
import net.quepierts.thatskyinteractions.client.gui.layer.AnimateScreenHolderLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.CandleInfoLayer;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenGridLayer;
import net.quepierts.thatskyinteractions.client.gui.screen.PlayerInteractScreen;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import org.slf4j.Logger;

public class ClientProxy extends CommonProxy {
    public final Options options;
    private final RelationshipDataCache dataCache;
    private final Logger logger = LogUtils.getLogger();

    public ClientProxy(IEventBus modBus, ModContainer modContainer) {
        super(modBus, modContainer);

        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.EntityInteract.class, this::onEntityInteract);
        NeoForge.EVENT_BUS.addListener(ClientPlayerNetworkEvent.LoggingOut.class, this::onLoggingOut);
        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, this::onClientTick);
        options = new Options();
        dataCache = new RelationshipDataCache();

        modBus.addListener(RegisterGuiLayersEvent.class, this::onRegisterGuiLayers);
        modBus.addListener(RegisterKeyMappingsEvent.class, options::register);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    private void onClientTick(final ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level != null) {
            World2ScreenGridLayer.INSTANCE.tick();
        }
    }

    private void onLoggingOut(final ClientPlayerNetworkEvent.LoggingOut loggingOut) {
        this.dataCache.clear();
    }

    private void onRegisterGuiLayers(final RegisterGuiLayersEvent event) {
        event.registerAboveAll(CandleInfoLayer.LOCATION, CandleInfoLayer.INSTANCE);
        event.registerAboveAll(World2ScreenGridLayer.LOCATION, World2ScreenGridLayer.INSTANCE);
        event.registerBelow(CandleInfoLayer.LOCATION, AnimateScreenHolderLayer.LOCATION, AnimateScreenHolderLayer.INSTANCE);
    }

    public void onEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        if (!event.getEntity().isLocalPlayer())
            return;

        if (!this.options.keyInteractMenu.get().isDown())
            return;

        if (event.getTarget() instanceof Player player) {
            this.logger.info("Interact: {}", player);
        } else {
            this.logger.info("Interact: {}", event.getTarget().getClass().getSimpleName());
        }
        InteractTree tree = this.dataCache.getTree();
        if (tree != null) {
            InteractTreeInstance instance = this.dataCache.get(event.getTarget().getUUID());
            Minecraft.getInstance().setScreen(new PlayerInteractScreen(event.getTarget(), tree, instance));
            event.setCanceled(true);
        }
    }

    public RelationshipDataCache getCache() {
        return dataCache;
    }

}
