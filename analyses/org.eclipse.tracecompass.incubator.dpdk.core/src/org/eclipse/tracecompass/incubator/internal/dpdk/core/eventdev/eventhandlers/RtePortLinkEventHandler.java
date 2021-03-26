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
public class RtePortLinkEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RtePortLinkEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer devId = content.getFieldValue(Integer.class, layout.fieldDevID());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortId());

        Integer nbLinks = content.getFieldValue(Integer.class, layout.fieldNbLinks());
        long[] queues = content.getFieldValue(long[].class, layout.fieldQueues());
        long[] priorities = content.getFieldValue(long[].class, layout.fieldPriorities());


        if (devId == null || portId == null || nbLinks == null || queues == null
                || priorities == null) {
            throw new IllegalArgumentException(layout.eventRtePortLink() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.getEventDevice(devId);
        if(device != null) {
            for(int i = 0; i < nbLinks; i++ ) {
                int queueId = (int) queues[i];
                int priority = (int) priorities[i];

                QueueModel queue = device.getQueue(queueId);
                queue.attachPort(portId, priority);
            }
        }
    }

}
