package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkLpmModelAttributes {

    @Nullable String LPM_TABS = Messages.IDpdkModel_LPM_TABS;
    @Nullable String TAB_NAME = Messages.IDpdkModel_TAB_NAME;
    @Nullable String TOT_NB_HIT = Messages.IDpdkModel_TOT_NB_HIT;
    @Nullable String TOT_NB_MISS = Messages.IDpdkModel_TOT_NB_MISS;
    @Nullable String LPM_RULES = Messages.IDpdkModel_LPM_RULES;
    @Nullable String RULE_ID = Messages.IDpdkModel_RULE_ID;

    @Nullable String NB_HIT = Messages.IDpdkModel_NB_HIT;
    @Nullable String NEXT_HOP = Messages.IDpdkModel_RULE_NEXT_HOP;

    @Nullable String DATA_PROVIDER_TITLE = Messages.IDpdkModel_DATA_PROVIDER_TITLE;

}
