package net.quepierts.thatskyinteractions.feature.registry;

import lombok.experimental.UtilityClass;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;
import net.quepierts.thatskyinteractions.feature.registry.builer.FriendshipBehaviourBuilder;
import net.quepierts.thatskyinteractions.feature.registry.entry.FriendshipBehaviourEntry;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

@UtilityClass
public class FriendshipBehaviours {


    public static void register() { }

    private static <T extends FriendshipBehaviour> FriendshipBehaviourEntry<T> create(
            final @NonNull String       name,
            final @NonNull Supplier<T>  supplier
    ) {
        return ThatSkyInteractions.REGISTRUM.entry(
                name,
                callback -> new FriendshipBehaviourBuilder<>(
                        ThatSkyInteractions.REGISTRUM,
                        ThatSkyInteractions.REGISTRUM,
                        name,
                        callback,
                        supplier
                )
        ).register();
    }
}
