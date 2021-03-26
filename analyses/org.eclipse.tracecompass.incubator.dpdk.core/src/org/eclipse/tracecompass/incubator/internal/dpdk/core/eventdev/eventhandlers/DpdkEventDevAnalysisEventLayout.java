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

    public @NonNull String eventSwPortSetup() {
        return new String("sw_eventdev:sw_port_setup");
    }

    public @NonNull String eventRteEventRingCreate() {
        return new String("librte_eventdev_ring:rte_event_ring_create");
    }

    public @NonNull String eventRteEventRingDequeueBurst() {
        return new String("librte_eventdev_ring:rte_event_ring_dequeue_burst");
    }

    public @NonNull String eventRteEventRingEnqueueBurst() {
        return new String("librte_eventdev_ring:rte_event_ring_enqueue_burst");
    }

    public @NonNull String eventRtePortUnlink() {
        return new String("librte_eventdev:rte_event_port_unlink");
    }

    public @NonNull String eventRtePortLink() {
        return new String("librte_eventdev:rte_event_port_link");
    }

    public @NonNull String eventRteEventDevStart() {
        return new String("librte_eventdev:rte_event_dev_start");
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

    public @NonNull String eventSwSchedulePullPortDir() {
        return new String("sw_eventdev:sw_schedule_pull_port_dir");
    }

    public @NonNull String eventSwScheduleDirToCq() {
        return new String("sw_eventdev:schedule_dir_to_cq");
    }

    public @NonNull String eventSwScheduleParallelToCq() {
        return new String("sw_eventdev:schedule_parallel_to_cq");
    }

    public @NonNull String eventSwScheduleAtomicToCq() {
        return new String("sw_eventdev:schedule_atomic_to_cq");
    }

    /**
     * Distributed Software Event Device
     */

    public @NonNull String eventDswProbe() {
        return new String("dsw_eventdev:dsw_probe");
    }

    public @NonNull String eventDswPortStartMigration() {
        return new String("dsw_eventdev:dsw_port_start_migration");
    }

    public @NonNull String eventDswPortEndMigration() {
        return new String("dsw_eventdev:dsw_port_end_migration");
    }

    public @NonNull String eventDswPortMoveMigratingFlow() {
        return new String("dsw_eventdev:dsw_port_move_migrating_flow");
    }

    public @NonNull String eventCollectAllConfirmationMessages() {
        return new String("dsw_eventdev:collect_all_confirm_msgs");
    }

    public @NonNull String eventDswPortLoadUpdate() {
        return new String("dsw_eventdev:dsw_port_load_update");
    }

    public @NonNull String eventDswPortSetup() {
        return new String("dsw_eventdev:dsw_port_setup");
    }

    public @NonNull String eventDswPortBufferNonPaused() {
        return new String("dsw_eventdev:dsw_port_buffer_non_paused");
    }

    public @NonNull String eventDswEventDequeueBurst() {
        return new String("dsw_eventdev:dsw_event_dequeue_burst");
    }

    public @NonNull String eventDswEventEnqueueBurst() {
        return new String("dsw_eventdev:dsw_event_enqueue_burst");
    }

    public @NonNull String eventDswPortAcquireCredits() {
        return new String("dsw_eventdev:dsw_port_acquire_credits");
    }

    //----------------------------

    public @NonNull String fieldDsw() {
        return new String("dsw");
    }

    public @NonNull String fieldSrcPortId() {
        return new String("src_port_id");
    }

    public @NonNull String fieldDstPortId() {
        return new String("dst_port_id");
    }

    public @NonNull String fieldFlowHash() {
        return new String("flow_hash");
    }

    public @NonNull String fieldInRing() {
        return new String("in_ring");
    }

    public @NonNull String fieldCtlInRing() {
        return new String("ctl_in_ring");
    }

    public @NonNull String fieldRequiredCredits() {
        return new String("required_credits");
    }

    public @NonNull String fieldCurrentLoad() {
        return new String("current_load");
    }

    public @NonNull String fieldDswCreditsOnLoan() {
        return new String("dsw_credits_on_loan");
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

    public @NonNull String fieldNbFreeEntries() {
        return new String("free_entries");
    }


    public @NonNull String fieldNbRemainingEvents() {
        return new String("remaining");
    }

    public @NonNull String fieldNbNewEvents() {
        return new String("n_new");
    }

    public @NonNull String fieldPortId() {
        return new String("port_id");
    }


    public @NonNull String fieldNbLinks() {
        return new String("nb_links");
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

    public @NonNull String fieldQueues() {
        return new String("queues");
    }

    public @NonNull String fieldPriorities() {
        return new String("priorities");
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

    public @NonNull String fieldCount() {
        return new String("count");
    }

    public @NonNull String fieldFlowID() {
        return new String("flow_id");
    }
}

