package net.quepierts.thatskyinteractions.feature.client.gui.controller;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class ScreenController<Model> {

    protected final Model model;

}
