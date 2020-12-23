package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author  Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkLpmModelAttributes {

    @Nullable String LOOKUP_OBJS = Messages.IDpdkModel_LOOKUP_OBJS;

    @Nullable String LPM_OBJS = Messages.IDpdkModel_LPM_OBJS;
    @Nullable String EM_OBJS = Messages.IDpdkModel_EM_OBJS;
    @Nullable String OBJ_NAME = Messages.IDpdkModel_OBJ_NAME;
    @Nullable String TOT_NB_HIT = Messages.IDpdkModel_TOT_NB_HIT;
    @Nullable String TOT_NB_MISS = Messages.IDpdkModel_TOT_NB_MISS;
    @Nullable String NB_RULES = Messages.IDpdkModel_NB_RULES;
    @Nullable String LPM_RULES = Messages.IDpdkModel_LPM_RULES;
    @Nullable String RULE_ID = Messages.IDpdkModel_RULE_ID;

    @Nullable String NB_HIT = Messages.IDpdkModel_NB_HIT;
    @Nullable String NEXT_HOP = Messages.IDpdkModel_RULE_NEXT_HOP;

    @NonNull String HIT_PERCENT_METRIC_LABEL = Messages.IDpdkModel_HIT_PERCENT_METRIC_LABEL;
    @NonNull String MISS_PERCENT_METRIC_LABEL = Messages.IDpdkModel_MISS_PERCENT_METRIC_LABEL;

    @Nullable String LPM_LOOKUP_DATA_PROVIDER_TITLE = Messages.IDpdkModel_LPM_LOOKUP_DATA_PROVIDER_TITLE;

}
