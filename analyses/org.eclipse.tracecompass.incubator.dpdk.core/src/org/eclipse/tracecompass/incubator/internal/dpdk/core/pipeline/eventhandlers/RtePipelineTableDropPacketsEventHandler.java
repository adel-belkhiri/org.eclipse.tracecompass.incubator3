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
public class RtePipelineTableDropPacketsEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePipelineTableDropPacketsEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
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
        Integer nbPktsDrop = content.getFieldValue(Integer.class, layout.fieldNbPktsDrop());

        if (tableIdx == null || pipeId == null || nbPktsDrop == null) {
            throw new IllegalArgumentException(" One of table dropping events does not have expected fields");
        }

        PipelineModel pipeline = fPipelineStateProvier.getPipeline(pipeId);
        if(pipeline != null){
            PipelineTableModel table = pipeline.searchTableByIndex(tableIdx);
            if(table != null) {
                table.dropPackets(nbPktsDrop, ts);
            }
        }
    }

}
