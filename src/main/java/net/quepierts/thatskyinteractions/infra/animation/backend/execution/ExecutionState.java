package net.quepierts.thatskyinteractions.infra.animation.backend.execution;

import java.util.Arrays;

public final class ExecutionState {

    private final long[] bits;

    public ExecutionState(int size) {
        this.bits = new long[size >>> 6];
        this.clear();
    }

    public void setExecutionMask(int oid, boolean value) {
        if (value) {
            this.bits[oid >>> 6] |= (1L << (oid & 0x3F));
        } else {
            this.bits[oid >>> 6] &= ~(1L << (oid & 0x3F));
        }
    }

    public boolean getExecutionMask(int oid) {
        return (this.bits[oid >>> 6] & (1L << (oid & 0x3F))) != 0;
    }

    public void clear() {
        Arrays.fill(this.bits, 0xFFFFFFFFFFFFFFL);
    }

    public void copyFrom(ExecutionState state) {
        System.arraycopy(state.bits, 0, this.bits, 0, state.bits.length);
    }

}
