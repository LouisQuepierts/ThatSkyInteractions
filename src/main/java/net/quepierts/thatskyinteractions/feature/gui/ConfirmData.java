package net.quepierts.thatskyinteractions.feature.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record ConfirmData(
        @NonNull Identifier     icon,
        @NonNull Component[]    message,

        Runnable                confirm,
        Runnable                cancel
) {

    public ConfirmData {

        if (message.length == 0) {
            throw new IllegalArgumentException("message cannot be empty");
        }

        confirm = confirm == null ? DEFAULT : confirm;
        cancel  = cancel  == null ? DEFAULT : cancel;
    }

    public static final Runnable DEFAULT = () -> {};

}
