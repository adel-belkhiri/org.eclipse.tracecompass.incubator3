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
public class RteEventRingEnqueueBurstEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RteEventRingEnqueueBurstEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer ringId = content.getFieldValue(Integer.class, layout.fieldRing());
        Integer nbEnqueue = content.getFieldValue(Integer.class, layout.fieldNbEnqEvents());
        Integer nbFreeEntries = content.getFieldValue(Integer.class, layout.fieldNbFreeEntries());

        if (ringId == null || nbEnqueue == null || nbFreeEntries == null) {
            throw new IllegalArgumentException(layout.eventRteEventRingEnqueueBurst() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        RingModel ring = fEventdevStateProvier.getRingBuffer(ringId);
        if(ring != null) {
            ring.enqueue(nbEnqueue, ts);
        }
    }

}
