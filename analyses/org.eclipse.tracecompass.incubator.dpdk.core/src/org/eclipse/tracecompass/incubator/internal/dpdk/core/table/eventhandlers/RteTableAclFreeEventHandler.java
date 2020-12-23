package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.DpdkTableStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteTableAclFreeEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteTableAclFreeEventHandler(@NonNull DpdkTableAnalysisEventLayout layout, DpdkTableStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkTableAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer tbl = content.getFieldValue(Integer.class, layout.fieldTblPointer());

        if (tbl == null) {
            throw new IllegalArgumentException(layout.eventRteTableAclFree() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fTableStateProvier.deleteTable(tbl, ts);
    }

}
