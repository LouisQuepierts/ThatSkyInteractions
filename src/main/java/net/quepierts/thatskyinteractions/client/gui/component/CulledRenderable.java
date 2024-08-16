package net.quepierts.thatskyinteractions.client.gui.component;

import net.minecraft.client.gui.components.Renderable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector2f;
import org.joml.Vector4f;

@OnlyIn(Dist.CLIENT)
public interface CulledRenderable extends Renderable {
    default boolean shouldRender(Vector4f region) {
        return true;
    }

    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    static boolean intersects(Vector4f a, Vector4f b) {
        return a.x < b.x + b.z &&
                a.x + a.z > b.x &&
                a.y < b.y + b.w &&
                a.y + a.w > b.y;
    }

    static boolean intersects(Vector4f rect, float x, float y, float width, float height) {

        return rect.x < x + width &&
                rect.x + rect.z > x &&
                rect.y < y + height &&
                rect.y + rect.w > y;
    }

    static boolean intersects(Vector4f rect, Vector2f point) {
        return point.x > rect.x &&
                point.x < rect.x + rect.z &&
                point.y > rect.y &&
                point.y < rect.y + rect.w;
    }

    static boolean intersects(Vector4f rect, float x, float y) {
        return x > rect.x &&
                x < rect.x + rect.z &&
                y > rect.y &&
                y < rect.y + rect.w;
    }
}
