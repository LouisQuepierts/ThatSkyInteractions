package net.quepierts.thatskyinteractions.infra;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.util.ServiceLoader;

@Slf4j
@UtilityClass
public class Services {

    public static <T> T load(final @NonNull Class<T> clazz) {
        final var loaded    = ServiceLoader.load(clazz, Services.class.getClassLoader())
                            .findFirst()
                            .orElseThrow();

        log.debug("Loaded {} for service {}", loaded, clazz);

        return loaded;
    }

}
