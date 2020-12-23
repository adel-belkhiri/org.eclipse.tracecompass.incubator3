package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author  Adel Belkhiri
 *
 */

@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.messages";  //$NON-NLS-1$

    public static @Nullable String IDpdkModel_LOOKUP_OBJS;
    public static @Nullable String IDpdkModel_LPM_OBJS;
    public static @Nullable String IDpdkModel_EM_OBJS;

    public static @Nullable String IDpdkModel_OBJ_NAME;
    public static @Nullable String IDpdkModel_TOT_NB_HIT;
    public static @Nullable String IDpdkModel_TOT_NB_MISS;
    public static @Nullable String IDpdkModel_LPM_RULES;
    public static @Nullable String IDpdkModel_NB_RULES;
    public static @Nullable String IDpdkModel_RULE_ID;
    public static @Nullable String IDpdkModel_RULE_NEXT_HOP;
    public static @Nullable String IDpdkModel_NB_HIT;

    public static @Nullable String IDpdkModel_LPM_LOOKUP_DATA_PROVIDER_TITLE;
    public static String IDpdkModel_HIT_PERCENT_METRIC_LABEL;
    public static String IDpdkModel_MISS_PERCENT_METRIC_LABEL;

    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
