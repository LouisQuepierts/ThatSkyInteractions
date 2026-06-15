package net.quepierts.thatskyinteractions.feature.client.reference;

import lombok.experimental.UtilityClass;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterTextureAtlasesEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class TsiAtlases {

    public static final Identifier ICONS
            = ThatSkyInteractions.location("icons");

    @SubscribeEvent
    public static void onRegisterTextureAtlases(final RegisterTextureAtlasesEvent event) {

        event.register(
                new AtlasManager.AtlasConfig(Sheets.ICONS, ICONS, false)
        );

    }

    public static final class Sheets {
        public static final Identifier ICONS
                = ThatSkyInteractions.location("textures/atlas/icons.png");
    }

}
