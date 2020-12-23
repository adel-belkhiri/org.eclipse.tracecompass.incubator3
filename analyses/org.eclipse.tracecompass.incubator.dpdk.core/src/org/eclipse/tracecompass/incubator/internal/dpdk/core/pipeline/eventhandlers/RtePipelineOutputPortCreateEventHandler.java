package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.Activator;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RtePipelineOutputPortCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePipelineOutputPortCreateEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    @SuppressWarnings("nls")
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();
        long ts = event.getTimestamp().getValue();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer pipeId = content.getFieldValue(Integer.class, layout.fieldPipeline());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortPtr());
        Integer portIdx = content.getFieldValue(Integer.class, layout.fieldPortIdx());

        if (portId == null || portIdx == null || pipeId == null) {
            throw new IllegalArgumentException(layout.eventRtePipelinePortOutCreate() + " event does not have expected fields");
        }

        boolean success = fPipelineStateProvier.addOutputPortToPipeline(pipeId, portIdx, portId, ts);
        if(!success){
            Activator.getInstance().logError("Pipeline (id = " + String.valueOf(pipeId)+ ") not found !!");
        }
    }

}
