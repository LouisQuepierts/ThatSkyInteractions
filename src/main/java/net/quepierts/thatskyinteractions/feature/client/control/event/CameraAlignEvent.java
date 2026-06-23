package net.quepierts.thatskyinteractions.feature.client.control.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;

@Getter
@RequiredArgsConstructor
public final class CameraAlignEvent extends Event {

    private final Player    entity;
    private final float     partialTick;

}
