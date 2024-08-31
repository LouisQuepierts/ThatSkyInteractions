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
    private Path root;

    public void saveAndClear() {
        this.dataMap.forEach(this::save);
        this.dataMap.clear();
    }

    private void save(UUID uuid, TSIUserData data) {
        if (data == null)
            return;

        try {
            Path directory = this.root;
            File directoryFile = directory.toFile();

            if (!directoryFile.exists()) {
                directoryFile.mkdirs();
            }

            Path temp = Files.createTempFile(directory, uuid + "-", ".dat");
            Path path = directory.resolve(uuid + ".dat");
            Path old = directory.resolve(uuid + "_old.dat");

            CompoundTag tag = new CompoundTag();
            TSIUserData.toNBT(tag, data);

            NbtIo.writeCompressed(tag, temp);
            Util.safeReplaceFile(path, temp, old);
        } catch (IOException e) {
            LOGGER.warn("Fail to save player tsi data for {}", uuid, e);
        }
    }

    public void onSaveToFile(final PlayerEvent.SaveToFile event) {
        Player player = event.getEntity();
        UUID uuid = player.getUUID();
        TSIUserData data = this.dataMap.get(uuid);
        this.save(uuid, data);
    }

    public void onLoadFromFile(final PlayerEvent.LoadFromFile event) {
        Player player = event.getEntity();
        UUID uuid = player.getUUID();


        if (this.dataMap.containsKey(uuid))
            return;

        Optional<TSIUserData> load = this.load(uuid);
        load.ifPresentOrElse(
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

    private Optional<TSIUserData> load(UUID uuid) {
        File directoryFile = this.root.toFile();
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
            this.getUserData(uuid);
            return Optional.empty();
        }

        Optional<CompoundTag> optional = this.load(this.root, uuid, ".dat");

        return optional.or(() -> load(this.root, uuid, "_old.dat"))
                .map(TSIUserData::fromNBT);
    }

    private Optional<CompoundTag> load(Path directory, UUID player, String subfix) {
        File file = directory.resolve(player + subfix).toFile();

        if (file.exists() && file.isFile()) {
            try {
                return Optional.of(NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap()));
            } catch (IOException e) {
                LOGGER.warn("Fail to load player tsi data for{}", player, e);
            }
        }

        return Optional.empty();
    }

    public TSIUserData getUserData(UUID uuid) {
        return this.dataMap.computeIfAbsent(uuid, this::loadOrCreate);
    }

    private TSIUserData loadOrCreate(UUID uuid) {
        Optional<TSIUserData> load = this.load(uuid);
        return load.orElse(TSIUserData.create());
    }

    public void litLight(UUID sender, UUID target) {
        this.getUserData(sender).sendLight(target);
        this.getUserData(target).awardLight(sender);
    }

    public void setRootPath(Path root) {
        this.root = root.getParent().resolve(ThatSkyInteractions.MODID + "/userdata");
    }
}
