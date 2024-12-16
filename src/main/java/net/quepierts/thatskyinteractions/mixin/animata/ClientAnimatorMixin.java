package net.quepierts.thatskyinteractions.mixin.animata;

import net.quepierts.simpleanimator.api.animation.ModelBone;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumMap;

@Mixin(ClientAnimator.class)
public class ClientAnimatorMixin {
    @Shadow @Final private EnumMap<ModelBone, ClientAnimator.Cache> cache;

    /**
     * @author Louis_Quepierts
     * @reason Wrong camera position calculation
     */
    @Overwrite
    public Vector3f getCameraPosition() {
        ClientAnimator.Cache head = cache.get(ModelBone.HEAD);
        ClientAnimator.Cache root = cache.get(ModelBone.ROOT);

        Matrix4f mat = new Matrix4f()
                .rotateXYZ(root.rotation())
                .translate(root.position())
                .translate(0, 12, 0);


        return mat
                .translate(0, 12, 0)
                .translate(head.position())
                .invert()
                .transformPosition(new Vector3f(0, 0, 0))
                .add(0, 24, 0)
                .div(16.0f, -16.0f, 16.0f);
    }
}
