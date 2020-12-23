package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.EventDevModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteQueueSetupEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RteQueueSetupEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer devId = content.getFieldValue(Integer.class, layout.fieldDevID());
        Integer queueId = content.getFieldValue(Integer.class, layout.fieldQueueId());
        Integer schedType = content.getFieldValue(Integer.class, layout.fieldScheduleType());
        Integer priority = content.getFieldValue(Integer.class, layout.fieldPriority());


        if (devId == null || queueId == null || priority == null || schedType == null) {
            throw new IllegalArgumentException(layout.eventRteQueueSetup() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.getEventDevice(devId);
        if(device != null) {
            device.addQueue(queueId, schedType, priority);
        }
    }

}
