package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.DpdkClassifierStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowTableModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteFlowClassifyTableEntryDeleteEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteFlowClassifyTableEntryDeleteEventHandler(@NonNull DpdkClassifierAnalysisEventLayout layout, DpdkClassifierStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkClassifierAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        String clsName = content.getFieldValue(String.class, layout.fieldClsName());
        Integer tblId = content.getFieldValue(Integer.class, layout.fieldTblPointer());

        Integer ruleId = content.getFieldValue(Integer.class, layout.fieldRuleId());

        if (clsName == null || tblId == null || ruleId == null) {
            throw new IllegalArgumentException(layout.eventRteFlowClassifyTableEntryDelete() +
                    " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        FlowTableModel table = fClassifierStateProvier.getTable(clsName, tblId);
        if(table != null) {
            table.removeRule(ruleId, ts);
        }

    }

}
