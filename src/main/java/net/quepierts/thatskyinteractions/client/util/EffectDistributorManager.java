package net.quepierts.thatskyinteractions.client.util;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.quepierts.simpleanimator.api.IAnimateHandler;
import net.quepierts.simpleanimator.api.animation.ModelBone;
import net.quepierts.simpleanimator.api.animation.keyframe.VariableHolder;
import net.quepierts.simpleanimator.core.client.ClientAnimator;
import net.quepierts.thatskyinteractions.client.distributor.EffectDistributor;
import net.quepierts.thatskyinteractions.client.registry.EffectDistributors;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class EffectDistributorManager {
    private final EnumMap<ModelBone, ImmutableList<EffectDistributor>> distributors;
    private final Map<UUID, PartMatrices> matrices;

    public EffectDistributorManager() {
        this.matrices = new Object2ObjectOpenHashMap<>();
        this.distributors = new EnumMap<>(ModelBone.class);

        this.addDistributors();
    }

    public PartMatrices get(UUID uuid) {
        return matrices.computeIfAbsent(uuid, o -> PartMatrices.create());
    }

    public void remove(UUID uuid) {
        this.matrices.remove(uuid);
    }

    public void clear() {
        this.matrices.clear();
    }

    public void onPlayerTick(final PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        UUID uuid = player.getUUID();

        PartMatrices matrices = this.matrices.get(uuid);
        if (matrices == null)
            return;

        ClientAnimator animator = (ClientAnimator) ((IAnimateHandler) player).simpleanimator$getAnimator();
        if (animator.isRunning()) {
            this.distribute(matrices.root, ModelBone.ROOT, player, animator);
            this.distribute(matrices.root, ModelBone.BODY, player, animator);
            this.distribute(matrices.leftArm, ModelBone.LEFT_ARM, player, animator);
            this.distribute(matrices.rightArm, ModelBone.RIGHT_ARM, player, animator);
        }
    }

    private void distribute(Matrix4f matrix4f, ModelBone bone, Player player, ClientAnimator animator) {
        ImmutableList<EffectDistributor> distributors = this.distributors.get(bone);

        if (distributors == null || distributors.isEmpty())
            return;

        for (EffectDistributor distributor : distributors) {
            VariableHolder holder = animator.getVariable(distributor.name());

            if (holder == VariableHolder.Immutable.ZERO || distributor.shouldSkipDistribute(holder))
                continue;

            Vector3f transformed = matrix4f.transformPosition(distributor.position(animator), new Vector3f());
            distributor.distribute(transformed, player, animator);
        }
    }

    private void addDistributors() {
        ImmutableList.Builder<EffectDistributor> leftArm = ImmutableList.builder();
        distributors.put(ModelBone.LEFT_ARM, leftArm.build());

        ImmutableList.Builder<EffectDistributor> rightArm = ImmutableList.builder();
        rightArm.add(EffectDistributors.CANDLE_FLAME_EFFECT_DISTRIBUTOR);
        distributors.put(ModelBone.RIGHT_ARM, rightArm.build());

        ImmutableList.Builder<EffectDistributor> root = ImmutableList.builder();
        root.add(EffectDistributors.HEART_EFFECT_DISTRIBUTOR);
        root.add(EffectDistributors.HIGH_FIVE_EFFECT_DISTRIBUTOR);
        distributors.put(ModelBone.ROOT, root.build());

        ImmutableList.Builder<EffectDistributor> body = ImmutableList.builder();
        distributors.put(ModelBone.BODY, body.build());
    }


    public record PartMatrices(
            Matrix4f root,
            Matrix4f body,
            Matrix4f leftArm,
            Matrix4f rightArm
    ) {
        public static PartMatrices create() {
            return new PartMatrices(
                    new Matrix4f(),
                    new Matrix4f(),
                    new Matrix4f(),
                    new Matrix4f()
            );
        }
    }
}
