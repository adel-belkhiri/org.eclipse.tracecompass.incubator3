package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author  Adel Belkhiri
 *
 */

@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.messages";  //$NON-NLS-1$


    public static @Nullable String IDpdkModel_CLASSIFIER_NAME;
    public static @Nullable String IDpdkModel_TABLES;
    public static @Nullable String IDpdkModel_TABLE_TYPE;
    public static @Nullable String IDpdkModel_TABLE_RULES;

    public static @Nullable String IDpdkModel_TOT_NB_HIT;
    public static @Nullable String IDpdkModel_TOT_NB_MISS;

    public static @Nullable String  IDpdkModel_PKTS_IN;
    public static @Nullable String  IDpdkModel_PKTS_OUT;

    public static @Nullable String IDpdkModel_NB_RULES;
    public static @Nullable String IDpdkModel_RULE_ID;
    public static @Nullable String IDpdkModel_NB_HIT;

    public static String IDpdkModel_HIT_PERCENT_METRIC_LABEL;
    public static String IDpdkModel_MISS_PERCENT_METRIC_LABEL;

    public static String IDpdkModel_PER_RULE_HIT_RATE_DATAPROVIDER_TITLE;

    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
