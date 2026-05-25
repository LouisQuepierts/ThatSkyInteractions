package net.quepierts.thatskyinteractions.feature.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.experimental.UtilityClass;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.lwjgl.glfw.GLFW;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class TSIKeys {

    public static final KeyMapping.Category CATEGORY
            = new KeyMapping.Category(ThatSkyInteractions.location("interactions"));

    public static final KeyMapping KEY_INTERACT = new KeyMapping(
            "key.thatskyinteractions.interact",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_ENTER,
            CATEGORY
    );


    @SubscribeEvent
    public static void onRegisterKeyMapping(final RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);
        event.register(KEY_INTERACT);
    }

}
