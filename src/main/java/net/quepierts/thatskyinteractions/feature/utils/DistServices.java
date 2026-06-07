package net.quepierts.thatskyinteractions.feature.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.quepierts.thatskyinteractions.infra.Services;
import org.jspecify.annotations.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessFlag;

@Slf4j
@UtilityClass
public class DistServices {

    @SuppressWarnings("unchecked")
    public static <T> T load(
            @NonNull final Dist     dist,
            @NonNull final Class<T> clazz
    ) {
        // if dist is not the same as current dist
        if (FMLLoader.getCurrent().getDist() != dist) {
            // look for default implementation
            try {
                for (final var field : clazz.getDeclaredFields()) {
                    final var flags = field.accessFlags();
                    if (!flags.contains(AccessFlag.STATIC)) {
                        continue;
                    }

                    if (!field.isAnnotationPresent(Default.class)) {
                        continue;
                    }

                    field.setAccessible(true);
                    return (T) field.get(null);
                }
            } catch (Exception e) {
                log.warn("Failed to load default implementation for {}", clazz);
                throw new IllegalStateException();
            }
        }

        // load service
        return Services.load(clazz);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Default {

    }

}
