package org.eclipse.tracecompass.incubator.internal.dpdk.ui.pipeline.sw.queue.latency;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.pipeline.sw.queue.latency.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkPipelineSwQueueLatencyViewer_Title;
    public static @Nullable String DpdkPipelineSwQueueLatencyViewer_XAxis;
    public static @Nullable String DpdkPipelineSwQueueLatencyViewer_YAxis;

    public static @Nullable String DpdkPipelineSwQueueLatencyTreeViewer_DevName;

    public static @Nullable String DpdkPipelineSwQueueLatencyView_Title;
    public static @Nullable String DpdkPipelineSwQueueLatencyTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
