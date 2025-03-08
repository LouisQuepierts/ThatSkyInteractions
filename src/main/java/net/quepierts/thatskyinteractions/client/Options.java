package net.quepierts.thatskyinteractions.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = ThatSkyInteractions.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Options {
    private static final String CATEGORY = "key.categories.thatskyinteractions";
    public static final Lazy<KeyMapping> KEY_ENABLED_INTERACT;
    public static final Lazy<KeyMapping> KEY_OPEN_ROULETTE;
    public static final Lazy<KeyMapping> KEY_OPEN_FRIEND_ASTROLABE;

    Options() {}

    @SubscribeEvent
    public static void register(final RegisterKeyMappingsEvent event) {
        event.register(KEY_ENABLED_INTERACT.get());
        event.register(KEY_OPEN_ROULETTE.get());
        event.register(KEY_OPEN_FRIEND_ASTROLABE.get());
    }

    static {
        KEY_ENABLED_INTERACT = Lazy.of(
                () -> new KeyMapping(
                        "key.thatskyinteractions.enable_interact",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_TAB,
                        CATEGORY
                )
        );

        KEY_OPEN_ROULETTE = Lazy.of(
                () -> new KeyMapping(
                        "key.thatskyinteractions.interact_button",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_Z,
                        CATEGORY
                )
        );

        KEY_OPEN_FRIEND_ASTROLABE = Lazy.of(
                () -> new KeyMapping(
                        "key.thatskyinteractions.friend_astrolabe",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_T,
                        CATEGORY
                )
        );
    }
}
