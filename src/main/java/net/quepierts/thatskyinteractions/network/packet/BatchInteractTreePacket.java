package net.quepierts.thatskyinteractions.network.packet;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.tree.InteractTree;
import org.jetbrains.annotations.NotNull;

public class BatchInteractTreePacket implements ISync {
    public static final Type<BatchInteractTreePacket> TYPE = NetworkPackets.createType(BatchInteractTreePacket.class);
    private final Object2ObjectMap<ResourceLocation, InteractTree> map;

    public BatchInteractTreePacket(FriendlyByteBuf byteBuf) {
        this.map = byteBuf.readMap(
                Object2ObjectOpenHashMap::new,
                FriendlyByteBuf::readResourceLocation,
                InteractTree::fromNetwork
        );
    }

    public BatchInteractTreePacket(Object2ObjectMap<ResourceLocation, InteractTree> interactTrees) {
        ThatSkyInteractions.LOGGER.info("Update Trees");
        this.map = interactTrees;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void sync() {
        ThatSkyInteractions.getInstance().getProxy().getInteractTreeManager().handleUpdateInteractTree(this);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeMap(
                this.map,
                FriendlyByteBuf::writeResourceLocation,
                InteractTree::toNetwork
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public Object2ObjectMap<ResourceLocation, InteractTree> getInteractTrees() {
        return new Object2ObjectOpenHashMap<>(this.map);
    }
}
