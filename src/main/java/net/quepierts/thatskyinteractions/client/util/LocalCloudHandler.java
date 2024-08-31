package net.quepierts.thatskyinteractions.client.util;

import com.mojang.blaze3d.vertex.VertexBuffer;

public class LocalCloudHandler {
    private VertexBuffer buffer;

    private void generatedVertexBuffer() {
        if (this.buffer != null) {
            this.buffer.close();
        }

        this.buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

    }
}
