package net.quepierts.thatskyinteractions.feature.mixin.vanilla.client.accessor;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientInput.class)
public interface ClientInputAccessor {

    @Accessor("moveVector")
    void a4j$setMoveVector(Vec2 vec);

}
