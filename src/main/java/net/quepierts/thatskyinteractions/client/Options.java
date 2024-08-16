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
    public final Lazy<KeyMapping> keyInteractMenu;

    public Options() {
        this.keyInteractMenu = Lazy.of(
                () -> new KeyMapping(
                        "key.thatskyinteractions.interact_menu",
                        KeyConflictContext.IN_GAME,
                        InputConstants.Type.KEYSYM,
                        GLFW.GLFW_KEY_I,
                        "key.categories.thatskyinteractions"
                )
        );
    }

    public void register(final RegisterKeyMappingsEvent event) {
        event.register(keyInteractMenu.get());
    }
}
