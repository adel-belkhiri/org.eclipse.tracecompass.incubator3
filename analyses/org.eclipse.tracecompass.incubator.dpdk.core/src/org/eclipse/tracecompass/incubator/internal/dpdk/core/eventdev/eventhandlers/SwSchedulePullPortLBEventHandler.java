package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class SwSchedulePullPortLBEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public SwSchedulePullPortLBEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        //long ts = event.getTimestamp().getValue();

        Integer backendId = content.getFieldValue(Integer.class, layout.fieldSw());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortId());
        Integer queueId = content.getFieldValue(Integer.class, layout.fieldQidID());

        //Integer iqNum = content.getFieldValue(Integer.class, layout.fieldQ);
        //Integer allowOrder = content.getFieldValue(Integer.class, layout.fieldQ);

        if (backendId == null || portId == null || queueId == null) {
            throw new IllegalArgumentException(layout.eventSwPullPortLB() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        //EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(backendId);
        //if(device != null) {
        //    PortModel port = device.getPort(portId);
        //    port.pullFromRingBufferRx(1, ts);
        //}
    }

}
