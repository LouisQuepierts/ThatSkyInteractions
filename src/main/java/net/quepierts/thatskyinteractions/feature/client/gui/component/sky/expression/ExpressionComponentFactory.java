package net.quepierts.thatskyinteractions.feature.client.gui.component.sky.expression;

import dev.anvilcraft.lib.v2.rendering.sdf.SdfGraphics;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.core.model.ui.Alignment;
import net.quepierts.thatskyinteractions.feature.client.ClientPlayerCallSystem;
import net.quepierts.thatskyinteractions.feature.client.gui.ColorStack;
import net.quepierts.thatskyinteractions.feature.client.gui.ExtendedGuiGraphics;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Button;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.Control;
import net.quepierts.thatskyinteractions.feature.client.gui.component.control.RadioButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.GridPane;
import net.quepierts.thatskyinteractions.feature.client.gui.component.layout.Pane;
import net.quepierts.thatskyinteractions.feature.client.gui.component.sky.TsiButton;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.GeneralVisualNodes;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.VisualNode;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonRenderOps;
import net.quepierts.thatskyinteractions.feature.client.gui.component.visual.button.ButtonVisualNodes;
import net.quepierts.thatskyinteractions.feature.client.gui.controller.ExpressionScreenController;
import net.quepierts.thatskyinteractions.feature.expression.ExpressionSet;
import net.quepierts.thatskyinteractions.feature.expression.PlayerExpressionManager;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceType;
import net.quepierts.thatskyinteractions.feature.expression.call.PlayerVoiceTypeManager;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import org.jspecify.annotations.NonNull;

@UtilityClass
public class ExpressionComponentFactory {

    public static Pane createExpressions(
            final @NonNull TweenScope tween,
            final @NonNull ExpressionScreenController controller
    ) {

        final var grid          = ExpressionComponentFactory.grid(tween);

        final var manager       = PlayerExpressionManager.getInstance();
        final var expressions   = manager.ordinal();
        final var maxColumn     = 4;
        var row                 = 0;
        var column              = 0;
        var i                   = 0;

        for (final var identifier : expressions) {

            final var id    = i;
            final var set   = manager.getSet(identifier);

            if (set == null) { // normally, this should never happen
                continue;
            }

            final var button = new ExpressionButton(
                    tween,
                    Component.empty(),
                    set.levels()
            );

            button.getActivationTrigger().set(Button.ActivationTrigger.RELEASED);
            button.setVisualNode(vExpressionButton(set));
            button.setOnClick(() -> {
                Minecraft.getInstance()
                        .getSoundManager()
                        .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                controller.onExpressionClicked(id, button.getSelected());
            });

            // calculate row and column
            if (column == maxColumn) {
                row++;
                column = 0;
            }

            grid.add(
                    button,
                    column,
                    row
            );

            column++;
            i++;
        }

        grid.fit();

        return grid;
    }

    public static Pane createVoices(
            final @NonNull TweenScope                   tween,
            final @NonNull ExpressionScreenController   controller
    ) {
        final var grid          = ExpressionComponentFactory.grid(tween);
        final var manager       = PlayerVoiceTypeManager.getInstance();
        final var voices        = manager.ordinal();

        final var preferred     = ClientPlayerCallSystem.getPreferredVoice();

        final var group         = new RadioButton.Group();
        final var maxColumn     = 4;
        var row                 = 0;
        var column              = 0;
        var i                   = 0;

        for (final var identifier : voices) {

            final var type      = manager.getNonNull(identifier);

            final var button    = TsiButton.create(
                    RadioButton::new,
                    tween,
                    Component.empty(),
                    0, 0
            );
            button.getActivationTrigger().set(Button.ActivationTrigger.RELEASED);
            button.setVisualNode(vVoiceButton(type));
            button.setOnClick(() -> {
                Minecraft.getInstance()
                        .getSoundManager()
                        .play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
                controller.onVoiceClicked(identifier);
            });
            group.add(button);

            if (type == preferred) {
                group.select(button);
            }

            if (column == maxColumn) {
                row++;
                column = 0;
            }

            grid.add(
                    button,
                    column,
                    row
            );

            column++;
            i++;

        }

        grid.fit();

        return grid;
    }

    private static GridPane grid(
            final @NonNull TweenScope                   tween
    ) {
        final var grid = new GridPane(
                tween,
                0, 0,
                0, 0,
                Component.empty()
        );
        grid.setAlignment(Alignment.TOP_CENTER);
        grid.setHgap(4);
        grid.setVgap(4);
        grid.getPadding().set(4, 8);

        return grid;
    }

    private static final Identifier ICON = ThatSkyInteractions.location("none");

    private static VisualNode vExpressionButton(
            final @NonNull ExpressionSet set
    ) {
        final var icon          = set.icon();
        final var select        = set.leveled() ? new VisualNode() {
            @Override
            public void extractRenderState(
                    final @NonNull Control              control,
                    final @NonNull ExtendedGuiGraphics  graphics,
                    final @NonNull ColorStack           colors,
                    final @NonNull TweenScope           tween,
                    final          int                  mouseX,
                    final          int                  mouseY,
                    final          float                delta
            ) {


                final var levels    = control.getAttribute(ExpressionButton.ATTRIBUTE_LEVELS).getAsInt();
                final var selected  = control.getAttribute(ExpressionButton.ATTRIBUTE_SELECTED).getAsInt() - 1;
                final var press     = control.getAttribute(Button.ATTRIBUTE_PRESS_TRANSITION);
                final var pressed   = press.getTarget();

                final var top       = -12 - press.getValue() * 4f;
                final var width     = levels * 4 - 2;
                final var left      = (width) / -2f;

                if (pressed) {
                    final var scale = 1.0f + press.getValue() * 0.25f;
                    graphics.pose().scale(scale, scale);
                }

                graphics.pose().translate(left, top);

                for (var i = 0; i < levels; i++) {
                    graphics.original().fill(
                            i * 4,
                            0,
                            i * 4 + 2,
                            2,
                            pressed && i == selected
                                    ? colors.argb(0xff, 0xff, 0xfe, 0xe0)
                                    : colors.argb(0xff, 0x80, 0x80, 0x80)
                    );
                }
            }
        } : VisualNode.EMPTY;

        return vButton(icon, select);
    }

    private static VisualNode vVoiceButton(
            final @NonNull PlayerVoiceType type
    ) {

        final var icon      = type.getIcon();
        final var selected  = new VisualNode() {
            @Override
            public void extractRenderState(
                    final @NonNull Control              control,
                    final @NonNull ExtendedGuiGraphics  graphics,
                    final @NonNull ColorStack           colors,
                    final @NonNull TweenScope           tween,
                    final          int                  mouseX,
                    final          int                  mouseY,
                    final          float                delta
            ) {
                final var transition    = control.getAttribute(RadioButton.ATTRIBUTE_SELECTED_TRANSITION);
                final var value         = transition.getValue();
                if (value == 0.0f) {
                    return;
                }

                final var width         = 32;
                final var height        = 32;

                colors.push();
                colors.mul(value, 1.0f, 1.0f, 1.0f);

                SdfGraphics.getInstance()
                        .reset()
                        .color(colors.argb(0xff, 0xc8, 0xc5, 0xb4))
                        .triangleIsosceles(0, 12.75f, 10, -3)
                        .fill()
                        .draw(graphics.original())
                        .center(true)
                        .round(4.5f)
                        .box(
                                0, 0,
                                width - 3,
                                height - 3
                        )
                        .stroke(2.0f)
                        .draw(graphics.original());

                colors.pop();
            }
        };

        return vButton(icon, selected);

    }

    private static VisualNode vButton(
            final @NonNull Identifier   icon,
            final @NonNull VisualNode   special
    ) {

        final var background    = GeneralVisualNodes.lBase(0x80000000, 6.0f);
        final var content       = ButtonVisualNodes.spin((graphics, colors, _, _, width, height) -> {
            graphics.blitIcon(
                    icon,
                    -14,
                    -14,
                    (int) width - 4,
                    (int) height - 4,
                    colors.argb()
            );
        });

        final var hover     = ButtonVisualNodes.hover(ButtonRenderOps.HOVER);
        return VisualNode.combine(
                ButtonVisualNodes::base,
                background, content, special, hover
        );

    }

}
