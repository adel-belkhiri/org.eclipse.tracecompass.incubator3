package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.PipelineModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RtePortCryptodevReaderRxEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePortCryptodevReaderRxEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer portId = content.getFieldValue(Integer.class, layout.fieldPort());
        Integer nbRxPkts = content.getFieldValue(Integer.class, layout.fieldNbRx());
        Long nbZeroPolls = content.getFieldValue(Long.class, layout.fieldzeroPolls());

        if (portId == null || nbRxPkts == null || nbZeroPolls == null) {
            throw new IllegalArgumentException(layout.eventRtePortCryptoReaderRx()+ " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        PipelineModel pipeline = fPipelineStateProvier.searchPipelineByPortID(portId);
        if(pipeline != null) {
            pipeline.receivePackets(portId, nbRxPkts, nbZeroPolls, ts);
        }
    }

}
