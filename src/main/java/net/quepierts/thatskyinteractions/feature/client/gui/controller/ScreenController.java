package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class ScreenController<Model> {

    protected final Model model;

    public void onTick(float delta) { }

}
