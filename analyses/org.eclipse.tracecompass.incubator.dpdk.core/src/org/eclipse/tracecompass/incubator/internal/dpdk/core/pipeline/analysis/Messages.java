package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author  Adel Belkhiri
 *
 */

@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.messages";  //$NON-NLS-1$

    public static @Nullable String IDpdkModel_PIPELINES;
    public static @Nullable String IDpdkModel_PORT_NAME;

    public static @Nullable String IDpdkModel_TABLES;
    public static @Nullable String IDpdkModel_TABLE_TYPE;
    public static @Nullable String IDpdkModel_TAB_NAME;

    public static @Nullable String IDpdkModel_TOT_NB_HIT;
    public static @Nullable String IDpdkModel_TOT_NB_MISS;

    public static @Nullable String  IDpdkModel_NB_RX;
    public static @Nullable String  IDpdkModel_NB_TX;

    public static @Nullable String  IDpdkModel_PORTS;
    public static @Nullable String  IDpdkModel_IN_PORTS;
    public static @Nullable String  IDpdkModel_OUT_PORTS;
    public static @Nullable String  IDpdkModel_PORT_STATUS;
    public static @Nullable String  IDpdkModel_PORT_TYPE;

    public static @Nullable String  IDpdkModel_QUEUE_LATENCY;
    public static @Nullable String  IDpdkModel_QUEUE_CAPACITY;
    public static @Nullable String  IDpdkModel_QUEUES;
    public static @Nullable String  IDpdkModel_NB_PKT;

    public static @Nullable String IDpdkModel_NB_RULES;
    public static @Nullable String IDpdkModel_RULE_ID;

    public static @Nullable String IDpdkModel_NB_HIT;
    public static @Nullable String IDpdkModel_NB_MISS;
    public static @Nullable String IDpdkModel_NB_PKTS_DROP;
    public static @Nullable String IDpdkModel_DEFAULT_ACTION;
    public static @Nullable String IDpdkModel_CHILD_TABLE;
    public static @Nullable String IDpdkModel_FWD_CHILD_TABLE;

    public static String IDpdkModel_HIT_PERCENT_METRIC_LABEL;
    public static String IDpdkModel_MISS_PERCENT_METRIC_LABEL;

    public static String IDpdkModel_TRAFIC_IN_LABEL;
    public static String IDpdkModel_TRAFIC_OUT_LABEL;

    public static String IDpdkModel_PER_PORT_PACKET_RATE_DATAPROVIDER_TITLE;
    public static String IDpdkModel_INTER_PIPELINE_PACKET_RATE_DATAPROVIDER_TITLE;
    public static String IDpdkModel_RING_QUEUE_OCCUPANCY_DATAPROVIDER_TITLE;
    public static String IDpdkModel_RING_QUEUE_LATENCY_DATAPROVIDER_TITLE;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
