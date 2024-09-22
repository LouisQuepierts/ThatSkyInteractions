package net.quepierts.thatskyinteractions.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class Options {
    private final String CATEGORY = "key.categories.thatskyinteractions";
    public final Lazy<KeyMapping> keyEnabledInteract;
    public final Lazy<KeyMapping> keyClickButton;
    public final Lazy<KeyMapping> keyOpenFriendAstrolabe;

    public Options() {
        this.keyEnabledInteract = Lazy.of(
                () -> new KeyMapping(
                        "key.thatskyinteractions.enable_interact",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_TAB,
                        CATEGORY
                )
        );

        this.keyClickButton = Lazy.of(
                () -> new KeyMapping(
                        "key.thatskyinteractions.interact_button",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        CATEGORY
                )
        );

        this.keyOpenFriendAstrolabe = Lazy.of(
                () -> new KeyMapping(
                        "key.thatskyinteractions.friend_astrolabe",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_T,
                        CATEGORY
                )
        );
    }

    public void register(final RegisterKeyMappingsEvent event) {
        event.register(keyEnabledInteract.get());
        event.register(keyClickButton.get());
        event.register(keyOpenFriendAstrolabe.get());
    }
}
