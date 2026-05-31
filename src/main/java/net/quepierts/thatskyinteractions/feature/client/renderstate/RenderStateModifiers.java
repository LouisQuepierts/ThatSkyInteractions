package net.quepierts.thatskyinteractions.feature.client.renderstate;

import lombok.experimental.UtilityClass;
import net.minecraft.util.context.ContextKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import org.jspecify.annotations.NonNull;

@UtilityClass
@EventBusSubscriber(value = Dist.CLIENT, modid = ThatSkyInteractions.MODID)
public class RenderStateModifiers {

    public static <T> ContextKey<T> create(final @NonNull String name) {
        return new ContextKey<>(ThatSkyInteractions.location(name));
    }

    @SubscribeEvent
    public static void onRegisterRenderStateModifiers(final RegisterRenderStateModifiersEvent event) {
        event.registerAvatarEntityModifier(AnimationStateModifier.INSTANCE);
    }

}
