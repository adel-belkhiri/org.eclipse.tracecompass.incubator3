package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.RingModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteEventRingDequeueBurstEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RteEventRingDequeueBurstEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer ringId = content.getFieldValue(Integer.class, layout.fieldRing());
        Integer nbDequeue = content.getFieldValue(Integer.class, layout.fieldNbDeqEvents());
        Integer nbRemainingEvents = content.getFieldValue(Integer.class, layout.fieldNbRemainingEvents());

        if (ringId == null || nbDequeue == null || nbRemainingEvents == null) {
            throw new IllegalArgumentException(layout.eventRteEventRingDequeueBurst() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        RingModel ring = fEventdevStateProvier.getRingBuffer(ringId);
        if(ring != null) {
            ring.dequeue(nbDequeue, ts);
        }
    }

}
