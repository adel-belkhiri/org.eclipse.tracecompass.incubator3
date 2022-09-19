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
public class SwEventDequeueBurstEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public SwEventDequeueBurstEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
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

        Integer nbDeqEvents = content.getFieldValue(Integer.class, layout.fieldNbDeqEvents());

        Integer portInflightCredit = content.getFieldValue(Integer.class, layout.fieldPortInflightCredits());
        Integer swInflightCredit = content.getFieldValue(Integer.class, layout.fieldSwInflights());

        Long nbZeroPolls = content.getFieldValue(Long.class, layout.fieldZeroPolls());
        Long nbTotPolls = content.getFieldValue(Long.class, layout.fieldTotPolls());

        if (backendId == null || portId == null || nbDeqEvents == null
                || portInflightCredit == null || swInflightCredit == null || nbZeroPolls == null || nbTotPolls == null) {
            throw new IllegalArgumentException(layout.eventSwEventDequeueBurst() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(backendId);
        if(device != null) {
            PortModel port = device.getPort(portId);
            if(port != null) {
                device.updateInflight(swInflightCredit, portId, portInflightCredit, ts);
                port.dequeueEvents(nbDeqEvents, nbZeroPolls, nbTotPolls, ts);
            }
        }
    }

}
