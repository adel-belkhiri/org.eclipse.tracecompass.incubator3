package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import org.eclipse.jdt.annotation.Nullable;

public enum LogicalCoreRole {

    /**
     *  LCore role is RTE/Thread
     */
    LCORE_RTE,

    /**
     *  LCore is Disabled
     */
    LCORE_OFF,

    /**
     * LCore role is Service
     */
    LCORE_SERVICE;

    private final static LogicalCoreRole[] fValues = values();

    @Override
    public @Nullable String toString() {
        //if(this == LCORE_OFF) {
        //    return null;
        //}
        return this.name();
    }

    /**
     * @param val xx
     * @return xx
     */
    public static @Nullable LogicalCoreRole fromInt(int val) {
        try {
            return fValues[val];
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
