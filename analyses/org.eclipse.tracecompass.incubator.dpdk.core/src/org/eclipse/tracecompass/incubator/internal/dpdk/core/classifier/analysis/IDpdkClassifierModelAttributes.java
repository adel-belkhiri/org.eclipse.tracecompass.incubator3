package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author  Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkClassifierModelAttributes {

    @Nullable String IDpdkModel_CLASSIFIER_NAME = Messages.IDpdkModel_CLASSIFIER_NAME;
    @Nullable String IDpdkModel_TABLES = Messages.IDpdkModel_TABLES;
    @Nullable String IDpdkModel_TABLE_TYPE = Messages.IDpdkModel_TABLE_TYPE;
    @Nullable String IDpdkModel_TABLE_RULES = Messages.IDpdkModel_TABLE_RULES;

    @Nullable String NB_HIT = Messages.IDpdkModel_NB_HIT;
    @Nullable String TOT_NB_HIT = Messages.IDpdkModel_TOT_NB_HIT;
    @Nullable String TOT_NB_MISS = Messages.IDpdkModel_TOT_NB_MISS;
    @Nullable String NB_RULES = Messages.IDpdkModel_NB_RULES;
    @Nullable String RULE_ID = Messages.IDpdkModel_RULE_ID;

    @Nullable String PKTS_IN = Messages.IDpdkModel_PKTS_IN;
    @Nullable String PKTS_OUT = Messages.IDpdkModel_PKTS_OUT;

    @Nullable String HIT_PERCENT_METRIC_LABEL = Messages.IDpdkModel_HIT_PERCENT_METRIC_LABEL;
    @Nullable String MISS_PERCENT_METRIC_LABEL = Messages.IDpdkModel_MISS_PERCENT_METRIC_LABEL;

    @Nullable String IDpdkModel_PER_RULE_HIT_RATE_DATAPROVIDER_TITLE = Messages.IDpdkModel_PER_RULE_HIT_RATE_DATAPROVIDER_TITLE;
}
