package org.eclipse.tracecompass.incubator.internal.dpdk.ui.vhost;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.vhost.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkVhostUserAvailDescrViewer_Title;
    public static @Nullable String DpdkVhostUserAvailDescrViewer_XAxis;
    public static @Nullable String DpdkVhostUserAvailDescrViewer_YAxis;

    public static @Nullable String DpdkVhostUserAvailDescrTreeViewer_DevName;

    public static @Nullable String DpdkVhostUserAvailDescrView_Title;
    public static @Nullable String DpdkVhostUserAvailDescrTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
