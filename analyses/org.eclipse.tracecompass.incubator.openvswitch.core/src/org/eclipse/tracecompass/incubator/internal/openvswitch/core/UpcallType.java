package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

/**
 * @author Adel Belkhiri
 *
 */
public enum UpcallType {
    /** Some kind of bug somewhere. */
    BAD_UPCALL,

    /**
     * A flow miss.
     */
    MISS_UPCALL,

    /* Action Upcall sub-types*/
    /**
     * Slow path upcall.
     */
    SLOW_PATH_UPCALL,

    /**
     * sFlow sample.
     */
    SFLOW_UPCALL,

    /**
     *  Per-flow sampling.
     */
    FLOW_SAMPLE_UPCALL,

    /**
     * Per-bridge sampling.
     */
    IPFIX_UPCALL,

    /**
     *  Destined for the controller.
     */
    CONTROLLER_UPCALL;


    private static final UpcallType[] values = UpcallType.values();

    /**
     * @param val
     * @return
     */
    public static UpcallType fromInt(int val) {
        try {
            return values[val];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return BAD_UPCALL;
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
