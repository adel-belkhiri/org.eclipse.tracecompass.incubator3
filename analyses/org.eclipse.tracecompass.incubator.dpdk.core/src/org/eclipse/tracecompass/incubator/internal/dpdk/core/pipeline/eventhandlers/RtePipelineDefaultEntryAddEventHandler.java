package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.PipelineAction;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.PipelineModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.PipelineTableModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RtePipelineDefaultEntryAddEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePipelineDefaultEntryAddEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
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
        Integer tableIdx = content.getFieldValue(Integer.class, layout.fieldTableIdx());
        Integer action = content.getFieldValue(Integer.class, layout.fieldDefaultAction());
        Integer nextPortOrTableId = content.getFieldValue(Integer.class, layout.fieldNextPortOrTableId());

        if (pipeId == null || tableIdx == null || action == null || nextPortOrTableId == null) {
            throw new IllegalArgumentException(layout.eventRtePipelineTableDefaultEntryAdd() + " event does not have expected fields");
        }

        PipelineModel pipeline = fPipelineStateProvier.getPipeline(pipeId);
        if(pipeline != null){
            PipelineTableModel table = pipeline.searchTableByIndex(tableIdx);
            table.setDefaultAction(PipelineAction.values()[action], nextPortOrTableId, ts);
        }
    }

}
