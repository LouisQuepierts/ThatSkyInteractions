package net.quepierts.thatskyinteractions.client.gui.animate;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimateUtils {
    public static class Lerp {
        public static double linear(double src, double dest, float time) {
            return src * (1.0F - time) + dest * time;
        }

        public static double smooth(double src, double dest, float time) {
            time = 2 * time - time * time;
            return src * (1.0F - time) + dest * time;
        }

        public static double sCurve(double src, double dest, float time) {
            float sqr = time * time;
            time = 3 * sqr - 2 * sqr * time;
            return src * (1.0F - time) + dest * time;
        }

        public static double bounce(double src, double dest, float time) {
            time = 2 * (time - time * time);
            return src * (1.0F - time) + dest * time;
        }
    }

    public static class Time {
        public static float smooth(float time) {
            return 2 * time - time * time;
        }

        public static float bounce(float time) {
            return 4 * (time - time * time);
        }
    }
}
