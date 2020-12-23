package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
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
public class RtePipelineForwardToNextTableEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePipelineForwardToNextTableEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
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
        Integer srcTableIdx = content.getFieldValue(Integer.class, layout.fieldFromTableIdx());
        Integer dstTableIdx = content.getFieldValue(Integer.class, layout.fieldToTableIdx());
        Integer nbPkts = content.getFieldValue(Integer.class, layout.fieldNbPkts());

        if (pipeId == null || srcTableIdx == null || dstTableIdx == null || nbPkts == null) {
            throw new IllegalArgumentException(layout.eventRtePipelineTableCreate() +
                    " event does not have expected fields");
        }

        if(nbPkts <= 0) {
            return;
        }

        PipelineModel pipeline = fPipelineStateProvier.getPipeline(pipeId);
        if(pipeline != null) {
            PipelineTableModel table = pipeline.searchTableByIndex(srcTableIdx);
            if(table != null) {
                if(table.getChildTableIndex() == dstTableIdx) {
                    table.forwardToChildTable(nbPkts, ts);
                }
            }
        }
    }

}
