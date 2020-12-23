package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.DpdkPipelineStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.PortTypeEnum;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RtePortSinkCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePortSinkCreateEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    @SuppressWarnings("nls")
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();
        StringBuilder sinkPortName =  new StringBuilder("sink:");
        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer portId = content.getFieldValue(Integer.class, layout.fieldPort());
        String pcapFileNamefieldPcapFileName = content.getFieldValue(String.class, layout.fieldPcapFileName());

        // maximum number of packets to write to the pcap file
        Integer maxNbPackets = content.getFieldValue(Integer.class, layout.fieldMaxNbPkts());

        if (portId == null || maxNbPackets == null || pcapFileNamefieldPcapFileName == null) {
            throw new IllegalArgumentException(layout.eventRtePortSinkCreate() +
                    " event does not have expected fields");
        }

        if(pcapFileNamefieldPcapFileName.length() == 0) {
            sinkPortName.append(portId.toString());
        } else {
            sinkPortName.append(pcapFileNamefieldPcapFileName);
        }

        fPipelineStateProvier.addPort(sinkPortName.toString(), portId, PortTypeEnum.SINK, 0 /*burst size*/);
    }

}
