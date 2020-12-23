package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Types of Queues scheduling types
 *
 * @author Adel Belkhiri
 *
 */
public enum QueueScheduleType {
    ORDERED,
    ATOMIC,
    PARALLEL;

    private final static QueueScheduleType[] fValues = values();

    @Override
    public @Nullable String toString() {
        return this.name();
    }

    public static @Nullable QueueScheduleType fromInt(int val) {
        try {
            return fValues[val];
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
