package net.quepierts.thatskyinteractions.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.quepierts.thatskyinteractions.client.gui.animate.AbstractScreenAnimation;
import net.quepierts.thatskyinteractions.client.gui.animate.AnimateUtils;
import net.quepierts.thatskyinteractions.client.gui.animate.ScreenAnimator;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class CameraHandler {
    private final EnumMap<Property, Entry> properties;

    public CameraHandler() {
        properties = new EnumMap<>(Property.class);
        for (Property value : Property.values()) {
            properties.put(value, Entry.create());
        }
    }

    @NotNull
    public Entry get(Property property) {
        return this.properties.get(property);
    }

    public void cleanup() {
        for (Entry value : this.properties.values()) {
            ScreenAnimator.GLOBAL.remove(value);
            value.reset();
        }
    }

    public void onComputeCameraAngles(final ViewportEvent.ComputeCameraAngles event) {
        Entry rotation = this.properties.get(Property.ROTATION);
        rotation.setUnmodified(event.getPitch(), event.getYaw(), event.getRoll());
        Vector3f modified = rotation.modified;
        event.setPitch(event.getPitch() + modified.x);
        event.setYaw(event.getYaw() + modified.y);
        event.setRoll(event.getRoll() + modified.z);
    }

    public Vec3 recomputeCameraPosition(final Vec3 in) {
        Entry position = this.properties.get(Property.POSITION);
        position.setUnmodified((float) in.x, (float) in.y, (float) in.z);

        Vector3f modified = position.modified;
        if (modified.lengthSquared() == 0)
            return in;
        return in.add(modified.x, modified.y, modified.z);
    }

    public float recomputeDayTime(float unmodified) {
        Entry dayTime = this.properties.get(Property.DAY_TIME);
        dayTime.unmodified.set(unmodified);
        return (unmodified + dayTime.modified.x) % 1.0f;
    }

    public static class Entry extends AbstractScreenAnimation {
        private static final Vector3f ZERO = new Vector3f();
        private final Vector3f unmodified;
        private final Vector3f src;
        private final Vector3f dest;
        private final Vector3f modified;

        private Entry(Vector3f vector3f, Vector3f src, Vector3f dest, Vector3f modified) {
            super(0.5f);
            unmodified = vector3f;
            this.src = src;
            this.dest = dest;
            this.modified = modified;
        }

        private static Entry create() {
            return new Entry(
                    new Vector3f(),
                    new Vector3f(),
                    new Vector3f(),
                    new Vector3f()
            );
        }

        public void setUnmodified(float x, float y, float z) {
            this.unmodified.set(x, y, z);
        }

        public void setUnmodified(Vector3f vector3f) {
            this.unmodified.set(vector3f);
        }

        public Vector3f getUnmodified(Vector3f out) {
            return out.set(this.unmodified);
        }

        public Vector3f getModified(Vector3f out) {
            return out.set(this.modified);
        }

        public Vector3f getDest(Vector3f out) {
            return out.set(this.dest);
        }

        public void reset() {
            this.src.set(0);
            this.dest.set(0);
            this.modified.set(0);
        }

        public void toTarget(Vector3f dest) {
            this.setLength(0.5f);
            this.src.set(this.modified);
            this.dest.set(dest);

            ScreenAnimator.GLOBAL.play(this);
        }

        public void toTarget(Vector3f dest, float length) {
            this.setLength(length);
            this.src.set(this.modified);
            this.dest.set(dest);

            ScreenAnimator.GLOBAL.play(this);
        }

        public void toTarget(Vector3f dest, float length, float delay) {
            this.setLength(length);
            this.src.set(this.modified);
            this.dest.set(dest);

            ScreenAnimator.GLOBAL.play(this, delay);
        }

        public void toDefault() {
            this.toTarget(ZERO);
        }

        public void toDefault(float length, float delay) {
            this.toTarget(ZERO, length, delay);
        }

        @Override
        protected void run(float time) {
            float local = time / this.getLength();
            this.modified.set(
                    AnimateUtils.Lerp.smooth(this.src.x, this.dest.x, local),
                    AnimateUtils.Lerp.smooth(this.src.y, this.dest.y, local),
                    AnimateUtils.Lerp.smooth(this.src.z, this.dest.z, local)
            );
        }
    }

    public enum Property {
        ROTATION,
        POSITION,
        DAY_TIME
    }
}
