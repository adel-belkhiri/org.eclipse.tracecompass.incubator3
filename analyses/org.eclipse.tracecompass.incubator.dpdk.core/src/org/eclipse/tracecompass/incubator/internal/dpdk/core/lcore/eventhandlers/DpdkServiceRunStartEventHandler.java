package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.DpdkCoreStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreRole;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.ServiceModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.ServiceStatus;
import org.eclipse.tracecompass.internal.analysis.os.linux.core.Activator;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class DpdkServiceRunStartEventHandler extends DpdkEventHandler {

    DpdkCoreStateProvider fCoreStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public DpdkServiceRunStartEventHandler(@NonNull DpdkAnalysisEventLayout layout, DpdkCoreStateProvider stateProvider) {
        super(layout);
        this.fCoreStateProvier = stateProvider;
    }

    //id=0, lcore_id=1

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer serviceId = content.getFieldValue(Integer.class, layout.fieldServiceId());
        Integer lcoreId = content.getFieldValue(Integer.class, layout.fieldLcoreId());

        long ts = event.getTimestamp().getValue();


        if (serviceId == null || lcoreId == null) {
            throw new IllegalArgumentException(layout.eventServiceRunEnd() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LogicalCoreModel core = fCoreStateProvier.getCore(lcoreId);
        ServiceModel service = core.getService(serviceId);


        if(service == null) {
            service = fCoreStateProvier.getService(serviceId);
            if(service != null) {
                if(core.getRole() != LogicalCoreRole.LCORE_SERVICE) {
                    core.setRole(ts, LogicalCoreRole.LCORE_SERVICE);
                }
                core.mapService(service, ts);
            }
            else {
                Activator.getDefault().logError("Error : Service" + serviceId.toString() + "not registered"); //$NON-NLS-1$
                return;
            }
        }

        boolean success = core.updateServiceStatus(serviceId, ServiceStatus.RUN, ts);
        if (!success) {
            Activator.getDefault().logError("Exception while building the state system"); //$NON-NLS-1$
        }
    }
}