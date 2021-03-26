package org.eclipse.tracecompass.incubator.internal.dpdk.ui.pipeline.sw.queue.occupancy;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.pipeline.sw.queue.occupancy.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkPipelineSwQueueOccupancyViewer_Title;
    public static @Nullable String DpdkPipelineSwQueueOccupancyViewer_XAxis;
    public static @Nullable String DpdkPipelineSwQueueOccupancyViewer_YAxis;

    public static @Nullable String DpdkPipelineSwQueueOccupancyTreeViewer_DevName;

    public static @Nullable String DpdkPipelineSwQueueOccupancyView_Title;
    public static @Nullable String DpdkPipelineSwQueueOccupancyTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
