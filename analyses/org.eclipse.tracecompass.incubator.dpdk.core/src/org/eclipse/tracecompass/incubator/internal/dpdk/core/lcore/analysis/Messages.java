package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */

@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.messages";  //$NON-NLS-1$

    public static @Nullable String IDpdkModel_LCORES;
    public static @Nullable String IDpdkModel_LCORE_ROLE;
    public static @Nullable String IDpdkModel_LCORE_STATUS;
    public static @Nullable String IDpdkModel_LCORE_FUNCTION;

    public static @Nullable String IDpdkModel_SERVICES;
    public static @Nullable String IDpdkModel_SERVICE_NAME;
    public static @Nullable String IDpdkModel_SERVICE_STATUS;
    public static @Nullable String IDpdkModel_SERVICE_CALLBACK;

    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
