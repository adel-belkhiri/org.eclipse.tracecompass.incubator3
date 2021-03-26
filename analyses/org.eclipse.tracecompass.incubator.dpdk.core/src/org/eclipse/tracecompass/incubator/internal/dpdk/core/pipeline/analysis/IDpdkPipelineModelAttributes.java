package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author  Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkPipelineModelAttributes {

    @Nullable String PIPELINES = Messages.IDpdkModel_PIPELINES;

    @Nullable String IDpdkModel_PORT_NAME = Messages.IDpdkModel_PORT_NAME;
    @Nullable String IDpdkModel_TAB_NAME = Messages.IDpdkModel_TAB_NAME;
    @Nullable String IDpdkModel_TABLES = Messages.IDpdkModel_TABLES;
    @Nullable String IDpdkModel_TABLE_TYPE = Messages.IDpdkModel_TABLE_TYPE;

    @Nullable String NB_HIT = Messages.IDpdkModel_NB_HIT;
    @Nullable String NB_MISS = Messages.IDpdkModel_NB_MISS;
    @Nullable String DEFAULT_ACTION = Messages.IDpdkModel_DEFAULT_ACTION;
    @Nullable String CHILD_TABLE = Messages.IDpdkModel_CHILD_TABLE;
    @Nullable String FWD_CHILD_TABLE = Messages.IDpdkModel_FWD_CHILD_TABLE;

    @Nullable String TOT_NB_HIT = Messages.IDpdkModel_TOT_NB_HIT;
    @Nullable String TOT_NB_MISS = Messages.IDpdkModel_TOT_NB_MISS;
    @Nullable String NB_RULES = Messages.IDpdkModel_NB_RULES;
    @Nullable String RULE_ID = Messages.IDpdkModel_RULE_ID;

    @Nullable String NB_RX = Messages.IDpdkModel_NB_RX;
    @Nullable String NB_PKTS_DROP = Messages.IDpdkModel_NB_PKTS_DROP;
    @Nullable String NB_TX = Messages.IDpdkModel_NB_TX;

    @Nullable String PORTS = Messages.IDpdkModel_PORTS;
    String IN_PORTS = Messages.IDpdkModel_IN_PORTS;
    String OUT_PORTS = Messages.IDpdkModel_OUT_PORTS;
    @Nullable String PORT_STATUS = Messages.IDpdkModel_PORT_STATUS;
    @Nullable String IDpdkModel_PORT_TYPE = Messages.IDpdkModel_PORT_TYPE;
    @Nullable String ZERO_POLLS = Messages.IDpdkModel_ZERO_POLLS;
    @Nullable String NON_ZERO_POLLS = Messages.IDpdkModel_NON_ZERO_POLLS;

    @Nullable String QUEUE_LATENCY = Messages.IDpdkModel_QUEUE_LATENCY;
    @Nullable String QUEUE_CAPACITY = Messages.IDpdkModel_QUEUE_CAPACITY;
    @Nullable String SW_QUEUES = Messages.IDpdkModel_QUEUES;
    @Nullable String NB_PKT = Messages.IDpdkModel_NB_PKT;

    @Nullable String HIT_PERCENT_METRIC_LABEL = Messages.IDpdkModel_HIT_PERCENT_METRIC_LABEL;
    @Nullable String MISS_PERCENT_METRIC_LABEL = Messages.IDpdkModel_MISS_PERCENT_METRIC_LABEL;

    String TRAFIC_IN_LABEL = Messages.IDpdkModel_TRAFIC_IN_LABEL;
    String TRAFIC_OUT_LABEL = Messages.IDpdkModel_TRAFIC_OUT_LABEL;

    @Nullable String IDpdkModel_PER_PORT_PACKET_RATE_DATAPROVIDER_TITLE = Messages.IDpdkModel_PER_PORT_PACKET_RATE_DATAPROVIDER_TITLE;
    @Nullable String IDpdkModel_INTER_PIPELINE_PACKET_RATE_DATAPROVIDER_TITLE = Messages.IDpdkModel_INTER_PIPELINE_PACKET_RATE_DATAPROVIDER_TITLE;
    @Nullable String IDpdkModel_RING_QUEUE_OCCUPANCY_DATAPROVIDER_TITLE = Messages.IDpdkModel_RING_QUEUE_OCCUPANCY_DATAPROVIDER_TITLE;
    @Nullable String IDpdkModel_RING_QUEUE_LATENCY_DATAPROVIDER_TITLE = Messages.IDpdkModel_RING_QUEUE_LATENCY_DATAPROVIDER_TITLE;

}
