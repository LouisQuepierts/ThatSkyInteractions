package net.quepierts.thatskyinteractions.client.data;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class BlockedPlayerList extends ObjectArraySet<UUID> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Type type;
    private final String name;
    private static final String DIRECTORY = "thatskyinteraction/blocked";

    private BlockedPlayerList(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public static BlockedPlayerList loadLocal(Type type, String name) {
        Path path = Path.of(DIRECTORY, type.name().toLowerCase(Locale.ROOT), name + ".dat");
        File file = path.toFile();

        BlockedPlayerList list = new BlockedPlayerList(type, name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return list;
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(UUID.fromString(line));
            }
        } catch (IOException e) {
            LOGGER.warn("Error occur during load blocked list");
        }

        return list;
    }

    public void save() {
        Path path = Path.of(DIRECTORY, type.name().toLowerCase(Locale.ROOT), name);
        File file = path.toFile();

        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (UUID uuid : this) {

                writer.write(uuid.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.warn("Error occur during save blocked list", e);
        }
    }

    public enum Type {
        LOCAL,
        SERVER
    }
}
