package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowTableModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.DpdkTableStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.HashFlowRuleModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteTableHashCuckooEntryAddEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteTableHashCuckooEntryAddEventHandler(@NonNull DpdkTableAnalysisEventLayout layout, DpdkTableStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkTableAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer tbl = content.getFieldValue(Integer.class, layout.fieldTblPointer());
        Long key = content.getFieldValue(Long.class, layout.fieldKey());
        Integer entryPtr = content.getFieldValue(Integer.class, layout.fieldEntryPtr());
        Integer keyFound = content.getFieldValue(Integer.class, layout.fieldKeyFound());

        if (tbl == null || key == null || entryPtr == null || keyFound == null) {
            throw new IllegalArgumentException(layout.eventRteTableHashCuckooEntryAdd() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* a new hash rule */
        if(keyFound == 0) {
            FlowTableModel table = fTableStateProvier.getTable(tbl);
            if(table != null) {
                HashFlowRuleModel rule = new HashFlowRuleModel(entryPtr, key);
                table.addRule(entryPtr, rule, ts);
            }
        }
    }

}
