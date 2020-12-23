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
public class RteTableAclCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteTableAclCreateEventHandler(@NonNull DpdkTableAnalysisEventLayout layout, DpdkTableStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkTableAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        String tblName = content.getFieldValue(String.class, layout.fieldName());
        Integer tbl = content.getFieldValue(Integer.class, layout.fieldTblPointer());
        long[] fieldDefType = content.getFieldValue(long[].class, layout.fieldDefType());
        long[] fieldDefSize = content.getFieldValue(long[].class, layout.fieldDefSize());

        if (tblName == null || tbl == null || fieldDefType == null || fieldDefSize == null) {
            throw new IllegalArgumentException(layout.eventRteTableAclCreate() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fTableStateProvier.addTable(tblName, tbl, FlowRuleKeyType.ACL_KEY, fieldDefType, fieldDefSize);
    }

}
