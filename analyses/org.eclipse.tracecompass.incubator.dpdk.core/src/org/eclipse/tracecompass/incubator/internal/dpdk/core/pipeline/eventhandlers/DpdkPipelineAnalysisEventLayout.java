package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */

@SuppressWarnings({"javadoc", "nls"})
public class DpdkPipelineAnalysisEventLayout {

    /** Ring Port events */
    public @NonNull String eventRtePortRingReaderCreate() {
        return new String("librte_port_ring:rte_port_ring_reader_create");
    }

    public @NonNull String eventRtePortRingWriterCreate() {
        return new String("librte_port_ring:rte_port_ring_writer_create");
    }

    public @NonNull String eventRtePortRingReaderRx() {
        return new String("librte_port_ring:rte_port_ring_reader_rx");
    }

    public @NonNull String eventRtePortRingWriterTx() {
        return new String("librte_port_ring:send_burst");
    }

    /** Ethdev events */
    public @NonNull String eventRteEthDevConfigure() {
        return new String("librte_ethdev:rte_eth_dev_configure");
    }

    /** Ethdev Port events */
    public @NonNull String eventRtePortEthdevReaderCreate() {
        return new String("librte_port_ethdev:rte_port_ethdev_reader_create");
    }

    public @NonNull String eventRtePortEthdevWriterCreate() {
        return new String("librte_port_ethdev:rte_port_ethdev_writer_create");
    }


    public @NonNull String eventRtePortSinkCreate() {
        return new String("librte_port_source_sink:rte_port_sink_create");
    }

    public @NonNull String eventRtePortSourceCreate() {
        return new String("librte_port_source_sink:rte_port_source_create");
    }

    public @NonNull String eventRtePortEthdevReaderRx() {
        return new String("librte_port_ethdev:rte_port_ethdev_reader_rx");
    }

    public @NonNull String eventRtePortSinkTx() {
        return new String("librte_port_source_sink:rte_port_sink_tx");
    }

    public @NonNull String eventRtePortSourceRx() {
        return new String("librte_port_source_sink:rte_port_source_rx");
    }

    public @NonNull String eventRtePortEthdevWriterTx() {
        return new String("librte_port_ethdev:send_burst");
    }

    public @NonNull String eventRtePipelineCreate() {
        return new String("librte_pipeline:rte_pipeline_create");
    }

    public @NonNull String eventRtePipelineFree() {
        return new String("librte_pipeline:rte_pipeline_free");
    }

    public @NonNull String eventRtePipelineInputPortCreate() {
        return new String("librte_pipeline:rte_pipeline_port_in_create");
    }

    public @NonNull String eventRtePipelinePortOutCreate() {
        return new String("librte_pipeline:rte_pipeline_port_out_create");
    }

    public @NonNull String eventRtePipelineInputPortFree() {
        return new String("librte_pipeline:rte_pipeline_port_in_free");
    }

    public @NonNull String eventRtePipelineOutputPortFree() {
        return new String("librte_pipeline:rte_pipeline_port_out_free");
    }

    public @NonNull String eventRtePipelinePortInConnectToTable() {
        return new String("librte_pipeline:rte_pipeline_port_in_connect_to_table");
    }

    public @NonNull String eventRtePipelineInputPortEnable() {
        return new String("librte_pipeline:rte_pipeline_port_in_enable");
    }

    public @NonNull String eventRtePipelineInputPortDisable() {
        return new String("librte_pipeline:rte_pipeline_port_in_disable");
    }


    public @NonNull String eventRtePipelineInputPortActionHandlerDrop() {
        return new String("librte_pipeline:port_in_ah_drop");
    }

    public @NonNull String eventRtePipelineOutputPortActionHandlerDrop() {
        return new String("librte_pipeline:port_out_ah_drop");
    }

    public @NonNull String eventRtePipelineTableCreate() {
        return new String("librte_pipeline:rte_pipeline_table_create");
    }

    public @NonNull String eventRtePipelineTableFree() {
        return new String("librte_pipeline:rte_pipeline_table_free");
    }

    public @NonNull String eventRtePipelineForwardToNextTable() {
        return new String("librte_pipeline:forward_to_next_table");
    }

    public @NonNull String eventRtePipelineTableDefaultEntryAdd() {
        return new String("librte_pipeline:rte_pipeline_table_default_entry_add");
    }

    public @NonNull String eventRtePipelineSetNextTableID() {
        return new String("librte_pipeline:set_next_table_id");
    }

    public @NonNull String eventRtePipelineTableDefaultEntryDelete() {
        return new String("librte_pipeline:rte_pipeline_table_default_entry_delete");
    }

    public @NonNull String eventRtePipelineTableEntryAdd() {
        return new String("librte_pipeline:rte_pipeline_table_entry_add");
    }

    public @NonNull String eventRtePipelineTableDropByLookupHitAH() {
        return new String("librte_pipeline:table_drop_by_lkp_hit_ah");
    }

    public @NonNull String eventRtePipelineTableDropByLookupMissAH() {
        return new String("librte_pipeline:table_drop_by_lkp_miss_ah");
    }

    public @NonNull String eventRtePipelineTableDropByLookupHit() {
        return new String("librte_pipeline:table_drop_by_lkp_hit");
    }

    public @NonNull String eventRtePipelineTableDropByLookupMiss() {
        return new String("librte_pipeline:table_drop_by_lkp_miss");
    }

    public @NonNull String eventRteTableAclCreate() {
        return new String("librte_table_acl:rte_table_acl_create");
    }

    public @NonNull String eventRteTableLpmCreate() {
        return new String("librte_table_lpm:rte_table_lpm_create");
    }

    public @NonNull String eventRteTableArrayCreate() {
        return new String("librte_table_array:rte_table_array_create");
    }

    /**
     *
     * @return
     */
    public @NonNull String fieldName() {
        return new String("name");
    }

    public @NonNull String fieldPipeline() {
        return new String("p");
    }

    public @NonNull String fieldPortIdx() {
        return new String("port_id");
    }

    public @NonNull String fieldPortPtr() {
        return new String("h_port");
    }

    public @NonNull String fieldPort() {
        return new String("port");
    }

    public @NonNull String fieldQueueId() {
        return new String("queue_id");
    }

    public @NonNull String fieldMaxNbPkts() {
        return new String("max_n_pkts");
    }

    public @NonNull String fieldNbBytesPerPkt() {
        return new String("n_bytes_per_pkt");
    }

    public @NonNull String fieldPcapFileName() {
        return new String("pcap_file_name");
    }

    public @NonNull String fieldNbRxQueues() {
        return new String("nb_rx_q");
    }


    public @NonNull String fieldNbTxQueues() {
        return new String("nb_tx_q");
    }

    public @NonNull String fieldNbRx() {
        return new String("rx_pkt_cnt");
    }

    public @NonNull String fieldNbTx() {
        return new String("tx_pkt_cnt");
    }

    public @NonNull String fieldNbPkts() {
        return new String("nb_pkts");
    }

    public @NonNull String fieldPortQueueSize() {
        return new String("capacity");
    }

    public @NonNull String fieldFuncAction() {
        return new String("f_action");
    }

    public @NonNull String fieldBurstSize() {
        return new String("burst_size");
    }


    public @NonNull String fieldNbPktsDrop() {
        return new String("nb_pkts_drop");
    }

    public @NonNull String fieldTxBurstSize() {
        return new String("tx_burst_sz");
    }

    public @NonNull String fieldTblPointer() {
        return new String("tbl");
    }

    public @NonNull String fieldTblName() {
        return new String("tbl_name");
    }

    public @NonNull String fieldTableIdx() {
        return new String("table_id");
    }

    public @NonNull String fieldFromTableIdx() {
        return new String("from_tbl_id");
    }

    public @NonNull String fieldToTableIdx() {
        return new String("to_tbl_id");
    }

    public @NonNull String fieldTablePtr() {
        return new String("h_table");
    }

    public @NonNull String fieldDefaultAction() {
        return new String("action");
    }

    public @NonNull String fieldNextPortOrTableId() {
        return new String("next_port_table_id");
    }

    public @NonNull String fieldNextTableIdx() {
        return new String("next_table_id");
    }

    public @NonNull String fieldRuleId() {
        return new String("rule_id");
    }

    public @NonNull String fieldRuleType() {
        return new String("rule_type");
    }

    public @NonNull String fieldTableType() {
        return new String("type");
    }

    public @NonNull String fieldKeyFound() {
        return new String("key_found");
    }

    public @NonNull String fieldNbPacketsIn() {
        return new String("n_pkts_in");
    }

    public @NonNull String fieldNbPacketsOut() {
        return new String("n_pkts_out");
    }

    public @NonNull String fieldEntriesPtr() {
        return new String("entries_ptr");
    }

    public @NonNull String fieldEntryPtr() {
        return new String("entry_ptr");
    }

    public @NonNull String fieldDstIP() {
        return new String("dst_ip");
    }

    public @NonNull String fieldDstIpMask() {
        return new String("dst_ip_mask");
    }

    public @NonNull String fieldSrcIP() {
        return new String("src_ip");
    }

    public @NonNull String fieldSrcIpMask() {
        return new String("src_ip_mask");
    }

    public @NonNull String fieldDstPort() {
        return new String("dst_port");
    }

    public @NonNull String fieldDstPortMask() {
        return new String("dst_port_mask");
    }

    public @NonNull String fieldSrcPort() {
        return new String("src_port");
    }

    public @NonNull String fieldSrcPortMask() {
        return new String("src_port_mask");
    }

    public @NonNull String fieldProto() {
        return new String("proto");
    }

    public @NonNull String fieldProtoMask() {
        return new String("proto_mask");
    }
}

