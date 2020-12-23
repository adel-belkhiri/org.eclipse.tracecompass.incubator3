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
public class RteEthdevConfigureEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RteEthdevConfigureEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer portIdx = content.getFieldValue(Integer.class, layout.fieldPortIdx());
        String devName = content.getFieldValue(String.class, layout.fieldName());
        Integer nbRxq = content.getFieldValue(Integer.class, layout.fieldNbRxQueues());
        Integer nbTxq = content.getFieldValue(Integer.class, layout.fieldNbTxQueues());

        if (portIdx == null || devName == null || nbRxq == null || nbTxq == null) {
            throw new IllegalArgumentException(layout.eventRteEthDevConfigure()+ " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fPipelineStateProvier.addEthernetDevice(devName, portIdx, nbRxq, nbTxq);
    }

}
