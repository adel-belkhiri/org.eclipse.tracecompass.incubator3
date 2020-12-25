package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */
public class IOpenvSwitchAnalysisEventLayout {

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsFlowCmdNew() {
        return new String("ovs_flow_cmd_new"); //$NON-NLS-1$
    }


    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsProcessUpcall() {
        return new String("ovs_dpif:process_upcall"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsFlowCmdDel() {
        return new String("ovs_flow_cmd_del"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsEmcCacheHit() {
        return new String("emc_cache_hit"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsMegaflowCacheHit() {
        return new String("megaflow_cache_hit"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsFlowMatch() {
        return new String("ovs_flow_match"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsDpUpcall() {
        return new String("ovs_dp_upcall"); //$NON-NLS-1$
    }


    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsFlowLimitUpdate() {
        return new String("ovs_dpif:flow_limit_update"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsRevalidationStart() {
        return new String("ovs_dpif:revalidation_start"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsRevalidationStop() {
        return new String("ovs_dpif:revalidation_stop"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsSweepStart() {
        return new String("ovs_dpif:sweep_start"); //$NON-NLS-1$
    }

    /**
     * Get the
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsSweepStop() {
        return new String("ovs_dpif:sweep_stop"); //$NON-NLS-1$
    }

    /**
     * Get the name of
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsUpcallRead() {
        return new String("ovs_dpif:upcall_read"); //$NON-NLS-1$
    }

    /**
     * Get the name of
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventUpcallUserspaceExecStart() {
        return new String("ovs_dpif:upcall_userspace_exec_start"); //$NON-NLS-1$
    }

    /**
     * Get the name of
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventUpcallUserspaceExecEnd() {
        return new String("ovs_dpif:upcall_userspace_exec_end"); //$NON-NLS-1$
    }

    /**
     * Get the name of
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventDowncallTransactMultiple() {
        return new String("ovs_dpif:downcall_transact_multiple"); //$NON-NLS-1$
    }

    /**
     * Get the name
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsQueueAmsgController() {
        return new String("ovs_dpif:queue_amsg_controller"); //$NON-NLS-1$
    }

    /**
     * Get the name
     *
     * @return The name of
     * @since 3.0
     */
    public @NonNull String eventOvsUpcallReceive() {
        return new String("ovs_dpif:upcall_receive"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldUpcallsIdsLength() {
        return new String("_upcalls_ids_length"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldUpcallsIds() {
        return new String("upcalls_ids"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldUpcallType() {
        return new String("upcall_type"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldFlowLimit() {
        return new String("flow_limit"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldRevalidationDuration() {
        return new String("revalidation_duration"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldSlowPathReason() {
        return new String("slow_path_reason"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldPortName() {
        return new String("port_name"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldInPort() {
        return new String("in_port"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldError() {
        return new String("err"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldSkbMark() {
        return new String("skb_mark"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldPhyHeader() {
        return new String("phy"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldSwKey() {
        return new String("sw_key"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldHandlerId() {
        return new String("handler_id"); //$NON-NLS-1$
    }


    /**
     * @return xx
     */
    public @NonNull String fieldUfid() {
        return new String("ufid"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldDpName() {
        return new String("dp_name"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldDumpSeq() {
        return new String("dump_seq"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldRevalidatorId() {
        return new String("revalidator_id"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldTblIndex() {
        return new String("index"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldPriority() {
        return new String("priority"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldRecircId() {
        return new String("recirc_id"); //$NON-NLS-1$
    }


    /**
     * @return xx
     */
    public @NonNull String fieldEthAddr() {
        return new String("eth_addr"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldSrc() {
        return new String("src"); //$NON-NLS-1$
    }

    /**
     * @return xx
     */
    public @NonNull String fieldDst() {
        return new String("dst"); //$NON-NLS-1$
    }


    /**
     * @return xx
     */
    public @NonNull String fieldExtNetHdr() {
        return new String("ext_net_hdr"); //$NON-NLS-1$
    }

    public @NonNull String fieldMplsTopLse() {
        return new String("top_lse"); //$NON-NLS-1$
    }
}

