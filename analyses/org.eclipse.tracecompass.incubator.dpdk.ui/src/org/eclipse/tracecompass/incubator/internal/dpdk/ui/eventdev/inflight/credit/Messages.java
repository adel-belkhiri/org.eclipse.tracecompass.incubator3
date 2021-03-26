package org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.inflight.credit;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.inflight.credit.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkEventDevInflightCreditViewer_Title;
    public static @Nullable String DpdkEventDevInflightCreditViewer_XAxis;
    public static @Nullable String DpdkEventDevInflightCreditViewer_YAxis;

    public static @Nullable String DpdkEventDevInflightCreditTreeViewer_EventDevName;

    public static @Nullable String DpdkEventDevInflightCreditTreeViewer_Title;
    public static @Nullable String DpdkEventDevInflightCreditTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
