package org.eclipse.tracecompass.incubator.internal.dpdk.ui.interpipeline.packet.rate;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings({"javadoc", "nls"})
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.interpipeline.packet.rate.messages";

    public static @Nullable String DpdkInterPipelinePacketRateViewer_Title;
    public static @Nullable String DpdkInterPipelinePacketRateViewer_XAxis;
    public static @Nullable String DpdkInterPipelinePacketRateViewer_YAxis;

    public static @Nullable String DpdkInterPipelinePacketRateTreeViewer_PipelineName;

    public static @Nullable String DpdkInterPipelinePacketRateView_Title;
    public static @Nullable String DpdkInterPipelinePacketRateTreeViewer_Legend;


    static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
