package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowTableModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.DpdkTableStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteTableAclLookupKeyEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteTableAclLookupKeyEventHandler(@NonNull DpdkTableAnalysisEventLayout layout, DpdkTableStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkTableAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer tbl = content.getFieldValue(Integer.class, layout.fieldTblPointer());
        long[] rule_pos = content.getFieldValue(long[].class, layout.fieldRulePosition());

        Integer nbPktIn = content.getFieldValue(Integer.class, layout.fieldNbPacketsIn());
        Integer nbPktOut = content.getFieldValue(Integer.class, layout.fieldNbPacketsOut());

        if (tbl == null || rule_pos == null || nbPktIn == null || nbPktOut == null) {
            throw new IllegalArgumentException(layout.eventRteTableAclLookup() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }


        FlowTableModel table = fTableStateProvier.getTable(tbl);
        if(table != null) {
            table.addLookupStats(rule_pos, nbPktIn, nbPktOut, ts);
        }
    }

}
