package net.quepierts.thatskyinteractions.client.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;

public class WingOfLightBlockEntityRenderer implements BlockEntityRenderer<WingOfLightBlockEntity> {
    @Override
    public void render(WingOfLightBlockEntity wingOfLightBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {


        Entity camera = Minecraft.getInstance().getCameraEntity();


    }
}
