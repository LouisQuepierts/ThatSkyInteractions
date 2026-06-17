package net.quepierts.thatskyinteractions.feature.registry;

import dev.anvilcraft.lib.v2.registrum.util.entry.SoundEventEntry;
import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;

@UtilityClass
@SuppressWarnings("unused")
public class TsiSoundEvents {

    public static final SoundEventEntry VOICE_CAT
            = ThatSkyInteractions.REGISTRUM.soundEvent("voice.cat").register();

    public static final SoundEventEntry VOICE_DOG
            = ThatSkyInteractions.REGISTRUM.soundEvent("voice.dog").register();

    public static final SoundEventEntry VOICE_FOX
            = ThatSkyInteractions.REGISTRUM.soundEvent("voice.fox").register();

    public static final SoundEventEntry VOICE_BIRD
            = ThatSkyInteractions.REGISTRUM.soundEvent("voice.bird").register();

    public static void register() { }
}
