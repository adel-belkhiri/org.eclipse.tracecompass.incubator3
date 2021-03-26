package org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.port.current.load;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.port.current.load.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkEventdevPortCurrentLoadViewer_Title;
    public static @Nullable String DpdkEventdevPortCurrentLoadViewer_XAxis;
    public static @Nullable String DpdkEventdevPortCurrentLoadViewer_YAxis;

    public static @Nullable String DpdkEventdevPortCurrentLoadTreeViewer_EventDevName;

    public static @Nullable String DpdkEventdevPortCurrentLoadTreeViewer_Title;
    public static @Nullable String DpdkEventdevPortCurrentLoadTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
