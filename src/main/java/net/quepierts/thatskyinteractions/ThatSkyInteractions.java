package net.quepierts.thatskyinteractions;

import dev.anvilcraft.lib.v2.registrum.Registrum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import net.quepierts.thatskyinteractions.feature.registry.DataComponents;

@Slf4j
@Getter
@Mod(ThatSkyInteractions.MODID)
public class ThatSkyInteractions {
    public static final String MODID = "thatskyinteractions";
    public static final Registrum REGISTRUM = Registrum.create(MODID);

    public ThatSkyInteractions(IEventBus modBus) {
        DataComponents.REGISTRAR.register(modBus);

        AttachmentTypes.register();
    }

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
