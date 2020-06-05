package org.eclipse.tracecompass.incubator.internal.dpdk.ui.vhost.packet.rate;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.vhost.packet.rate.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkVhostUserPacketRateViewer_Title;
    public static @Nullable String DpdkVhostUserPacketRateViewer_XAxis;
    public static @Nullable String DpdkVhostUserPacketRateViewer_YAxis;

    public static @Nullable String DpdkVhostUserPacketRateTreeViewer_DevName;

    public static @Nullable String DpdkVhostUserPacketRateView_Title;
    public static @Nullable String DpdkVhostUserPacketRateTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
