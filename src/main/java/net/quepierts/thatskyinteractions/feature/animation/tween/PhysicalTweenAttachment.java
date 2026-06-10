package net.quepierts.thatskyinteractions.feature.animation.tween;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.quepierts.thatskyinteractions.feature.registry.AttachmentTypes;
import net.quepierts.thatskyinteractions.infra.animation.tween.Tween;
import net.quepierts.thatskyinteractions.infra.animation.tween.TweenScope;
import net.quepierts.thatskyinteractions.infra.animation.tween.backend.TweenTickHandler;
import org.jspecify.annotations.NonNull;

public final class PhysicalTweenAttachment {

    private final TweenScope        tween       = Tween.create();
    private final TweenTickHandler  handler     = this.tween instanceof TweenTickHandler ? (TweenTickHandler) this.tween : null;

    public static PhysicalTweenAttachment getAttachment(@NonNull Level level) {
        return level.getData(AttachmentTypes.PHYSICAL_TWEEN);
    }

    public static PhysicalTweenAttachment getAttachment(@NonNull Player player) {
        return player.getData(AttachmentTypes.PHYSICAL_TWEEN);
    }

    public static TweenScope tween(@NonNull Level level) {
        return PhysicalTweenAttachment.getAttachment(level).tween();
    }

    public void tick(float delta) {
        if (this.handler != null) {
            this.handler.tick(delta);
        }
    }

    public TweenScope tween() {
        return this.tween;
    }

}
