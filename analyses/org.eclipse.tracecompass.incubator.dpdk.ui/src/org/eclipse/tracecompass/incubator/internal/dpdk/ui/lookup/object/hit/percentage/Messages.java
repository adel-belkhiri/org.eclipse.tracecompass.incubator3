package org.eclipse.tracecompass.incubator.internal.dpdk.ui.lookup.object.hit.percentage;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.lookup.object.hit.percentage.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkLookupObjectHitRateViewer_Title;
    public static @Nullable String DpdkLookupObjectHitRateViewer_XAxis;
    public static @Nullable String DpdkLookupObjectHitRateViewer_YAxis;

    public static @Nullable String DpdkLookupObjectHitRateTreeViewer_ObjName;

    public static @Nullable String DpdkLookupObjectHitRateView_Title;
    public static @Nullable String DpdkLookupObjectHitRateTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
