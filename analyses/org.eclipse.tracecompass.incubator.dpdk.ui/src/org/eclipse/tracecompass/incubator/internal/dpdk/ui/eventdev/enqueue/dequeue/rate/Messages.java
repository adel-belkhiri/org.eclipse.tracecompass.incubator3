package org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.enqueue.dequeue.rate;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.enqueue.dequeue.rate.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkEventDevEnqueueDequeueRateViewer_Title;
    public static @Nullable String DpdkEventDevEnqueueDequeueRateViewer_XAxis;
    public static @Nullable String DpdkEventDevEnqueueDequeueRateViewer_YAxis;

    public static @Nullable String DpdkEventDevEnqueueDequeueRateTreeViewer_EventDevName;

    public static @Nullable String DpdkEventDevEnqueueDequeueRateTreeViewer_Title;
    public static @Nullable String DpdkEventDevEnqueueDequeueRateTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
