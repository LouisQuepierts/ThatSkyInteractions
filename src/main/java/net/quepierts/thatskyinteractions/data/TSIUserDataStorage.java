package net.quepierts.thatskyinteractions.data;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.network.packet.UserDataSync;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class TSIUserDataStorage {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Object2ObjectMap<UUID, TSIUserData> dataMap = new Object2ObjectOpenHashMap<>();

    private boolean mkdir = true;

    public void onSaveToFile(final PlayerEvent.SaveToFile event) {
        Player player = event.getEntity();
        try {
            Path directory = this.getDirectoryPath(event.getPlayerDirectory());

            if (this.mkdir) {
                directory.toFile().mkdirs();
                this.mkdir = false;
            }

            String uuid = event.getPlayerUUID();
            Path temp = Files.createTempFile(directory, uuid + "-", ".dat");
            Path path = directory.resolve(uuid + ".dat");
            Path old = directory.resolve(uuid + "_old.dat");

            CompoundTag tag = new CompoundTag();
            TSIUserData userData = this.getUserData(player.getUUID());
            TSIUserData.toNBT(tag, userData);

            NbtIo.writeCompressed(tag, temp);
            Util.safeReplaceFile(path, temp, old);
            ThatSkyInteractions.LOGGER.info(event.getPlayerDirectory().getPath());
        } catch (IOException e) {
            LOGGER.warn("Fail to save player tsi data for {}", player.getName().getString());
        }
    }

    public void onLoadFromFile(final PlayerEvent.LoadFromFile event) {
        Player player = event.getEntity();
        Path directory = getDirectoryPath(event.getPlayerDirectory());

        UUID uuid = player.getUUID();
        if (this.mkdir) {
            directory.toFile().mkdirs();
            this.mkdir = false;
            this.getUserData(uuid);
            return;
        }

        Optional<CompoundTag> optional = this.load(directory, player, ".dat");

       optional.or(() -> load(directory, player, "_old.dat"))
               .map(TSIUserData::fromNBT)
               .ifPresentOrElse(
                       (data -> this.dataMap.put(uuid, data)),
                       () -> this.getUserData(uuid)
               );
    }

    public void sync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player != null) {
            SimpleAnimator.getNetwork().sendToPlayer(
                    new UserDataSync(this.getUserData(player.getUUID())),
                    player
            );
        } else {
            for (ServerPlayer splayer : event.getPlayerList().getPlayers()) {
                SimpleAnimator.getNetwork().sendToPlayer(
                        new UserDataSync(this.getUserData(splayer.getUUID())),
                        splayer
                );
            }
        }
    }

    private Optional<CompoundTag> load(Path directory, Player player, String subfix) {
        File file = directory.resolve(player.getStringUUID() + subfix).toFile();

        if (file.exists() && file.isFile()) {
            try {
                return Optional.of(NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap()));
            } catch (IOException e) {
                LOGGER.warn("Fail to load player tsi data for{}", player.getName().getString(), e);
            }
        }

        return Optional.empty();
    }

    public TSIUserData getUserData(UUID uuid) {
        return this.dataMap.computeIfAbsent(uuid, TSIUserData::create);
    }

    private Path getDirectoryPath(File directory) {
        File parent = directory.getParentFile();
        return new File(parent, ThatSkyInteractions.MODID + "/userdata").toPath();
    }

    public void litLight(UUID sender, UUID target) {
        this.getUserData(sender).sendLight(target);
        this.getUserData(target).awardLight(sender);
    }
}
