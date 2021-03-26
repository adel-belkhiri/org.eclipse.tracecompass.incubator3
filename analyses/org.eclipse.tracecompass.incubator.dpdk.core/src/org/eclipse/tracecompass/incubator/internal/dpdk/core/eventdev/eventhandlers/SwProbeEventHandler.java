package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.EventDevBackendType;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class SwProbeEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkPipelineAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public SwProbeEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        String devName = content.getFieldValue(String.class, layout.fieldName());
        Integer devId = content.getFieldValue(Integer.class, layout.fieldDevID());
        Integer sw = content.getFieldValue(Integer.class, layout.fieldSw());

        Integer serviceId = content.getFieldValue(Integer.class, layout.fieldServiceId());
        Integer creditQ = content.getFieldValue(Integer.class, layout.fieldCreditQuanta());
        Integer schedQ = content.getFieldValue(Integer.class, layout.fieldSchedQuanta());

        if (devName == null || devId == null || sw == null || creditQ == null
                || schedQ == null || serviceId == null) {
            throw new IllegalArgumentException(layout.eventSwProbe() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fEventdevStateProvier
            .addEventDevice(devName, devId, sw, serviceId, creditQ, schedQ, EventDevBackendType.SW);
    }

}
