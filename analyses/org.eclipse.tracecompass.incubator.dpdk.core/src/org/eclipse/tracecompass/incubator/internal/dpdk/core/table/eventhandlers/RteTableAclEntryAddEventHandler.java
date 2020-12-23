package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.AclFlowRuleModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis.DpdkTableStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteTableAclEntryAddEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteTableAclEntryAddEventHandler(@NonNull DpdkTableAnalysisEventLayout layout, DpdkTableStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkTableAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        //tbl=0x10553fe80, ip=2913840557, depth=16, key_found=0, nht_pos=0, entry_ptr=4384620224
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer tbl = content.getFieldValue(Integer.class, layout.fieldTblPointer());
        Integer priority = content.getFieldValue(Integer.class, layout.fieldPriority());
        long[] fields = content.getFieldValue(long[].class, layout.fieldRuleFields());
        long[] masks = content.getFieldValue(long[].class, layout.fieldRuleFieldMasks());
        Integer keyFound = content.getFieldValue(Integer.class, layout.fieldKeyFound());
        Integer rulePos = content.getFieldValue(Integer.class, layout.fieldRulePosition());

        if (tbl == null || priority == null || fields == null || masks == null || rulePos == null || keyFound == null) {
            throw new IllegalArgumentException(layout.eventRteTableAclEntryAdd() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* a new acl rule */
        if(keyFound == 0) {
            AclFlowTableModel table = (AclFlowTableModel) fTableStateProvier.getTable(tbl);
            if(table != null) {
                AclFlowRuleModel rule = new AclFlowRuleModel(rulePos, priority, fields, masks,
                        table.getFieldType(), table.getFieldSize());
                table.addRule(rulePos, rule, ts);
            }
        }
    }

}
