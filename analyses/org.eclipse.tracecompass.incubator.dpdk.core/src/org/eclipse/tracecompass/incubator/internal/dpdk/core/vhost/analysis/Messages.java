package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */

@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.messages";  //$NON-NLS-1$

    public static @Nullable String IDpdkModel_DEVICES;
    public static @Nullable String IDpdkModel_IFCE_NAME;
    public static @Nullable String IDpdkModel_HW_ADDR;
    public static @Nullable String IDpdkModel_VIDS;
    public static @Nullable String IDpdkModel_CONNFD;
    public static @Nullable String IDpdkModel_RX_QUEUES;
    public static @Nullable String IDpdkModel_TX_QUEUES;
    public static @Nullable String IDpdkModel_AVAIL_DESCR;
    public static @Nullable String IDpdkModel_NB_MBUF_DEQUEUE;
    public static @Nullable String IDpdkModel_NB_MBUF_ENQUEUE;

    public static @Nullable String IDpdkModel_DATA_PROVIDER_TITLE;

    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
