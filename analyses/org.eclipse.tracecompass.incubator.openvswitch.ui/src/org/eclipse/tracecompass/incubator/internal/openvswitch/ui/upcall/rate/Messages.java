package org.eclipse.tracecompass.incubator.internal.openvswitch.ui.upcall.rate;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.openvswitch.ui.upcall.rate.messages";  //$NON-NLS-1$

    public static @Nullable String OvsUpcallRateViewer_Title;
    public static @Nullable String OvsUpcallRateViewer_XAxis;
    public static @Nullable String OvsUpcallRateViewer_YAxis;

    public static @Nullable String OvsUpcallRateTreeViewer_DevName;

    public static @Nullable String OvsUpcallRateView_Title;
    public static @Nullable String OvsUpcallRateTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
