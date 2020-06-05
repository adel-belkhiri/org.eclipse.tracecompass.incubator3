package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkVhostModelAttributes {

    @Nullable String DEVICES    = Messages.IDpdkModel_DEVICES;
    @Nullable String IFACE_NAME = Messages.IDpdkModel_IFCE_NAME;
    @Nullable String HW_ADDR = Messages.IDpdkModel_HW_ADDR;
    @Nullable String VIDS = Messages.IDpdkModel_VIDS;
    @Nullable String RX_QUEUES = Messages.IDpdkModel_RX_QUEUES;
    @Nullable String TX_QUEUES = Messages.IDpdkModel_TX_QUEUES;
    @Nullable String CONNFD = Messages.IDpdkModel_CONNFD;
    @Nullable String AVAIL_DESCR = Messages.IDpdkModel_AVAIL_DESCR;
    @Nullable String NB_MBUF_DEQUEUE = Messages.IDpdkModel_NB_MBUF_DEQUEUE;
    @Nullable String NB_MBUF_ENQUEUE = Messages.IDpdkModel_NB_MBUF_ENQUEUE;

    @Nullable String DATA_PROVIDER_TITLE = Messages.IDpdkModel_DATA_PROVIDER_TITLE;

}
