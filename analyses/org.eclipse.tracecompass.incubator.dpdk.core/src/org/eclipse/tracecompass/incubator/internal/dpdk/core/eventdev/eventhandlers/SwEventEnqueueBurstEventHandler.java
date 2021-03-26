package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.EventDevModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.PortModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class SwEventEnqueueBurstEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public SwEventEnqueueBurstEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer backendId = content.getFieldValue(Integer.class, layout.fieldSw());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortId());

        Integer nbEnqEvents = content.getFieldValue(Integer.class, layout.fieldNbEnqEvents());
        Integer nbNewEnqEvents = content.getFieldValue(Integer.class, layout.fieldNbNewEvents());

        Integer portInflightCredit = content.getFieldValue(Integer.class, layout.fieldPortInflightCredits());
        Integer swInflightCredit = content.getFieldValue(Integer.class, layout.fieldSwInflights());

        if (backendId == null || portId == null || nbEnqEvents == null || nbNewEnqEvents == null
                || portInflightCredit == null || swInflightCredit == null) {
            throw new IllegalArgumentException(layout.eventSwEventEnqueueBurst() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(backendId);
        if(device != null) {
            PortModel port = device.getPort(portId);
            if(port != null) {
                device.updateInflight(swInflightCredit, portId, portInflightCredit, ts);
                port.enqueueEvents(nbEnqEvents, ts);
            }
        }
    }

}
