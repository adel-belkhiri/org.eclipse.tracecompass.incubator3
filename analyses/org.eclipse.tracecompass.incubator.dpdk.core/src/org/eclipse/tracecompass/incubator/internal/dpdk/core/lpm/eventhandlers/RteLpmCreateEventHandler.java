package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmObjectsStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteLpmCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteLpmCreateEventHandler(@NonNull DpdkLookupObjectsAnalysisEventLayout layout, DpdkLpmObjectsStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLookupObjectsAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        Integer lpm = content.getFieldValue(Integer.class, layout.fieldLpm());
        String name = content.getFieldValue(String.class, layout.fieldName());
        Integer maxRules = content.getFieldValue(Integer.class, layout.fieldMaxRules());

        if (lpm == null || name == null || maxRules == null) {
            throw new IllegalArgumentException(layout.eventRteLpmCreate() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fLpmStateProvier.addLpmTable(lpm, name);
    }

}
