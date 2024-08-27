package net.quepierts.thatskyinteractions.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;

public class BlockedPlayerRenderHandler {
    private final ClientProxy client;
    private boolean pushed = false;

    public BlockedPlayerRenderHandler(ClientProxy client) {
        this.client = client;
    }

    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (client.getCache().getUserData().isBlocked(event.getEntity().getUUID())) {
            RenderSystem.setShaderColor(0.1f, 0.1f, 0.1f, 1);
            pushed = true;
        }
    }

    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (pushed) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }
}
