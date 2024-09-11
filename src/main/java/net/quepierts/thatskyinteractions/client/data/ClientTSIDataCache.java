package net.quepierts.thatskyinteractions.client.data;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.SimpleAnimator;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.block.entity.WingOfLightBlockEntity;
import net.quepierts.thatskyinteractions.client.gui.layer.World2ScreenWidgetLayer;
import net.quepierts.thatskyinteractions.data.PlayerPair;
import net.quepierts.thatskyinteractions.data.RelationshipSavedData;
import net.quepierts.thatskyinteractions.data.TSIUserData;
import net.quepierts.thatskyinteractions.data.astrolabe.FriendAstrolabeInstance;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import net.quepierts.thatskyinteractions.data.tree.InteractTreeInstance;
import net.quepierts.thatskyinteractions.network.packet.BatchRelationshipPacket;
import net.quepierts.thatskyinteractions.network.packet.PickupWingOfLightPacket;
import net.quepierts.thatskyinteractions.network.packet.UserDataModifyPacket;
import net.quepierts.thatskyinteractions.network.packet.astrolabe.AstrolabeOperationPacket;
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
        ClientLevel level = Minecraft.getInstance().level;

        if (level == null) {
            return;
        }

        List<AbstractClientPlayer> players = level.players();
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

        /*for (int i = 0; i < 11; i++) {
            UUID uuid = UUID.randomUUID();
            this.addFriend(uuid);

            if (ThatSkyInteractions.RANDOM.nextBoolean()) {
                this.likeFriend(uuid, false);
            }
        }*/
    }

    public InteractTreeInstance get(UUID other) {
        LocalPlayer player = Minecraft.getInstance().player;
        PlayerPair pair = new PlayerPair(other, player.getUUID());
        return relationship.computeIfAbsent(other, key -> new InteractTreeInstance(pair, tree, RelationshipSavedData.FRIEND_INTERACT_TREE));

    }
    @Nullable
    public InteractTree getTree() {
        return this.tree;
    }

    @Nullable
    public TSIUserData getUserData() {
        return this.userData;
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
        if (this.userData == null) {
            return false;
        }

        return this.userData.isFriend(player);
    }

    public boolean isLikedFriend(UUID player) {
        if (this.userData == null) {
            return false;
        }

        return this.userData.isLiked(player);
    }

    public void setOnline(Player player, boolean online) {
        if (this.userData == null) {
            return;
        }

        Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> pair = this.userData.cache().get(player.getUUID());
        if (pair == null) {
            return;
        }

        FriendAstrolabeInstance.NodeData data = pair.getFirst();
        if (data != null) {
            data.setFlag(FriendAstrolabeInstance.Flag.ONLINE, online);
        }
    }

    @Nullable
    private Pair<FriendAstrolabeInstance.NodeData, ResourceLocation> addFriend(UUID uuid) {
        if (this.userData == null) {
            return null;
        }

        return this.userData.addFriend(uuid);
    }

    public void likeFriend(UUID uuid, boolean update) {
        if (this.userData == null) {
            return;
        }
        if (this.userData.likeFriend(uuid) && update) {
            SimpleAnimator.getNetwork().update(new UserDataModifyPacket.Like(uuid));
        }
    }

    public void unlikeFriend(UUID uuid, boolean update) {
        if (this.userData == null) {
            return;
        }
        
        if (this.userData.unlikeFriend(uuid) && update) {
            SimpleAnimator.getNetwork().update(new UserDataModifyPacket.Unlike(uuid));
        }
    }

    public void pickupWingOfLight(WingOfLightBlockEntity wol, boolean update) {
        TSIUserData data = this.userData;

        if (data == null) {
            return;
        }

        if (!data.isPickedUp(wol)) {
            UUID uuid = wol.getUUID();
            data.pickupWingOfLight(uuid);
            World2ScreenWidgetLayer.INSTANCE.remove(uuid);

            if (update) {
                SimpleAnimator.getNetwork().update(new PickupWingOfLightPacket(wol));
            }
        }
    }

    public void sendLight(UUID target, boolean update) {
        if (this.userData == null) {
            return;
        }

        if (this.userData.sendLight(target) && update) {
            SimpleAnimator.getNetwork().update(new AstrolabeOperationPacket.Ignite(target));
        }
    }

    public void awardLight(UUID target) {
        if (this.userData == null) {
            return;
        }

        this.userData.awardLight(target);
    }

    public void gainLight(UUID uuid, boolean update) {
        if (this.userData == null) {
            return;
        }

        if (this.userData.gainLight(uuid) && update) {
            SimpleAnimator.getNetwork().update(new AstrolabeOperationPacket.Gain(uuid));
        }
    }

    public boolean unprepared() {
        return this.userData == null;
    }
}
