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
public class RteEventDevConfigureEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      @DpdkEventDevAnalysisEventLayout instance
     * @param stateProvider
     *      Pipelinesstate provider
     */
    public RteEventDevConfigureEventHandler(@NonNull DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkEventDevAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer devId = content.getFieldValue(Integer.class, layout.fieldDevID());
        Integer nbEvLimit = content.getFieldValue(Integer.class, layout.fieldNbEventsLimit());

        if (devId == null || nbEvLimit == null) {
            throw new IllegalArgumentException(layout.eventRteEventDevConfigure() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        EventDevModel evDev = fEventdevStateProvier.getEventDevice(devId);
        if(evDev != null) {
            evDev.setNbEventsLimit(nbEvLimit);
        }

    }

}
