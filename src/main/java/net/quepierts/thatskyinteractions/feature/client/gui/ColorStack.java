package net.quepierts.thatskyinteractions.feature.client.gui;

import java.util.Arrays;

public final class ColorStack {
    private static final int INITIAL_CAPACITY = 16;

    private int[] r;
    private int[] g;
    private int[] b;
    private int[] a;
    private int top = -1;

    public ColorStack() {
        this.r = new int[INITIAL_CAPACITY];
        this.g = new int[INITIAL_CAPACITY];
        this.b = new int[INITIAL_CAPACITY];
        this.a = new int[INITIAL_CAPACITY];
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
            final int red,
            final int green,
            final int blue,
            final int alpha
    ) {
        this.top++;

        if (this.top >= this.r.length) {
            this.expand();
        }

        this.r[this.top] = red   & 0xFF;
        this.g[this.top] = green & 0xFF;
        this.b[this.top] = blue  & 0xFF;
        this.a[this.top] = alpha & 0xFF;
    }

    public void pop() {
        if (this.top > 0) {
            this.top--;
        }
    }

    public void mul(
            final int rf,
            final int gf,
            final int bf,
            final int af
    ) {
        this.r[this.top] = clamp(this.r[this.top] * rf / 255);
        this.g[this.top] = clamp(this.g[this.top] * gf / 255);
        this.b[this.top] = clamp(this.b[this.top] * bf / 255);
        this.a[this.top] = clamp(this.a[this.top] * af / 255);
    }

    public void set(
            final int red,
            final int green,
            final int blue,
            final int alpha
    ) {
        this.r[this.top] = red   & 0xFF;
        this.g[this.top] = green & 0xFF;
        this.b[this.top] = blue  & 0xFF;
        this.a[this.top] = alpha & 0xFF;
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

    public void clear() {
        this.top = 0;
        this.r[0] = 255;
        this.g[0] = 255;
        this.b[0] = 255;
        this.a[0] = 255;
    }

    private void expand() {
        final var newCap = this.r.length * 2;

        this.r = Arrays.copyOf(this.r, newCap);
        this.g = Arrays.copyOf(this.g, newCap);
        this.b = Arrays.copyOf(this.b, newCap);
        this.a = Arrays.copyOf(this.a, newCap);
    }

    private static int clamp(final int v) {
        return Math.max(0, Math.min(255, v));
    }
}
