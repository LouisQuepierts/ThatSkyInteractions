package net.quepierts.thatskyinteractions.network.packet;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.quepierts.simpleanimator.core.network.ISync;
import net.quepierts.simpleanimator.core.network.NetworkPackets;
import net.quepierts.thatskyinteractions.ThatSkyInteractions;
import net.quepierts.thatskyinteractions.data.astrolabe.Astrolabe;
import org.jetbrains.annotations.NotNull;

public class BatchAstrolabePacket implements ISync {
    public static final Type<BatchAstrolabePacket> TYPE = NetworkPackets.createType(BatchAstrolabePacket.class);

    private final Object2ObjectMap<ResourceLocation, Astrolabe> map;

    public BatchAstrolabePacket(FriendlyByteBuf byteBuf) {
        this.map = byteBuf.readMap(
                Object2ObjectOpenHashMap::new,
                FriendlyByteBuf::readResourceLocation,
                Astrolabe::fromNetwork
        );
    }

    public BatchAstrolabePacket(Object2ObjectMap<ResourceLocation, Astrolabe> map) {
        ThatSkyInteractions.LOGGER.info("Update Astrolabes");
        this.map = map;
    }

    @Override
    public void sync() {
        ThatSkyInteractions.getInstance().getProxy().getAstrolabeManager().handleUpdateAstrolabe(this);
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeMap(
                this.map,
                FriendlyByteBuf::writeResourceLocation,
                Astrolabe::toNetwork
        );
    }

    public Object2ObjectMap<ResourceLocation, Astrolabe> getAstrolabes() {
        return map;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
