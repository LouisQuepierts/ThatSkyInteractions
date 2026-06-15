package net.quepierts.thatskyinteractions.feature.config;

import dev.anvilcraft.lib.v2.config.Comment;
import dev.anvilcraft.lib.v2.config.Config;
import net.neoforged.fml.config.ModConfig;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@Config(
        group = ThatSkyInteractions.MODID,
        name = ThatSkyInteractions.MODID,
        type = ModConfig.Type.SERVER
)
public class TsiServerConfig {

    @Comment("Enable conditional friendship, if enabled, players required currency (Candles) to unlock friendship nodes.")
    public boolean enableConditionalFriendship = false;

}
