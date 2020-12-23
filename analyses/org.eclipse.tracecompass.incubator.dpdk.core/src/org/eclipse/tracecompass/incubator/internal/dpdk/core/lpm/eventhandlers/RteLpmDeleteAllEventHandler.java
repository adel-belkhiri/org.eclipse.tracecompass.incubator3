package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmObjectsStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.LpmLookupObjectModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteLpmDeleteAllEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteLpmDeleteAllEventHandler(@NonNull DpdkLookupObjectsAnalysisEventLayout layout, DpdkLpmObjectsStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLookupObjectsAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();
        Integer id = content.getFieldValue(Integer.class, layout.fieldLpm());

        if (id == null) {
            throw new IllegalArgumentException(layout.eventRteLpmDeleteAll() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LpmLookupObjectModel table = fLpmStateProvier.getLpmTable(id);
        if(table != null) {
            table.deleteAllRules(ts);
        }
    }

}
