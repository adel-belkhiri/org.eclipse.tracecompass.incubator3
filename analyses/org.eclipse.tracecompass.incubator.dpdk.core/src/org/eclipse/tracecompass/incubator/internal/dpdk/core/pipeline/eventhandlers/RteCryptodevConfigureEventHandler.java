package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteCryptodevConfigureEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RteCryptodevConfigureEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer devIdx = content.getFieldValue(Integer.class, layout.fieldDevIdx());
        String devName = content.getFieldValue(String.class, layout.fieldName());
        Integer nbQueuePairs = content.getFieldValue(Integer.class, layout.fieldNbQueuePairs());

        if (devIdx == null || devName == null || nbQueuePairs == null) {
            throw new IllegalArgumentException(layout.eventRteCryptoDevConfigure()+ " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fPipelineStateProvier.addCryptoDevice(devName, devIdx, nbQueuePairs);
    }

}
