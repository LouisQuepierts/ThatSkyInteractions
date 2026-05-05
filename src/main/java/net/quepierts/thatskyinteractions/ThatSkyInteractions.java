package net.quepierts.thatskyinteractions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Slf4j
@Getter
@Mod(ThatSkyInteractions.MODID)
public class ThatSkyInteractions {
    public static final String MODID = "thatskyinteractions";

    private static ThatSkyInteractions instance;

    public ThatSkyInteractions(IEventBus modBus, ModContainer modContainer) {
        instance = this;
    }

    public static Identifier location(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
