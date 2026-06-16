package net.quepierts.thatskyinteractions.feature.client.gui.component.control;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.quepierts.thatskyinteractions.core.property.FloatProperty;
import net.quepierts.thatskyinteractions.core.transition.BooleanTransition;
import net.quepierts.thatskyinteractions.feature.client.gui.component.attribute.AttributeKey;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.ease.Eases;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RadioButton extends Button {

    public static final AttributeKey<FloatProperty> ATTRIBUTE_SELECTED_DURATION
            = new AttributeKey<>("selected_duration");
    public static final AttributeKey<BooleanTransition> ATTRIBUTE_SELECTED_TRANSITION
            = new AttributeKey<>("selected_transition");

    @Getter
    private final FloatProperty     selectedDuration    = new FloatProperty(0.25f);

    @Getter
    private final BooleanTransition selectedTransition  = new BooleanTransition(Eases.LINEAR, this.selectedDuration);

    @Setter(AccessLevel.PRIVATE)
    private @Nullable Group         group;

    public RadioButton(
            final TweenScope    tween,
            final int           x,
            final int           y,
            final int           width,
            final int           height,
            final Component     message
    ) {
        super(tween, x, y, width, height, message);

        this.setAttribute(ATTRIBUTE_SELECTED_DURATION, this.selectedDuration);
        this.setAttribute(ATTRIBUTE_SELECTED_TRANSITION, this.selectedTransition);
    }

    @Override
    public void onClick(
            final @NonNull MouseButtonEvent event,
            final boolean                   doubleClick
    ) {
        super.onClick(event, doubleClick);

        final var trigger = this.getActivationTrigger().get();
        if (this.group != null
                && (trigger == ActivationTrigger.CLICKED && !doubleClick
                    || trigger == ActivationTrigger.DOUBLE_CLICKED && doubleClick)) {
            this.group.select(this);
        }
    }

    @Override
    public void onRelease(
            final @NonNull MouseButtonEvent event
    ) {
        super.onRelease(event);

        final var group = this.group;
        if (group != null
                && this.getActivationTrigger().get() == ActivationTrigger.RELEASED) {
            group.select(this);
        }
    }

    public boolean isSelected() {
        return this.selectedTransition.getTarget();
    }

    private void setSelected(final boolean selected) {
        this.selectedTransition.update(this.tween(), selected);
    }

    public static final class Group {

        private final @NonNull  List<RadioButton>   buttons         = new ArrayList<>();

        @Getter
        private       @Nullable RadioButton         selected;

        public void add(
                final @NonNull RadioButton button
        ) {

            this.buttons    .add(button);
            button          .setGroup(this);

        }

        public void select(
                final @NonNull RadioButton button
        ) {

            final var index     = this.buttons.indexOf(button);

            if (index           == -1) {
                return;
            }

            if (this.selected   == button) {
                return;
            }

            if (this.selected   != null) {
                this.selected   .setSelected(false);
            }

            this.selected       = button;
            button              .setSelected(true);

        }

        public void select(
                final int index
        ) {
            if (index < 0 || index >= this.buttons.size()) {
                return;
            }

            this.select(this.buttons.get(index));
        }

        public void clearSelection() {
            if (this.selected   != null) {
                this.selected   .setSelected(false);
                this.selected   = null;
            }
        }

    }

}
