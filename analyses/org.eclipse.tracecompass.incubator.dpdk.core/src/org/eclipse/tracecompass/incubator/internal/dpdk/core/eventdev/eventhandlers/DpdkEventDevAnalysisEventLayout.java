package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */

@SuppressWarnings({"javadoc", "nls"})
public class DpdkEventDevAnalysisEventLayout {

    /** Eventdev events **/
    public @NonNull String eventSwProbe() {
        return new String("sw_eventdev:sw_probe");
    }

    public @NonNull String eventRteEventDevConfigure() {
        return new String("librte_eventdev:rte_event_dev_configure");
    }

    public @NonNull String eventRteQueueSetup() {
        return new String("librte_eventdev:rte_event_queue_setup");
    }

    public @NonNull String eventRtePortSetup() {
        return new String("librte_eventdev:rte_event_port_setup");
    }

    public @NonNull String eventSwPortSetup() {
        return new String("sw_eventdev:sw_port_setup");
    }

    public @NonNull String eventRteEventRingCreate() {
        return new String("librte_eventdev_ring:rte_event_ring_create");
    }

    public @NonNull String eventRtePortUnlink() {
        return new String("librte_eventdev:rte_event_port_unlink");
    }

    public @NonNull String eventRtePorLink() {
        return new String("librte_eventdev:rte_event_port_link");
    }

    public @NonNull String eventRteEventDevStart() {
        return new String("librte_eventdev:rte_event_dev_start");
    }

    public @NonNull String eventRteEventRingDequeueBurst() {
        return new String("librte_eventdev_ring:rte_event_ring_dequeue_burst");
    }

    public @NonNull String eventRteEventRingEnqueueBurst() {
        return new String("librte_eventdev_ring:rte_event_ring_enqueue_burst");
    }

    public @NonNull String eventSwEventEnqueueBurst() {
        return new String("sw_eventdev:sw_event_enqueue_burst");
    }

    public @NonNull String eventSwEventDequeueBurst() {
        return new String("sw_eventdev:sw_event_dequeue_burst");
    }

    public @NonNull String eventSwPullPortLB() {
        return new String("sw_eventdev:pull_port_lb");
    }

    public @NonNull String eventSwScheduleParallelToCq() {
        return new String("sw_eventdev:schedule_parallel_to_cq");
    }

    public @NonNull String eventSwScheduleAtomicToCq() {
        return new String("sw_eventdev:schedule_atomic_to_cq");
    }

    /**
     * Event fields
     * @return field name
     */

    public @NonNull String fieldSw() {
        return new String("sw");
    }

    public @NonNull String fieldSwInflights() {
        return new String("sw_inflights");
    }

    public @NonNull String fieldPortInflightCredits() {
        return new String("p_inflight_credits");
    }

    public @NonNull String fieldCreditQuanta() {
        return new String("credit_quanta");
    }

    public @NonNull String fieldServiceId() {
        return new String("service_id");
    }

    public @NonNull String fieldDevID() {
        return new String("dev_id");
    }

    public @NonNull String fieldNbEventsLimit() {
        return new String("nb_events_limit");
    }

    public @NonNull String fieldEventDev() {
        return new String("eventdev");
    }

    public @NonNull String fieldSchedQuanta() {
        return new String("sched_quanta");
    }

    public @NonNull String fieldName() {
        return new String("name");
    }

    public @NonNull String fieldRing() {
        return new String("ring");
    }

    public @NonNull String fieldRingCapacity() {
        return new String("capacity");
    }

    public @NonNull String fieldNbDeqEvents() {
        return new String("n_deq");
    }

    public @NonNull String fieldNbEnqEvents() {
        return new String("n_enq");
    }

    public @NonNull String fieldNbNewEvents() {
        return new String("n_new");
    }

    public @NonNull String fieldPortIdx() {
        return new String("port_id");
    }

    public @NonNull String fieldPortEnqueueDepth() {
        return new String("enqueue_depth");
    }

    public @NonNull String fieldPortDequeueDepth() {
        return new String("dequeue_depth");
    }


    public @NonNull String fieldPortNewEventsThreshold() {
        return new String("new_event_threshold");
    }

    public @NonNull String fieldPort() {
        return new String("port");
    }

    public @NonNull String fieldRxWorkerRing() {
        return new String("rx_worker_ring");
    }

    public @NonNull String fieldCqWorkerRing() {
        return new String("cq_worker_ring");
    }

    public @NonNull String fieldQueueId() {
        return new String("queue_id");
    }

    public @NonNull String fieldPriority() {
        return new String("priority");
    }

    public @NonNull String fieldScheduleType() {
        return new String("schedule_type");
    }

    public @NonNull String fieldQidID() {
        return new String("qid_id");
    }

    public @NonNull String fieldFlowID() {
        return new String("flow_id");
    }
}

