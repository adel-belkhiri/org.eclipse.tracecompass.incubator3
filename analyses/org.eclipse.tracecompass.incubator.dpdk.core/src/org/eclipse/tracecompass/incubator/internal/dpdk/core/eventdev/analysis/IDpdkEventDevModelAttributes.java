package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author  Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkEventDevModelAttributes {

    @Nullable String EVENTDEVS = Messages.IDpdkModel_EVENTDEVS;

    @Nullable String SW = Messages.IDpdkModel_SW;
    @Nullable String DEV_ID = Messages.IDpdkModel_DEV_ID;
    @Nullable String SERVICE_ID = Messages.IDpdkModel_SERVICE_ID;
    @Nullable String CREDIT_QUANTA = Messages.IDpdkModel_CREDIT_QUANTA;
    @Nullable String SCHED_QUANTA = Messages.IDpdkModel_SCHED_QUANTA;

    @Nullable String NB_EVENT_LIMIT = Messages.IDpdkModel_NB_EVENT_LIMIT;
    @Nullable String EVENTDEV_NAME = Messages.IDpdkModel_EVENTDEV_NAME;

    @Nullable String PORTS = Messages.IDpdkModel_PORTS;
    @Nullable String PORT_DEQUEUE_DEPTH = Messages.IDpdkModel_PORT_DEQUEUE_DEPTH;
    @Nullable String PORT_ENQUEUE_DEPTH = Messages.IDpdkModel_PORT_ENQUEUE_DEPTH;
    @Nullable String PORT_NEW_THRESHOLD = Messages.IDpdkModel_PORT_NEW_THRESHOLD;

    @Nullable String QUEUES = Messages.IDpdkModel_QUEUES;
    @Nullable String PRIORITY = Messages.IDpdkModel_PRIORITY;
    @Nullable String SCHEDULE_TYPE = Messages.IDpdkModel_SCHEDULE_TYPE;

    @Nullable String EVENT_RX = Messages.IDpdkModel_EVENT_RX;
    @Nullable String EVENT_TX = Messages.IDpdkModel_EVENT_TX;
    @Nullable String EVENT_DROPPED = Messages.IDpdkModel_EVENT_DROPPED;

    @Nullable String RINGS = Messages.IDpdkModel_RINGS;
    @Nullable String NB_EVENTS = Messages.IDpdkModel_NB_EVENTS;
    @Nullable String RING_NAME = Messages.IDpdkModel_RING_NAME;
    @Nullable String RING_CAPACITY = Messages.IDpdkModel_RING_CAPACITY;
    @Nullable String FLOW_ID = Messages.IDpdkModel_FLOW_ID;


    //@Nullable String HIT_PERCENT_METRIC_LABEL = Messages.IDpdkModel_HIT_PERCENT_METRIC_LABEL;

    @Nullable String IDpdkModel_PER_PORT_PACKET_RATE_DATAPROVIDER_TITLE = Messages.IDpdkModel_PER_PORT_PACKET_RATE_DATAPROVIDER_TITLE;
}
