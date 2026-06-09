package net.quepierts.thatskyinteractions.feature.client.control.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.Event;

@Getter
@Setter
@AllArgsConstructor
public final class ComputeCameraPositionEvent extends Event {

    private final Entity    entity;
    private double          x;
    private double          y;
    private double          z;


}
