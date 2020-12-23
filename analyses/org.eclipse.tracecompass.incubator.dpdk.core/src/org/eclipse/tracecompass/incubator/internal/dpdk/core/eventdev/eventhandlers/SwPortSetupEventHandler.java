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
public class SwPortSetupEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public SwPortSetupEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer backendId = content.getFieldValue(Integer.class, layout.fieldSw());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortIdx());

        Integer newEvtThreshold = content.getFieldValue(Integer.class, layout.fieldPortNewEventsThreshold());
        Integer enqueueDepth = content.getFieldValue(Integer.class, layout.fieldPortEnqueueDepth());
        Integer dequeueDepth = content.getFieldValue(Integer.class, layout.fieldPortDequeueDepth());

        Integer ringRxId = content.getFieldValue(Integer.class, layout.fieldRxWorkerRing());
        Integer ringCqId = content.getFieldValue(Integer.class, layout.fieldCqWorkerRing());

        if (backendId == null || portId == null || enqueueDepth == null || dequeueDepth == null
                || newEvtThreshold == null || ringRxId == null || ringCqId == null) {
            throw new IllegalArgumentException(layout.eventRtePortSetup() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(backendId);
        RingModel ringRx = fEventdevStateProvier.getRingBuffer(ringRxId);
        RingModel ringCq = fEventdevStateProvier.getRingBuffer(ringCqId);

        if(device != null && ringRx != null && ringCq != null) {
            device.addPort(portId, newEvtThreshold, enqueueDepth, dequeueDepth, ringRx, ringCq);
        }
    }

}
