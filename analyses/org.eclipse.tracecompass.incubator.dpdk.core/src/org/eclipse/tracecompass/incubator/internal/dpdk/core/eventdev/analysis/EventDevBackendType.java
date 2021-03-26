package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Types of Queues scheduling types
 *
 * @author Adel Belkhiri
 *
 */
public enum EventDevBackendType {
    UNKNOWN,
    SW,
    DSW;

    private final static EventDevBackendType[] fValues = values();

    @Override
    public @Nullable String toString() {
        return this.name();
    }

    public static @Nullable EventDevBackendType fromInt(int val) {
        try {
            return fValues[val];
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
