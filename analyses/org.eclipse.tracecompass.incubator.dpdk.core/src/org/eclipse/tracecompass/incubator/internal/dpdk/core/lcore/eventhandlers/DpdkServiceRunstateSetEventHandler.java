package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.DpdkCoreStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.ServiceModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.ServiceStatus;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class DpdkServiceRunstateSetEventHandler extends DpdkEventHandler {

    DpdkCoreStateProvider fCoreStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public DpdkServiceRunstateSetEventHandler(@NonNull DpdkAnalysisEventLayout layout, DpdkCoreStateProvider stateProvider) {
        super(layout);
        this.fCoreStateProvier = stateProvider;
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer serviceId = content.getFieldValue(Integer.class, layout.fieldServiceId());
        Integer runState = content.getFieldValue(Integer.class, layout.fieldRunState());

        long ts = event.getTimestamp().getValue();


        if (serviceId == null || runState == null) {
            throw new IllegalArgumentException(layout.eventServiceRunstateSet() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        ArrayList<LogicalCoreModel> coresList = fCoreStateProvier.getMappedCores(serviceId);

        if(coresList.size() > 0) {
            for(LogicalCoreModel core : coresList) {
                ServiceModel service = core.getService(serviceId);
                if(service != null) {
                    core.updateServiceStatus(serviceId, runState == 1 ? ServiceStatus.ENABLED : ServiceStatus.DISABLED, ts);
                }
            }
        }
        else {
            ServiceModel service = fCoreStateProvier.getService(serviceId);
            if(service != null) {
                service.setActivationTimestamp(ts);
            }
        }
    }
}
