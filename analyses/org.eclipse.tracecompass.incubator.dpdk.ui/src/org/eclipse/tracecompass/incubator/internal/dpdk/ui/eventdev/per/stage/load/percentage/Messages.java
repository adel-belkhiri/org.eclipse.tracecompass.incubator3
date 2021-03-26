package org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.per.stage.load.percentage;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.per.stage.load.percentage.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkEventDevPerStageLoadPercentageViewer_Title;
    public static @Nullable String DpdkEventDevPerStageLoadPercentageViewer_XAxis;
    public static @Nullable String DpdkEventDevPerStageLoadPercentageViewer_YAxis;

    public static @Nullable String DpdkEventDevPerStageLoadPercentageTreeViewer_EventDevName;

    public static @Nullable String DpdkEventDevPerStageLoadPercentageTreeViewer_Title;
    public static @Nullable String DpdkEventDevPerStageLoadPercentageTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
