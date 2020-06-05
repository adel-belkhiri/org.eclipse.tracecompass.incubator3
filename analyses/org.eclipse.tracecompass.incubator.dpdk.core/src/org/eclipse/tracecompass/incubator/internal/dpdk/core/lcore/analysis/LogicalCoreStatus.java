package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author adel
 *
 */
public enum LogicalCoreStatus {

    /**
     *  Lcore is wating for a new command
     */
    IDLE,

    /**
     *  Lcore is running
     */
    RUNNING,

    /**
     * Not yet ready to start scheduling services
     */
    DISABLED,

    /**
     * Command is executed
     */
    OFF;

    private final static LogicalCoreStatus[] fValues = values();

    @Override
    public @Nullable String toString() {
        if(this == OFF) {
            return null;
        }
        return this.name();
    }

    /**
     * @param val xx
     * @return xx
     */
    public static @Nullable LogicalCoreStatus fromInt(int val) {
        try {
            return fValues[val];
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
