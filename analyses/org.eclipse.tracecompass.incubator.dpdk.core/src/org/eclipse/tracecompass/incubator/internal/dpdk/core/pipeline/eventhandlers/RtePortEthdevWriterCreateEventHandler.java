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
public class RtePortEthdevWriterCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePortEthdevWriterCreateEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    @SuppressWarnings("nls")
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer portId = content.getFieldValue(Integer.class, layout.fieldPort());
        Integer portIdx = content.getFieldValue(Integer.class, layout.fieldPortIdx());
        Integer queueId = content.getFieldValue(Integer.class, layout.fieldQueueId());

        Integer txBurstSize = content.getFieldValue(Integer.class, layout.fieldTxBurstSize());
        if (portId == null || portIdx == null || queueId == null || txBurstSize == null) {
            throw new IllegalArgumentException(layout.eventRtePortEthdevWriterCreate() +
                    " event does not have expected fields");
        }

        String name = "no_name";
        NetworkDeviceModel dev = fPipelineStateProvier.getEthernetDevice(portIdx);
        if(dev != null) {
            name = dev.getName() + "/TX" + queueId.toString();
        }
        fPipelineStateProvier.addPort(name, portId, PortTypeEnum.ETHER, txBurstSize);
    }

}
