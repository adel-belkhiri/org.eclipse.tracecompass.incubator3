package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis.CryptoDeviceModel;
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
public class RtePortCryptodevWriterCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePortCryptodevWriterCreateEventHandler(@NonNull DpdkPipelineAnalysisEventLayout layout, DpdkPipelineStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    @SuppressWarnings("nls")
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkPipelineAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        // librte_port_sym_crypto:rte_port_crypto_writer_create    port=0x10520cb40, dev_id=0, queue_id=0, tx_burst_sz=32

        //
        ITmfEventField content = event.getContent();

        Integer portId = content.getFieldValue(Integer.class, layout.fieldPort());
        Integer devIdx = content.getFieldValue(Integer.class, layout.fieldDevIdx());
        Integer queueId = content.getFieldValue(Integer.class, layout.fieldQueueId());
        Integer txBurstSize = content.getFieldValue(Integer.class, layout.fieldTxBurstSize());

        if (portId == null || devIdx == null || queueId == null || txBurstSize == null) {
            throw new IllegalArgumentException(layout.eventRtePortCryptoWriterCreate() +
                    " event does not have expected fields");
        }

        String name = "no_name";
        CryptoDeviceModel dev = fPipelineStateProvier.getCryptoDevice(devIdx);
        if(dev != null) {
            name = dev.getName() + "/TX" + queueId.toString();
        }
        fPipelineStateProvier.addPort(name, portId, PortTypeEnum.CRYPTO, txBurstSize);
    }

}
