package net.quepierts.thatskyinteractions.feature.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.quepierts.thatskyinteractions.core.model.ui.Alignment;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.*;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.expression.ExpressionButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.expression.ExpressionComponentFactory;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.GeneralVisualNodes;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonRenderOps;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonVisualNodes;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ExpressionScreenController;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionSet;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionManager;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

public final class ExpressionsScreen extends SlideScreen<Void, ExpressionScreenController> {

    public ExpressionsScreen() {
        super(Component.translatable("menu.thatskyinteractions.expressions"), null);
    }

    @Override
    protected ExpressionScreenController createController(final Void unused) {
        return new ExpressionScreenController();
    }

    @Override
    protected Control createView() {

        var controller          = this.getController();
        var tween               = this.tween();
        var scroll              = new VScrollPane(
                                    tween,
                                    0, 0,
                                    this.getSliderWide().get(),
                                    this.height,
                                    Component.empty()
                                );
        final var padding       = scroll.getPadding();
        padding.top             = 16;

        var vbox                = new VBox(
                                    tween,
                                    0, 0,

                                    this.getSliderWide().get(),
                                    this.height,
                                    Component.empty()
                                );

        vbox                    .setAlignment(Alignment.TOP_CENTER);

        scroll                  .getScrollSpeed().set(16.0f);

        final var voices        = ExpressionComponentFactory.createVoices(tween, controller);
        final var expressions   = ExpressionComponentFactory.createExpressions(tween, controller);

        scroll                  .addChild(voices);
        scroll                  .addChild(expressions);
        scroll                  .fit();

        vbox                    .addChild(scroll);
        vbox                    .layout();

        return vbox;
    }




    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
