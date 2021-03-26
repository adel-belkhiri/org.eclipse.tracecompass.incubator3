package org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.ring.occupation;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.ring.occupation.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkEventDevRingOccupationViewer_Title;
    public static @Nullable String DpdkEventDevRingOccupationViewer_XAxis;
    public static @Nullable String DpdkEventDevRingOccupationViewer_YAxis;

    public static @Nullable String DpdkEventDevRingOccupationTreeViewer_EventDevName;

    public static @Nullable String DpdkEventDevRingOccupationTreeViewer_Title;
    public static @Nullable String DpdkEventDevRingOccupationTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
