package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.NetworkDeviceModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.PortTypeEnum;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RtePortEthdevReaderCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePortEthdevReaderCreateEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer portId = content.getFieldValue(Integer.class, layout.fieldPort());
        Integer portIdx = content.getFieldValue(Integer.class, layout.fieldPortIdx());
        Integer queueId = content.getFieldValue(Integer.class, layout.fieldQueueId());

        if (portId == null || portIdx == null || queueId == null ) {
            throw new IllegalArgumentException(layout.eventRtePortEthdevReaderCreate() +
                    " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        String name = "no_name";
        NetworkDeviceModel dev = fPipelineStateProvier.getEthernetDevice(portIdx);
        if(dev != null) {
            name = dev.getName() + "/RX" + queueId.toString(); //$NON-NLS-1$
        }
        fPipelineStateProvier.addPort(name, portId, PortTypeEnum.ETHER, 0);
    }

}
