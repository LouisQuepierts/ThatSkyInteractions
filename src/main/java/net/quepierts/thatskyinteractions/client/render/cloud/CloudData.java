package net.quepierts.thatskyinteractions.client.render.cloud;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public record CloudData(Vector3f position, Vector3f size, int cullFlag) {
    public void split(List<CloudData> listIn) {
        int xChunks = (int) Math.ceil(size.x / (float) CloudRenderer.SINGLE_CLOUD_SIZE);
        int yChunks = (int) Math.ceil(size.y / (float) CloudRenderer.SINGLE_CLOUD_SIZE);
        int zChunks = (int) Math.ceil(size.z / (float) CloudRenderer.SINGLE_CLOUD_SIZE);

        for (int i = 0; i < xChunks; i++) {
            for (int j = 0; j < yChunks; j++) {
                for (int k = 0; k < zChunks; k++) {
                    int flag = this.cullFlag;

                    if (i < xChunks - 1) {
                        flag |= CloudRenderer.CULL_XP;
                    }

                    if (i != 0) {
                        flag |= CloudRenderer.CULL_XN;
                    }

                    if (j < yChunks - 1) {
                        flag |= CloudRenderer.CULL_YP;
                    }

                    if (j != 0) {
                        flag |= CloudRenderer.CULL_YN;
                    }

                    if (k < zChunks - 1) {
                        flag |= CloudRenderer.CULL_ZP;
                    }

                    if (k != 0) {
                        flag |= CloudRenderer.CULL_ZN;
                    }

                    if (flag == CloudRenderer.INVISIBLE) {
                        continue;
                    }

                    Vector3f position = new Vector3f(
                            this.position.x + i * CloudRenderer.SINGLE_CLOUD_SIZE,
                            this.position.y + j * CloudRenderer.SINGLE_CLOUD_SIZE,
                            this.position.z + k * CloudRenderer.SINGLE_CLOUD_SIZE
                    );

                    Vector3f size = new Vector3f(
                            Math.min(CloudRenderer.SINGLE_CLOUD_SIZE, this.size.x - i * CloudRenderer.SINGLE_CLOUD_SIZE),
                            Math.min(CloudRenderer.SINGLE_CLOUD_SIZE, this.size.y - j * CloudRenderer.SINGLE_CLOUD_SIZE),
                            Math.min(CloudRenderer.SINGLE_CLOUD_SIZE, this.size.z - k * CloudRenderer.SINGLE_CLOUD_SIZE)
                    );

                    listIn.add(new CloudData(position, size, flag));
                }
            }
        }
    }
}
