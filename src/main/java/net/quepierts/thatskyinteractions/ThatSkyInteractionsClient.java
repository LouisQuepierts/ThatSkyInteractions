package net.quepierts.thatskyinteractions;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ThatSkyInteractions.MODID, dist = Dist.CLIENT)
public class ThatSkyInteractionsClient {

    public ThatSkyInteractionsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
//        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

}
