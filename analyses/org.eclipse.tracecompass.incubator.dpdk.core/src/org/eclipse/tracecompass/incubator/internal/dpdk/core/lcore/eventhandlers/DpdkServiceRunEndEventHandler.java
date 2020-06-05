package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.DpdkCoreStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreModel;
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
public class DpdkServiceRunEndEventHandler extends DpdkEventHandler {

    DpdkCoreStateProvider fCoreStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public DpdkServiceRunEndEventHandler(@NonNull DpdkAnalysisEventLayout layout, DpdkCoreStateProvider stateProvider) {
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
            throw new IllegalArgumentException(layout.eventServiceRunBegin() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LogicalCoreModel core = fCoreStateProvier.getCore(lcoreId);
        ServiceModel service = core.getService(serviceId);


        if(service != null) {
            boolean success = core.updateServiceStatus(serviceId, ServiceStatus.PENDING, ts);
            if (!success) {
                Activator.getDefault().logError("Exception while building network state system"); //$NON-NLS-1$
            }
        }
    }
}
