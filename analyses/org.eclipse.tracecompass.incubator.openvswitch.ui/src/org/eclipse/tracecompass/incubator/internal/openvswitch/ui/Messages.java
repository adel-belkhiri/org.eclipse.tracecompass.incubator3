package org.eclipse.tracecompass.incubator.internal.openvswitch.ui;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.openvswitch.ui.messages";  //$NON-NLS-1$

    public static @Nullable String OvsCacheUsageTreeViewer_Title;
    public static @Nullable String OvsCacheUsageTreeViewer_XAxis;
    public static @Nullable String OvsCacheUsageTreeViewer_YAxis;

    public static @Nullable String OvsCacheUsageTreeViewer_CacheName;
    public static @Nullable String OvsCacheUsageTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
