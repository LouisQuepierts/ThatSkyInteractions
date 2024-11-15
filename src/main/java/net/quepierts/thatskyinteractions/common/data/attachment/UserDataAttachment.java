package net.quepierts.thatskyinteractions.common.data.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.quepierts.thatskyinteractions.common.data.attachment.component.AstrolabeComponent;
import net.quepierts.thatskyinteractions.common.data.attachment.component.PickupComponent;
import net.quepierts.thatskyinteractions.common.data.attachment.component.RelationshipComponent;
import net.quepierts.thatskyinteractions.common.registry.AttachmentTypes;
import org.jetbrains.annotations.NotNull;

public class UserDataAttachment {
    public static final Codec<UserDataAttachment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PickupComponent.CODEC.fieldOf("pickup").forGetter(UserDataAttachment::getPickup),
            RelationshipComponent.CODEC.fieldOf("relationship").forGetter(UserDataAttachment::getRelationship),
            AstrolabeComponent.CODEC.fieldOf("astrolabe").forGetter(UserDataAttachment::getAstrolabe),
            Codec.LONG.fieldOf("lastChangedGameDay").forGetter(UserDataAttachment::getLastChangedGameDay)
    ).apply(instance, (pickup, relationship, astrolabe, lastUpdateTime) -> {
        UserDataAttachment attachment = new UserDataAttachment();
        attachment.getPickup().setInfo(pickup);
        attachment.getRelationship().setInfo(relationship);
        attachment.getAstrolabe().setInfo(astrolabe);
        attachment.tryUpdateDaily(lastUpdateTime);

        return attachment;
    }));

    public static final StreamCodec<ByteBuf, UserDataAttachment> STREAM_CODEC = StreamCodec.composite(
            PickupComponent.STREAM_CODEC,
            UserDataAttachment::getPickup,
            RelationshipComponent.STREAM_CODEC,
            UserDataAttachment::getRelationship,
            AstrolabeComponent.STREAM_CODEC,
            UserDataAttachment::getAstrolabe,
            ByteBufCodecs.VAR_LONG,
            UserDataAttachment::getLastChangedGameDay,
            (pickup, relationship, astrolabe, lastUpdateTime) -> {
                UserDataAttachment attachment = new UserDataAttachment();
                attachment.getPickup().setInfo(pickup);
                attachment.getRelationship().setInfo(relationship);
                attachment.getAstrolabe().setInfo(astrolabe);
                attachment.tryUpdateDaily(lastUpdateTime);

                return attachment;
            }
    );

    public static UserDataAttachment getAttachment(Player player) {
        return player.getData(AttachmentTypes.USER_DATA);
    }

    @NotNull
    private final PickupComponent pickup = PickupComponent.createInstance();

    @NotNull
    private final RelationshipComponent relationship = RelationshipComponent.createInstance();

    @NotNull
    private final AstrolabeComponent astrolabe = AstrolabeComponent.createInstance();

    private long lastChangedGameDay;

    @NotNull
    public PickupComponent getPickup() {
        return this.pickup;
    }

    @NotNull
    public RelationshipComponent getRelationship() {
        return this.relationship;
    }

    @NotNull
    public AstrolabeComponent getAstrolabe() {
        return astrolabe;
    }

    private Long getLastChangedGameDay() {
        return this.lastChangedGameDay;
    }

    public void setComponent(UserDataAttachment last) {
        this.pickup.setInfo(last.pickup);
        this.relationship.setInfo(last.relationship);
        this.astrolabe.setInfo(last.astrolabe);
    }

    public boolean tryUpdateDaily(long day) {
        if (this.lastChangedGameDay != day) {
            this.lastChangedGameDay = day;

            this.pickup.update();
            this.astrolabe.update();
            return true;
        }
        return false;
    }
}
