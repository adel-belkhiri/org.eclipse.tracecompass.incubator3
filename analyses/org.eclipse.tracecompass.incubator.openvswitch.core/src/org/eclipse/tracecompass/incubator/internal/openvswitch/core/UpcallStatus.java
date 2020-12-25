package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

/**
 * @author Adel Belkhiri
 *
 */
public enum UpcallStatus {
    /**
     * the upcall status is unknown
     */
    UPCALL_UNKNOWN,
    /**
     * Upcall is waiting in the queue
     */
    UPCALL_WAITING,
    /**
     * Upcall has been processed by the thread handler
     */
    UPCALL_PROCESSING,

    /**
     * Upcall actions are being executed by the ovs handlers
     */
    UPCALL_USERSPACE_EXEC,

    /**
     * Upcall actions are being executed by the ovs handlers
     */
    UPCALL_QUEUE_CONTROLLER,

    /**
     * Upcall actions are being executed by datapath
     */
    UPCALL_DATAPATH_EXEC;


    @Override
    public String toString() {
        return this.name();
    }
}
