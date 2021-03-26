package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.EventDevModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.RingModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class DswPortSetupEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public DswPortSetupEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer backendId = content.getFieldValue(Integer.class, layout.fieldDsw());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortId());

        Integer newEvtThreshold = content.getFieldValue(Integer.class, layout.fieldPortNewEventsThreshold());
        Integer enqueueDepth = content.getFieldValue(Integer.class, layout.fieldPortEnqueueDepth());
        Integer dequeueDepth = content.getFieldValue(Integer.class, layout.fieldPortDequeueDepth());

        Integer inRingId = content.getFieldValue(Integer.class, layout.fieldInRing());
        Integer ctlInringId = content.getFieldValue(Integer.class, layout.fieldCtlInRing());

        if (backendId == null || portId == null || enqueueDepth == null || dequeueDepth == null
                || newEvtThreshold == null || inRingId == null || ctlInringId == null) {
            throw new IllegalArgumentException(layout.eventDswPortSetup() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(backendId);
        RingModel inRing = fEventdevStateProvier.getRingBuffer(inRingId);
        //RingModel ctlRing = fEventdevStateProvier.getRingBuffer(ringCqId);

        if(device != null && inRing != null) {
            device.addPort(portId, newEvtThreshold, enqueueDepth, dequeueDepth, inRing, inRing);
        }
    }

}
