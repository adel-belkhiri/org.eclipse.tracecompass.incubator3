package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */

@SuppressWarnings({"javadoc", "nls"})
public class DpdkClassifierAnalysisEventLayout {

    /**
     * This event fires when a classifier is created
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteFlowClassifierCreate() {
        return new String("librte_flow_classify:rte_flow_classifier_create");
    }


    /**
     * This event fires when a classifier is deleted
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteFlowClassifierFree() {
        return new String("librte_flow_classify:rte_flow_classifier_free");
    }

    /**
     * This event fires when a table is added to a classifier
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteFlowClassifyTableCreate() {
        return new String("librte_flow_classify:rte_flow_classify_table_create");
    }

    /**
     * This event fires when a rule is added to a classifier table
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteFlowClassifyTableEntryAdd() {
        return new String("librte_flow_classify:rte_flow_classify_table_entry_add");
    }

    /**
     * This event fires when a rule is deleted from a classifier table
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteFlowClassifyTableEntryDelete() {
        return new String("librte_flow_classify:rte_flow_classify_table_entry_delete");
    }


    /**
     * This event fires when a rule is deleted from a classifier table
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteFlowClassifyLookup() {
        return new String("librte_flow_classify:flow_classifier_lookup");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldClsName() {
        return new String("cls_name");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldTblPointer() {
        return new String("tbl");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldTblName() {
        return new String("tbl_name");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldRuleId() {
        return new String("rule_id");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldRuleType() {
        return new String("rule_type");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldTableType() {
        return new String("type");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldKeyFound() {
        return new String("key_found");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldNbPacketsIn() {
        return new String("n_pkts_in");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldNbPacketsOut() {
        return new String("n_pkts_out");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldEntriesPtr() {
        return new String("entries_ptr");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldEntryPtr() {
        return new String("entry_ptr");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldDstIP() {
        return new String("dst_ip");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldDstIpMask() {
        return new String("dst_ip_mask");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldSrcIP() {
        return new String("src_ip");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldSrcIpMask() {
        return new String("src_ip_mask");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldDstPort() {
        return new String("dst_port");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldDstPortMask() {
        return new String("dst_port_mask");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldSrcPort() {
        return new String("src_port");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldSrcPortMask() {
        return new String("src_port_mask");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldProto() {
        return new String("proto");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldProtoMask() {
        return new String("proto_mask");
    }
}

