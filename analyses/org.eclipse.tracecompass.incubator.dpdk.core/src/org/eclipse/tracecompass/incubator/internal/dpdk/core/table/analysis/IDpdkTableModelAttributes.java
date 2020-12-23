package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author  Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkTableModelAttributes {

    @Nullable String IDpdkModel_HASH_TAB = Messages.IDpdkModel_HASH_TAB;
    @Nullable String IDpdkModel_ACL_TAB = Messages.IDpdkModel_ACL_TAB;
    @Nullable String IDpdkModel_LPM_TAB = Messages.IDpdkModel_LPM_TAB;

    @Nullable String NB_RULES = Messages.IDpdkModel_NB_RULES;

    @Nullable String TOT_NB_HIT = Messages.IDpdkModel_TOT_NB_HIT;
    @Nullable String TOT_NB_MISS = Messages.IDpdkModel_TOT_NB_MISS;

    @Nullable String NB_HIT = Messages.IDpdkModel_NB_HIT;

    @NonNull String HIT_PERCENT_METRIC_LABEL = Messages.IDpdkModel_HIT_PERCENT_METRIC_LABEL;
    @NonNull String MISS_PERCENT_METRIC_LABEL = Messages.IDpdkModel_MISS_PERCENT_METRIC_LABEL;

    @Nullable String IDpdkModel_PER_RULE_HIT_RATE_DATAPROVIDER_TITLE = Messages.IDpdkModel_PER_RULE_HIT_RATE_DATAPROVIDER_TITLE;
}
