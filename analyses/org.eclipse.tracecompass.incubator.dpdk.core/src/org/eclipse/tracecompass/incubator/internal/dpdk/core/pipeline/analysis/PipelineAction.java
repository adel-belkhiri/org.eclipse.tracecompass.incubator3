package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

public enum PipelineAction {
    /** Drop the packet */
    ACTION_DROP,

    /** Send packet to output port */
    ACTION_PORT,

    /** Send packet to output port read from packet meta-data */
    ACTION_PORT_META,

    /** Send packet to table */
    ACTION_TABLE,

    /** Number of reserved actions */
    ACTIONS;
}
