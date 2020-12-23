package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author  Adel Belkhiri
 *
 */

@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.messages";  //$NON-NLS-1$


    public static @Nullable String IDpdkModel_EVENTDEVS;
    public static @Nullable String IDpdkModel_SW;
    public static @Nullable String IDpdkModel_DEV_ID;
    public static @Nullable String IDpdkModel_SERVICE_ID;
    public static @Nullable String IDpdkModel_CREDIT_QUANTA;
    public static @Nullable String IDpdkModel_SCHED_QUANTA;

    public static @Nullable String IDpdkModel_NB_EVENT_LIMIT;
    public static @Nullable String IDpdkModel_EVENTDEV_NAME;

    public static @Nullable String  IDpdkModel_PORTS;
    public static @Nullable String  IDpdkModel_PORT_DEQUEUE_DEPTH;
    public static @Nullable String  IDpdkModel_PORT_ENQUEUE_DEPTH;
    public static @Nullable String  IDpdkModel_PORT_NEW_THRESHOLD;
    public static @Nullable String  IDpdkModel_PRIORITY;
    public static @Nullable String  IDpdkModel_SCHEDULE_TYPE;

    public static @Nullable String  IDpdkModel_QUEUES;
    public static @Nullable String  IDpdkModel_EVENT_RX;
    public static @Nullable String  IDpdkModel_EVENT_TX;
    public static @Nullable String  IDpdkModel_EVENT_DROPPED;

    public static @Nullable String  IDpdkModel_RING_NAME;
    public static @Nullable String  IDpdkModel_NB_EVENTS;
    public static @Nullable String  IDpdkModel_RINGS;
    public static @Nullable String  IDpdkModel_RING_CAPACITY;

    public static @Nullable String  IDpdkModel_QUEUE_SCHEDULE_TYPE;
    public static @Nullable String  IDpdkModel_FLOW_ID;

    public static String IDpdkModel_PER_PORT_PACKET_RATE_DATAPROVIDER_TITLE;

    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
