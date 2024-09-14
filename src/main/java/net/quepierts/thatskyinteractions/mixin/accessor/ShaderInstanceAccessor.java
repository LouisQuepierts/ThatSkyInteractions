package net.quepierts.thatskyinteractions.mixin.accessor;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShaderInstance.class)
public interface ShaderInstanceAccessor {
    @Accessor("uniforms")
    List<Uniform> tsi$getUniforms();
}
