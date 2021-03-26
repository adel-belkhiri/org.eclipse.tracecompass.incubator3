package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.PipelineModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.SoftwareQueueModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RtePortRingWriterSendBurstEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePortRingWriterSendBurstEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer portId = content.getFieldValue(Integer.class, layout.fieldPort());
        Integer txPktCount = content.getFieldValue(Integer.class, layout.fieldTxPktCount());

        if (portId == null || txPktCount == null) {
            throw new IllegalArgumentException(layout.eventRtePortRingSendBurst()+ " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        PipelineModel pipeline = fPipelineStateProvier.searchPipelineByPortID(portId);
        if(pipeline != null) {
            pipeline.sendPackets(portId, txPktCount, ts);

            SoftwareQueueModel queue = fPipelineStateProvier.getSoftwareQueue(portId);
            if(queue != null) {
                queue.enqueuePackets(txPktCount, ts);
            }
        }
    }

}
