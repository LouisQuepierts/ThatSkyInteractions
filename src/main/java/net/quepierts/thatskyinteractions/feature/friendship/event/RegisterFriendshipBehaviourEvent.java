package net.quepierts.thatskyinteractions.feature.friendship.event;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.neoforged.bus.api.Event;
import net.quepierts.thatskyinteractions.feature.friendship.behaviour.FriendshipBehaviour;

@RequiredArgsConstructor
public final class RegisterFriendshipBehaviourEvent extends Event {

    private final ImmutableMap.Builder<String, FriendshipBehaviour> builder;

    public void register(
            @NonNull final String               type,
            @NonNull final FriendshipBehaviour  behaviour
    ) {
        this.builder.put(type, behaviour);
    }

}
