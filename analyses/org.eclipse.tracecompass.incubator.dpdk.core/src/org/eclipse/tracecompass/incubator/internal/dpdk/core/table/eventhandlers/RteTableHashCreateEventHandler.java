package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.DpdkTableStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.FlowRuleKeyType;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteTableHashCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteTableHashCreateEventHandler(@NonNull DpdkTableAnalysisEventLayout layout, DpdkTableStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkTableAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        String tblName = content.getFieldValue(String.class, layout.fieldName());
        Integer tbl = content.getFieldValue(Integer.class, layout.fieldTblPointer());

        if (tblName == null || tbl == null) {
            throw new IllegalArgumentException(layout.eventRteTableHashCuckooCreate() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fTableStateProvier.addTable(tblName, tbl, FlowRuleKeyType.Hash_KEY, new long[0], new long[0]);
    }

}
