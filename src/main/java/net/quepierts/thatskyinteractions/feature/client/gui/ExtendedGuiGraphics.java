package net.quepierts.thatskyinteractions.feature.client.gui;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.quepierts.thatskyinteractions.feature.client.reference.TsiAtlases;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

public final class ExtendedGuiGraphics {

    private final GuiGraphicsExtractor  graphics;
    private final TextureAtlas          iconAtlas;

    public ExtendedGuiGraphics(final @NonNull GuiGraphicsExtractor graphics) {
        this.graphics = graphics;

        final var minecraft = Minecraft.getInstance();
        this.iconAtlas = minecraft.getAtlasManager().getAtlasOrThrow(TsiAtlases.ICONS);
    }

    public void blitIcon(
            final Identifier identifier,
            final int x,
            final int y,
            final int width,
            final int height
    ) {

        final var sprite = this.iconAtlas.getSprite(identifier);

        this.graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                sprite,
                x,
                y,
                width,
                height
        );
    }

    public void blitIcon(
            final Identifier identifier,
            final int x,
            final int y,
            final int width,
            final int height,
            final int color
    ) {

        final var sprite = this.iconAtlas.getSprite(identifier);

        this.graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                sprite,
                x,
                y,
                width,
                height,
                color
        );
    }

    public Matrix3x2fStack pose() {
        return this.graphics.pose();
    }

    public GuiGraphicsExtractor original() {
        return this.graphics;
    }

}
