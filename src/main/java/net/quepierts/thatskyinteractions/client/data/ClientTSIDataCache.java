package net.quepierts.thatskyinteractions.client.data;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.network.packet.BatchRelationshipPacket;
import net.quepierts.thatskyinteractions.network.packet.UserDataModifyPacket;
import net.quepierts.thatskyinteractions.network.packet.astrolabe.AstrolabeIgnitePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class ClientTSIDataCache {
    @NotNull private final Object2ObjectMap<UUID, InteractTreeInstance> relationship;
    @Nullable private InteractTree tree;
    @Nullable private TSIUserData userData;

    public ClientTSIDataCache() {
        this.relationship = new Object2ObjectOpenHashMap<>();
    }

    public void clear() {
        this.relationship.clear();
        this.tree = null;
        this.userData = null;
    }

    public void handleUpdateRelationships(BatchRelationshipPacket batchRelationshipPacket) {
        ThatSkyInteractions.LOGGER.info("Update Relationships");
        this.tree = ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().get(RelationshipSavedData.FRIEND_INTERACT_TREE);
        this.relationship.putAll(batchRelationshipPacket.getRelationships());
    }

    public void handleUpdateUserData(TSIUserData userData) {
        ThatSkyInteractions.LOGGER.info("Update UserData");
        List<AbstractClientPlayer> players = Minecraft.getInstance().level.players();
        Set<UUID> uuids = new HashSet<>(players.size());
        for (AbstractClientPlayer player : players) {
            uuids.add(player.getUUID());
        }

        this.userData = userData;
        userData.astrolabes().values().stream()
                .map(FriendAstrolabeInstance::getNodes)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .forEach(data -> {
                    UUID uuid = data.getFriendData().getUuid();
                    data.setFlag(FriendAstrolabeInstance.Flag.ONLINE, uuids.contains(uuid));
                });
    }

    public InteractTreeInstance get(UUID other) {
        PlayerPair pair = new PlayerPair(other, Minecraft.getInstance().player.getUUID());
        return relationship.computeIfAbsent(other, key -> new InteractTreeInstance(pair, tree, RelationshipSavedData.FRIEND_INTERACT_TREE));
    }

    @NotNull
    public InteractTree getTree() {
        return Objects.requireNonNull(this.tree, "Attempted to call getTree before client data have synced.");
    }

    @NotNull
    public TSIUserData getUserData() {
        return Objects.requireNonNull(this.userData, "Attempted to call getUserData before client data have synced.");
    }

    public Object2ObjectMap<UUID, InteractTreeInstance> relationships() {
        return this.relationship;
    }

    public boolean unlocked(UUID player, String node) {
        InteractTreeInstance instance = this.relationship.get(player);
        if (instance == null)
            return false;
        return instance.isUnlocked(node);
    }

    public boolean isFriend(UUID player) {
        return this.getUserData().isFriend(player);
    }

    public boolean isLikedFriend(UUID player) {
        return this.getUserData().isLiked(player);
    }

    public void setOnline(Player player, boolean online) {
        if (player.isLocalPlayer())
            return;

        FriendAstrolabeInstance.NodeData data = this.getUserData().cache().get(player.getUUID()).getFirst();
        if (data != null) {
            data.setFlag(FriendAstrolabeInstance.Flag.ONLINE, online);
        }
    }

    private @Nullable Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(UUID uuid) {
        return this.getUserData().astrolabes().addFriend(uuid);
    }

    public void likeFriend(UUID uuid, boolean update) {
        if (this.getUserData().likeFriend(uuid) && update) {
            SimpleAnimator.getNetwork().update(new UserDataModifyPacket.Like(uuid));
        }
    }

    public void unlikeFriend(UUID uuid, boolean update) {
        if (this.getUserData().unlikeFriend(uuid) && update) {
            SimpleAnimator.getNetwork().update(new UserDataModifyPacket.Unlike(uuid));
        }
    }

    public void sendLight(UUID target, boolean update) {
        if (this.getUserData().sendLight(target) && update) {
            SimpleAnimator.getNetwork().update(new AstrolabeIgnitePacket(target));
        }
    }

    public void awardLight(UUID target) {
        this.getUserData().awardLight(target);
    }
}
