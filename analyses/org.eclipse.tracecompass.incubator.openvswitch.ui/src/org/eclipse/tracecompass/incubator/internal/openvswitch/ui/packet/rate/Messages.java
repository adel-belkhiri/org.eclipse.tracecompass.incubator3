package org.eclipse.tracecompass.incubator.internal.openvswitch.ui.packet.rate;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.openvswitch.ui.packet.rate.messages";  //$NON-NLS-1$

    public static @Nullable String OvsPacketRateViewer_Title;
    public static @Nullable String OvsPacketRateViewer_XAxis;
    public static @Nullable String OvsPacketRateViewer_YAxis;

    public static @Nullable String OvsPacketRateTreeViewer_DevName;

    public static @Nullable String OvsPacketRateView_Title;
    public static @Nullable String OvsPacketRateTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
