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
public class RteFlowClassifierLookupEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteFlowClassifierLookupEventHandler(@NonNull DpdkClassifierAnalysisEventLayout layout, DpdkClassifierStateProvider stateProvider) {
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

        Integer nbPktIn = content.getFieldValue(Integer.class, layout.fieldNbPacketsIn());
        Integer nbPktOut = content.getFieldValue(Integer.class, layout.fieldNbPacketsOut());
        long[] entries = content.getFieldValue(long[].class, layout.fieldEntriesPtr());

        if (clsName == null || tblId == null || nbPktIn == null || nbPktOut == null
                || entries == null) {
            throw new IllegalArgumentException(layout.eventRteFlowClassifyLookup() +
                    " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        FlowTableModel table = fClassifierStateProvier.getClassifier(clsName).getTable(tblId);
        if(table != null) {
            table.addLookupStats(entries, nbPktIn, nbPktOut, ts);
        }
    }

}
