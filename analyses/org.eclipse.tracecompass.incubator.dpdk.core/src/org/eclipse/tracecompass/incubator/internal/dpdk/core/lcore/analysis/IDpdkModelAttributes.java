package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public interface IDpdkModelAttributes {

    @Nullable String LCORES = Messages.IDpdkModel_LCORES;
    @Nullable String LCORE_ROLE = Messages.IDpdkModel_LCORE_ROLE;
    @Nullable String LCORE_STATUS = Messages.IDpdkModel_LCORE_STATUS;
    @Nullable String LCORE_FUNCTION = Messages.IDpdkModel_LCORE_FUNCTION;

    @Nullable String SERVICES = Messages.IDpdkModel_SERVICES;
    @Nullable String SERVICE_NAME = Messages.IDpdkModel_SERVICE_NAME;
    @Nullable String SERVICE_STATUS = Messages.IDpdkModel_SERVICE_STATUS;
    @Nullable String SERVICE_CALLBACK = Messages.IDpdkModel_SERVICE_CALLBACK;

}
