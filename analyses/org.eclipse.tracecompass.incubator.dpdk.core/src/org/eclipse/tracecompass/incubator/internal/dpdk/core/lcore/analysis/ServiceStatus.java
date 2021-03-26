package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author adel
 *
 */
public enum ServiceStatus {

    /**
     *
     */
    UNKNOWN,
    /**
     *
     */
    REGISTRED,

    /**
     *  Service is Disabled
     */
    DISABLED,

    /**
     *  Service is Enabled
     */
    ENABLED,

    /**
     *  Service is enabled but waiting to be executed
     */
    PENDING,

    /**
     * Service is running
     */
    RUNNING;

    private final static ServiceStatus[] fValues = values();

    @Override
    public @Nullable String toString() {
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
