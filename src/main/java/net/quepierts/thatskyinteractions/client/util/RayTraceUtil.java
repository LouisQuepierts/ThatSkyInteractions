package net.quepierts.thatskyinteractions.client.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class RayTraceUtil {
    public static boolean isBlockedBySolidBlock(Level level, Vector3f start, Vector3f end, float step) {
        float distance = start.distanceSquared(end);
        final Vector3f direction = end.sub(start).normalize();
        Vector3f current = start;

        for (double d = 0; d * d < distance; d += step) {
            BlockPos pos = new BlockPos(
                    Math.round(current.x),
                    Math.round(current.y),
                    Math.round(current.z)
            );
            BlockState state = level.getBlockState(pos);

            if (state.isViewBlocking(level, pos)) {
                return true;
            }

            current = current.add(
                    direction.x * step,
                    direction.y * step,
                    direction.z * step
            );
        }

        return false;
    }
}
