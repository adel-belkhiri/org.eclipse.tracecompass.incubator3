package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.Activator;
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
public class RtePipelineInputPortCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePipelineInputPortCreateEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @SuppressWarnings("nls")
    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer pipeId = content.getFieldValue(Integer.class, layout.fieldPipeline());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortPtr());
        Integer portIdx = content.getFieldValue(Integer.class, layout.fieldPortIdx());
        Integer burstSize = content.getFieldValue(Integer.class, layout.fieldBurstSize());

        if (portId == null || portIdx == null || pipeId == null || burstSize == null) {
            throw new IllegalArgumentException(layout.eventRtePipelineInputPortCreate() + " event does not have expected fields");
        }

        boolean success = fPipelineStateProvier.addInputPortToPipeline(pipeId, portIdx, portId, burstSize, ts);
        if(!success){
            Activator.getInstance().logError("Pipeline (id = " + String.valueOf(pipeId)+ ") not found !!");
        }
    }

}
