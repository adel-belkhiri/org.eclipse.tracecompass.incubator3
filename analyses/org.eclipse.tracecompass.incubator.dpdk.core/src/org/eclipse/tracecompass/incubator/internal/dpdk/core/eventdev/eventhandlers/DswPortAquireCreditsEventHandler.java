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
public class DswPortAquireCreditsEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public DswPortAquireCreditsEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer dsw = content.getFieldValue(Integer.class, layout.fieldDsw());
        Integer portId = content.getFieldValue(Integer.class, layout.fieldPortId());

        Integer requiredCredits = content.getFieldValue(Integer.class, layout.fieldRequiredCredits());
        Integer portInflightCredits = content.getFieldValue(Integer.class, layout.fieldPortInflightCredits());
        Integer dswCreditsOnLoan = content.getFieldValue(Integer.class, layout.fieldDswCreditsOnLoan());

        if (dsw == null || portId == null || requiredCredits == null || portInflightCredits == null
                || dswCreditsOnLoan == null) {
            throw new IllegalArgumentException(layout.eventDswPortAcquireCredits() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel device = fEventdevStateProvier.searchEventDevByBackendId(dsw);
        if(device != null) {
           device.updateInflight(dswCreditsOnLoan, portId, portInflightCredits, ts);
        }
    }

}
