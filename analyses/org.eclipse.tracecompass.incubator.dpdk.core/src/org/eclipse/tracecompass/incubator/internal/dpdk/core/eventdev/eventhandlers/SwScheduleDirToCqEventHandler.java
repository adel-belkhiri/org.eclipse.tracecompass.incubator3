package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.EventDevModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.QueueModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class SwScheduleDirToCqEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public SwScheduleDirToCqEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer backendId = content.getFieldValue(Integer.class, layout.fieldSw());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortId());
        Integer queueId = content.getFieldValue(Integer.class, layout.fieldQidID());

        Integer count = content.getFieldValue(Integer.class, layout.fieldCount());


        if (backendId == null || portId == null || queueId == null || count == null) {
            throw new IllegalArgumentException(layout.eventSwScheduleDirToCq() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(backendId);
        if(device != null) {
            //PortModel port = device.getPort(portId);
            QueueModel queue = device.getQueue(queueId);

            if(queue != null) {
                queue.transferToPort(portId, null, ts);
            }

            //port.pushToRingBufferCq(count, ts);
        }
    }

}
