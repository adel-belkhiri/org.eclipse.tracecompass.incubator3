package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.EventDevModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.PortModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.PortStatus;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class DswCollectConfirmationMessagesEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public DswCollectConfirmationMessagesEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer dsw = content.getFieldValue(Integer.class, layout.fieldDsw());
        Integer srcPortId = content.getFieldValue(Integer.class, layout.fieldSrcPortId());

        if (dsw == null || srcPortId == null) {
            throw new IllegalArgumentException(layout.eventCollectAllConfirmationMessages()+ " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(dsw);
        if(device != null) {
            PortModel port = device.getPort(srcPortId);
            if(port != null) {
                if(port.getStatus() == PortStatus.PAUSING) {
                    port.updateStatus(PortStatus.FORWARDING, ts);
                }
            }
        }
    }

}