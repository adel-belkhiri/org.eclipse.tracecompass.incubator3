package org.eclipse.tracecompass.incubator.internal.dpdk.ui.flow.classifier.lookup.rate;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings({"javadoc", "nls"})
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.flow.classifier.lookup.rate.messages";

    public static @Nullable String DpdkFlowClassifierLookupHitRateViewer_Title;
    public static @Nullable String DpdkFlowClassifierLookupHitRateViewer_XAxis;
    public static @Nullable String DpdkFlowClassifierLookupHitRateViewer_YAxis;

    public static @Nullable String DpdkFlowClassifierLookupHitRateTreeViewer_ClassifierName;

    public static @Nullable String DpdkFlowClassifierLookupHitRateView_Title;
    public static @Nullable String DpdkFlowClassifierLookupHitRateTreeViewer_Legend;


    static {
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
