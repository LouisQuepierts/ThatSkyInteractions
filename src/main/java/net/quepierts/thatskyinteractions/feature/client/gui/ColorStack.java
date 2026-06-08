package net.quepierts.thatskyinteractions.feature.client.gui;

import net.minecraft.util.ARGB;

import java.util.Arrays;

public final class ColorStack {
    private static final int INITIAL_CAPACITY = 16;

    private int[] a;
    private int[] r;
    private int[] g;
    private int[] b;
    private int top = -1;

    public ColorStack() {
        this.a = new int[INITIAL_CAPACITY];
        this.r = new int[INITIAL_CAPACITY];
        this.g = new int[INITIAL_CAPACITY];
        this.b = new int[INITIAL_CAPACITY];
        this.push(255, 255, 255, 255);
    }

    public void push() {
        this.push(
                this.r[this.top],
                this.g[this.top],
                this.b[this.top],
                this.a[this.top]
        );
    }

    public void push(
            final int alpha,
            final int red,
            final int green,
            final int blue
    ) {
        this.top++;

        if (this.top >= this.r.length) {
            this.expand();
        }

        this.a[this.top] = alpha & 0xFF;
        this.r[this.top] = red   & 0xFF;
        this.g[this.top] = green & 0xFF;
        this.b[this.top] = blue  & 0xFF;
    }

    public void pop() {
        if (this.top > 1) {
            this.top--;
        }
    }

    public void mul(
            final int af,
            final int rf,
            final int gf,
            final int bf
    ) {
        this.a[this.top] = clamp(this.a[this.top] * af / 255);
        this.r[this.top] = clamp(this.r[this.top] * rf / 255);
        this.g[this.top] = clamp(this.g[this.top] * gf / 255);
        this.b[this.top] = clamp(this.b[this.top] * bf / 255);
    }

    public void mul(
            final float af,
            final float rf,
            final float gf,
            final float bf
    ) {
        this.a[this.top] = clamp((int) (this.a[this.top] * af));
        this.r[this.top] = clamp((int) (this.r[this.top] * rf));
        this.g[this.top] = clamp((int) (this.g[this.top] * gf));
        this.b[this.top] = clamp((int) (this.b[this.top] * bf));
    }

    public void set(
            final int alpha,
            final int red,
            final int green,
            final int blue
    ) {
        this.a[this.top] = alpha & 0xFF;
        this.r[this.top] = red   & 0xFF;
        this.g[this.top] = green & 0xFF;
        this.b[this.top] = blue  & 0xFF;
    }

    public void set(
            final float alpha,
            final float red,
            final float green,
            final float blue
    ) {
        this.a[this.top] = clamp((int) (alpha * 255));
        this.r[this.top] = clamp((int) (red   * 255));
        this.g[this.top] = clamp((int) (green * 255));
        this.b[this.top] = clamp((int) (blue  * 255));
    }

    public int red() {
        return this.r[this.top];
    }

    public int green() {
        return this.g[this.top];
    }

    public int blue() {
        return this.b[this.top];
    }

    public int alpha() {
        return this.a[this.top];
    }

    public int argb() {
        return (this.a[this.top] << 24)
                | (this.r[this.top] << 16)
                | (this.g[this.top] << 8)
                | this.b[this.top];
    }

    public int argb(
            int alpha,
            int red,
            int green,
            int blue
    ) {
        return ARGB.color(
                clamp(alpha * this.a[this.top] / 255),
                clamp(red   * this.r[this.top] / 255),
                clamp(green * this.g[this.top] / 255),
                clamp(blue  * this.b[this.top] / 255)
        );
    }

    public int argb(int argb) {
        return switch (argb) {
            case 0x00000000 -> 0x00000000;
            case 0xffffffff -> this.argb();
            default -> this.argb(
                    ARGB.alpha(argb),
                    ARGB.red(argb),
                    ARGB.green(argb),
                    ARGB.blue(argb)
            );
        };
    }

    public void clear() {
        this.top = 0;
        this.a[0] = 255;
        this.r[0] = 255;
        this.g[0] = 255;
        this.b[0] = 255;
    }

    private void expand() {
        final var newCap = this.r.length * 2;

        this.a = Arrays.copyOf(this.a, newCap);
        this.r = Arrays.copyOf(this.r, newCap);
        this.g = Arrays.copyOf(this.g, newCap);
        this.b = Arrays.copyOf(this.b, newCap);
    }

    private static int clamp(final int v) {
        return Math.max(0, Math.min(255, v));
    }
}
