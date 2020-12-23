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
public class RtePipelineFreeEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipeline state provider
     */
    public RtePipelineFreeEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer pipeId = content.getFieldValue(Integer.class, layout.fieldPipeline());

        if (pipeId == null) {
            throw new IllegalArgumentException(layout.eventRtePipelineFree() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fPipelineStateProvier.deletePipeline(pipeId, ts);
    }

}
