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
    public static @Nullable String IDpdkModel_INFLIGHTS_CREDIT;
    public static @Nullable String IDpdkModel_EVENTDEV_NAME;

    public static @Nullable String  IDpdkModel_PORTS;
    public static @Nullable String  IDpdkModel_ATTACHED_PORTS;
    public static @Nullable String  IDpdkModel_PORT_STATUS;
    public static @Nullable String  IDpdkModel_NB_FLOW_MIGRATION;
    public static @Nullable String  IDpdkModel_PORT_DEQUEUE_DEPTH;
    public static @Nullable String  IDpdkModel_PORT_ENQUEUE_DEPTH;
    public static @Nullable String  IDpdkModel_PORT_NEW_THRESHOLD;
    public static @Nullable String  IDpdkModel_PRIORITY;
    public static @Nullable String  IDpdkModel_SCHEDULE_TYPE;
    public static @Nullable String  IDpdkModel_PORT_LOAD;
    public static @Nullable String  IDpdkModel_COUNT_PER_FLOW;
    public static @Nullable String  IDpdkModel_FLOW_STATUS;
    public static @Nullable String  IDpdkModel_ZERO_POLLS;
    public static @Nullable String  IDpdkModel_TOT_POLLS;

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
    public static @Nullable String  IDpdkModel_FLOWS;
    public static @Nullable String  IDpdkModel_NB_FLOWS;
    public static @Nullable String  IDpdkModel_FLOW_MIGRATION_LATENCY;

    public static String IDpdkModel_EVENTDEV_RING_OCCUPATION_DATAPROVIDER_TITLE;
    public static String IDpdkModel_EVENTDEV_INFLIGHT_CREDIT_DATAPROVIDER_TITLE;
    public static String IDpdkModel_EVENTDEV_PORT_ENQUEUE_DEQUEUE_DATAPROVIDER_TITLE;
    public static String IDpdkModel_EVENTDEV_PORT_PER_STAGE_LOAD_PERCENTAGE_DATAPROVIDER_TITLE;
    public static String IDpdkModel_EVENTDEV_PORT_BUSYNESS_PERCENTAGE_DATAPROVIDER_TITLE;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
