package net.quepierts.thatskyinteractions;

import dev.anvilcraft.lib.v2.registrum.Registrum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.quepierts.thatskyinteractions.feature.animation.PlayerAnimationFactory;
import net.quepierts.thatskyinteractions.feature.data.DataSyncSystem;
import net.quepierts.thatskyinteractions.feature.registry.AnimationLayerTypes;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import net.quepierts.thatskyinteractions.feature.registry.FriendshipBehaviours;
import net.quepierts.thatskyinteractions.feature.registry.InteractionTypes;

@Slf4j
@Getter
@Mod(ThatSkyInteractions.MODID)
public class ThatSkyInteractions {
    public static final String MODID = "thatskyinteractions";
    public static final Registrum REGISTRUM = Registrum.create(MODID);

    public ThatSkyInteractions(IEventBus modBus) {
        AnimationLayerTypes.register();
        InteractionTypes.register();
        FriendshipBehaviours.register();

        AttachmentTypes.register();
        modBus.register(this);
    }

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @SubscribeEvent
    private void onFmlCommonSetup(final FMLCommonSetupEvent event) {
        PlayerAnimationFactory.register();
        DataSyncSystem.register();
    }
}
