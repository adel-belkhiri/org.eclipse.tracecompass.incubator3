package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author Adel Belkhiri
 *
 */
public enum FlowState {

    /**
    *
    */
   FLOW_UNKNOWN_STATUS,

    /**
     *
     */
    FLOW_IDLE,

    /**
     * the upcall status is unknown
     */
    FLOW_KERNEL_INSTALLED,

    /**
     *
     */
    FLOW_USERSPACE_INSTALLED;


    @Override
    public @Nullable String toString() {
        if(this == FLOW_IDLE) {
            return null;
        }
        return this.name();
    }
}
