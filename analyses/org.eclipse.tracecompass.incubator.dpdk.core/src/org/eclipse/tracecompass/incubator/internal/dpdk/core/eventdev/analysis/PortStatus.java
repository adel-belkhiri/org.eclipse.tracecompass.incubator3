package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Types of Queues scheduling types
 *
 * @author Adel Belkhiri
 *
 */
public enum PortStatus {
    UNKNOWN,
    IDLE,
    PAUSING,
    FORWARDING,
    UNPAUSING;

    private final static PortStatus[] fValues = values();

    @Override
    public @Nullable String toString() {
        return this.name();
    }

    public static @Nullable PortStatus fromInt(int val) {
        try {
            return fValues[val];
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
