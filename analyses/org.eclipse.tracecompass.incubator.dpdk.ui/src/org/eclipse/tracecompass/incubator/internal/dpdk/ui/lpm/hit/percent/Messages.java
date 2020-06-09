package org.eclipse.tracecompass.incubator.internal.dpdk.ui.lpm.hit.percent;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.lpm.hit.percent.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkLpmHitRateViewer_Title;
    public static @Nullable String DpdkLpmHitRateViewer_XAxis;
    public static @Nullable String DpdkLpmHitRateViewer_YAxis;

    public static @Nullable String DpdkLpmHitRateTreeViewer_TabName;

    public static @Nullable String DpdkLpmHitRateView_Title;
    public static @Nullable String DpdkLpmHitRateTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
