package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author adel
 *
 */
public enum ServiceStatus {

    /**
     *  LCore role is RTE/Thread
     */
    UNKNOWN,
    /**
     *  LCore role is RTE/Thread
     */
    REGISTRED,

    /**
     *  LCore is Disabled
     */
    PENDING,

    /**
     * LCore role is Service
     */
    RUN;

    private final static ServiceStatus[] fValues = values();

    @Override
    public @Nullable String toString() {
        //if(this == PENDING) {
        //    return null;
        //}
        return this.name().toLowerCase();

    }

    /**
     * @param val xx
     * @return xx
     */
    public static @Nullable ServiceStatus fromInt(int val) {
        try {
            return fValues[val];
        }
        catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
