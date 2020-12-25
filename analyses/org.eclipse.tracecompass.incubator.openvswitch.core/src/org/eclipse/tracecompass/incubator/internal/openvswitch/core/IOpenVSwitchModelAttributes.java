package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public interface IOpenVSwitchModelAttributes {

    @Nullable String HANDLERS = Messages.IOpenVSwitchModel_HANDLERS;
    @Nullable String HANDLER_ID = Messages.IOpenVSwitchModel_HANDLER_ID;
    @Nullable String UPCALLS = Messages.IOpenVSwitchModel_UPCALLS;
    @Nullable String UPCALL = Messages.IOpenVSwitchModel_UPCALL;
    @Nullable String UPCALL_ID = Messages.IOpenVSwitchModel_UPCALL_ID;
    @Nullable String UPCALL_TYPE = Messages.IOpenVSwitchModel_UPCALL_TYPE;
    @Nullable String N_PROCESSED_UPCALLS = Messages.IOpenVSwitchModel_N_PROCESSED_UPCALLS;
    @Nullable String N_WAITING_UPCALLS = Messages.IOpenVSwitchModel_N_WAITING_UPCALLS;
    @Nullable String UPCALL_IN_PORT = Messages.IOpenVSwitchModel_UPCALL_IN_PORT;
    @Nullable String SOCK_ID = Messages.IOpenVSwitchModel_SOCK_ID;
    @Nullable String UPCALL_STATUS = Messages.IOpenVSwitchModel_UPCALL_STATUS;

    @Nullable String VPORTS = Messages.IOpenVSwitchModel_VPORTS;
    @Nullable String VPORT = Messages.IOpenVSwitchModel_VPORT;
    @Nullable String VPORT_ID = Messages.IOpenVSwitchModel_VPORT_ID;
    @Nullable String N_SENT_UPCALLS = Messages.IOpenVSwitchModel_N_SENT_UPCALLS;

    @Nullable String DATAPATHS = Messages.IOpenVSwitchModel_DATAPATHS;
    @Nullable String STATISTICS = Messages.IOpenVSwitchModel_STATISTICS;
    @Nullable String FLOWS = Messages.IOpenVSwitchModel_FLOWS;
    @Nullable String NB_CURRENT_FLOWS = Messages.IOpenVSwitchModel_NB_CURRENT_FLOWS;
    @Nullable String FLOW_STATE = Messages.IOpenVSwitchModel_FLOW_STATE;
    @Nullable String TOT_NB_HIT = Messages.IOpenVSwitchModel_TOT_NB_HIT;
    @Nullable String NB_HIT = Messages.IOpenVSwitchModel_NB_HIT;
    @Nullable String NB_EMC_CACHE_HIT = Messages.IOpenVSwitchModel_NB_EMC_CACHE_HIT;
    @Nullable String NB_MISS_UPCALLS = Messages.IOpenVSwitchModel_NB_MISS_UPCALLS;
    @Nullable String NB_USERSPACE_UPCALLS = Messages.IOpenVSwitchModel_NB_USERSPACE_UPCALLS;
    @Nullable String NB_MEGAFLOW_CACHE_HIT = Messages.IOpenVSwitchModel_NB_MEGAFLOW_CACHE_HIT;
    @Nullable String NB_DUMP = Messages.IOpenVSwitchModel_NB_DUMP;
    @Nullable String NB_EVICTION = Messages.IOpenVSwitchModel_NB_DUMP;

    @Nullable String REVALIDATORS = Messages.IOpenVSwitchModel_REVALIDATORS;
    @Nullable String REVALIDATOR_ID = Messages.IOpenVSwitchModel_REVALIDATOR_ID;
    @Nullable String REVAL_DURATION = Messages.IOpenVSwitchModel_REVAL_DURATION;
    @Nullable String FLOW_LIMIT = Messages.IOpenVSwitchModel_FLOW_LIMIT;

    @Nullable String CACHE_USAGE_DATAPROVIDER_TITLE = Messages.IOpenVSwitchModel_OvsCacheUsageDataProvider_TITLE;
    @Nullable String EMC_CACHE = Messages.IOpenVSwitchModel_OvsCacheUsageDataProvider_EMC_CACHE;
    @Nullable String MEGAFLOW_CACHE = Messages.IOpenVSwitchModel_OvsCacheUsageDataProvider_MEGAFLOW_CACHE;

    @Nullable String PACKET_RATE_DATAPROVIDER_TITLE = Messages.IOpenVSwitchModel_OvsPacketRateDataProvider_TITLE;

    @Nullable String UPCALL_RATE_DATAPROVIDER_TITLE = Messages.IOpenVSwitchModel_OvsUpcallRateDataProvider_TITLE;

    @Nullable String REVALIDATION_DATAPROVIDER_TITLE = Messages.IOpenVSwitchModel_OvsRevalidationDataProvider_TITLE;

}
