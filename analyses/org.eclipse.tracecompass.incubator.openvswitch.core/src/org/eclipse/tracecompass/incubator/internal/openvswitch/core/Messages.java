package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */

@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.openvswitch.core.messages";  //$NON-NLS-1$

    public static @Nullable String IOpenVSwitchModel_HANDLERS;
    public static @Nullable String IOpenVSwitchModel_HANDLER_ID;
    public static @Nullable String IOpenVSwitchModel_UPCALLS;
    public static @Nullable String IOpenVSwitchModel_UPCALL;
    public static @Nullable String IOpenVSwitchModel_UPCALL_ID;
    public static @Nullable String IOpenVSwitchModel_UPCALL_TYPE;
    public static @Nullable String IOpenVSwitchModel_N_PROCESSED_UPCALLS;
    public static @Nullable String IOpenVSwitchModel_N_WAITING_UPCALLS;
    public static @Nullable String IOpenVSwitchModel_UPCALL_IN_PORT;
    public static @Nullable String IOpenVSwitchModel_UPCALL_STATUS;
    public static @Nullable String IOpenVSwitchModel_SOCK_ID;


    public static @Nullable String IOpenVSwitchModel_VPORTS;
    public static @Nullable String IOpenVSwitchModel_VPORT;
    public static @Nullable String IOpenVSwitchModel_VPORT_ID;
    public static @Nullable String IOpenVSwitchModel_N_SENT_UPCALLS;


    public static @Nullable String IOpenVSwitchModel_DATAPATHS;
    public static @Nullable String IOpenVSwitchModel_STATISTICS;
    public static @Nullable String IOpenVSwitchModel_FLOWS;
    public static @Nullable String IOpenVSwitchModel_NB_CURRENT_FLOWS;
    public static @Nullable String IOpenVSwitchModel_FLOW_STATE;
    public static @Nullable String IOpenVSwitchModel_NB_HIT;
    public static @Nullable String IOpenVSwitchModel_NB_EMC_CACHE_HIT;
    public static @Nullable String IOpenVSwitchModel_NB_MEGAFLOW_CACHE_HIT;
    public static @Nullable String IOpenVSwitchModel_TOT_NB_HIT;
    public static @Nullable String IOpenVSwitchModel_NB_MISS_UPCALLS;
    public static @Nullable String IOpenVSwitchModel_NB_USERSPACE_UPCALLS;
    public static @Nullable String IOpenVSwitchModel_NB_DUMP;
    public static @Nullable String IOpenVSwitchModel_NB_EVICTION;

    public static @Nullable String IOpenVSwitchModel_REVALIDATORS;
    public static @Nullable String IOpenVSwitchModel_REVALIDATOR_ID;
    public static @Nullable String IOpenVSwitchModel_FLOW_LIMIT;
    public static @Nullable String IOpenVSwitchModel_REVAL_DURATION;

    /* Cache Usage Data Provider */
    public static @Nullable String IOpenVSwitchModel_OvsCacheUsageDataProvider_TITLE;
    public static @Nullable String IOpenVSwitchModel_OvsCacheUsageDataProvider_EMC_CACHE;
    public static @Nullable String IOpenVSwitchModel_OvsCacheUsageDataProvider_MEGAFLOW_CACHE;

    /* Packet Rate Data Provider */
    public static @Nullable String IOpenVSwitchModel_OvsPacketRateDataProvider_TITLE;
    public static @Nullable String IOpenVSwitchModel_OvsUpcallRateDataProvider_TITLE;
    public static @Nullable String IOpenVSwitchModel_OvsRevalidationDataProvider_TITLE;

    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
