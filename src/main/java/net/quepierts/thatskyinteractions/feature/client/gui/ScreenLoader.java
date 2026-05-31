package net.quepierts.thatskyinteractions.feature.client.gui;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ScreenController;
import net.quepierts.thatskyinteractions.feature.client.gui.layer.AnimatableScreenLayer;
import net.quepierts.thatskyinteractions.feature.client.gui.screen.AnimatableScreen;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Slf4j
@UtilityClass
public class ScreenLoader {

    @SuppressWarnings("unchecked")
    public static <
            Model,
            View extends AnimatableScreen<Model, ?>>
    @Nullable View open(
            final Class<View> type,
            final Model model
    ) {
        final Constructor<?> constructor = type.getConstructors()[0];

        try {
            final var view      = (View) constructor.newInstance(model);
            final var minecraft = Minecraft.getInstance();

            minecraft.pushGuiLayer(view);
            minecraft.mouseHandler.releaseMouse();

            AnimatableScreenLayer.INSTANCE.push(view);

            return view;

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to open screen", e);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <
            View extends AnimatableScreen<Void, ?>>
    @Nullable View open(
            final Class<View> type
    ) {
        final Constructor<?> constructor = type.getConstructors()[0];

        try {
            final var view      = (View) constructor.newInstance();
            final var minecraft = Minecraft.getInstance();

            minecraft.pushGuiLayer(view);
            minecraft.mouseHandler.releaseMouse();

            AnimatableScreenLayer.INSTANCE.push(view);

            return view;

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to open screen", e);
        }

        return null;
    }
}
