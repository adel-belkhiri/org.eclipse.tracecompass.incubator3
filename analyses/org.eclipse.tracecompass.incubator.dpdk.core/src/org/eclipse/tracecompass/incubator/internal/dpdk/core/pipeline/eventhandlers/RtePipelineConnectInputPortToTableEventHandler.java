package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.InputPortModel;
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
public class RtePipelineConnectInputPortToTableEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePipelineConnectInputPortToTableEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
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
        Integer portIdx = content.getFieldValue(Integer.class, layout.fieldPortIdx());

        if (pipeId == null || tableIdx == null || portIdx == null) {
            throw new IllegalArgumentException(layout.eventRtePipelinePortInConnectToTable() +
                    " event does not have expected fields");
        }

        PipelineModel pipe = fPipelineStateProvier.getPipeline(pipeId);
        if(pipe != null) {
            PipelineTableModel table = pipe.searchTableByIndex(tableIdx);
            if(table != null) {
                InputPortModel port = pipe.searchInputPortByIndex(portIdx);
                if(port != null) {
                    table.attachInputPort(port, ts);
                }
            }
        }
    }

}
