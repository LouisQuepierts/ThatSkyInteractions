package net.quepierts.thatskyinteractions.common.registry;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.common.data.attachment.UserDataAttachment;

public class AttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(
            NeoForgeRegistries.ATTACHMENT_TYPES, ThatSkyInteractions.MODID
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<UserDataAttachment>> USER_DATA = REGISTER.register(
            "user_data",
            () -> AttachmentType.builder(UserDataAttachment::new)
                    .serialize(UserDataAttachment.CODEC)
                    .build()
    );
}
