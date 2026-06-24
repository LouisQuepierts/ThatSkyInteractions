package net.quepierts.thatskyinteractions.feature.control;

import lombok.experimental.UtilityClass;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;

@UtilityClass
public class AvatarDimensions {

    public static final EntityDimensions    RIDING_DIMENSIONS
            = EntityDimensions
            .scalable(0.6F, 1.5F)
            .withEyeHeight(1.5F)
            .withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, Avatar.DEFAULT_VEHICLE_ATTACHMENT));

}
