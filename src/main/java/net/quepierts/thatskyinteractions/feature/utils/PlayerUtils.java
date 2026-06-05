package net.quepierts.thatskyinteractions.feature.utils;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PlayerUtils {
    public static boolean isRiding(Entity player) {
        return player != null && player.getVehicle() != null;
    }

    public static float getLookAtRotY(Player player, Vec3 vec3) {
        Vec3 vec32 = EntityAnchorArgument.Anchor.EYES.apply(player);
        double d = vec3.x - vec32.x;
        double f = vec3.z - vec32.z;
        return Mth.wrapDegrees((float)(Mth.atan2(f, d) * 57.2957763671875) - 90.0F);
    }

    public static Vec3 getRelativePositionWorldSpace(Player player, double forward, double left) {
        Vec2 vec2 = new Vec2(0, player.yBodyRot);
        Vec3 vec3 = player.position();
        final float f = Mth.cos((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        final float f1 = Mth.sin((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        final double d0 = f * forward - f1 * left;
        final double d2 = f1 * forward + f * left;
        return new Vec3(vec3.x + d0, vec3.y, vec3.z + d2);
    }

    public static Vec3 getRelativePosition(Player player, double forward, double left) {
        Vec2 vec2 = new Vec2(0, player.yBodyRot);
        final float f = Mth.cos((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        final float f1 = Mth.sin((vec2.y + 90.0F) * ((float)Math.PI / 180F));
        return new Vec3(f * forward - f1 * left, 0, f1 * forward + f * left);
    }

    public static boolean canPositionStand(Vec3 vec3, Level level, float down) {
        AABB box = new AABB(
                vec3.x - 0.3f, vec3.y - down, vec3.z - 0.3f,
                vec3.x + 0.3f, vec3.y, vec3.z + 0.3f);
        return level.getBlockCollisions(null, box).iterator().hasNext();
    }

    public static boolean canPositionPass(Vec3 vec3, Level level) {
        AABB box = new AABB(
                vec3.x - 0.3f, vec3.y + 0.1f, vec3.z - 0.3f,
                vec3.x + 0.3f, vec3.y + 1.7f, vec3.z + 0.3f);

        return !level.getBlockCollisions(null, box).iterator().hasNext();
    }

    public static boolean isPositionSave(Vec3 vec3, Level level) {
        return canPositionStand(vec3, level, 0.1f) && canPositionPass(vec3, level);
    }

    public static double distanceSqr2D(Vec3 src, Vec3 dest) {
        double x = dest.x - src.x;
        double z = dest.z - src.z;
        return x * x + z * z;
    }

    public static boolean inSameDimension(Player a, Player b) {
        return a.level() == b.level();
    }
}
