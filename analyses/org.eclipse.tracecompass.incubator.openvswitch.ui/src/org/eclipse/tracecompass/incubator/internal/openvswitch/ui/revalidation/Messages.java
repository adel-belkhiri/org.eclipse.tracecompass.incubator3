package org.eclipse.tracecompass.incubator.internal.openvswitch.ui.revalidation;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.openvswitch.ui.revalidation.messages";  //$NON-NLS-1$

    public static @Nullable String OvsRevalidationViewer_Title;
    public static @Nullable String OvsRevalidationViewer_XAxis;
    public static @Nullable String OvsRevalidationViewer_YAxis;

    public static @Nullable String OvsRevalidationTreeViewer_DatapathName;

    public static @Nullable String OvsRevalidationView_Title;
    public static @Nullable String OvsRevalidationTreeViewer_Legend;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
