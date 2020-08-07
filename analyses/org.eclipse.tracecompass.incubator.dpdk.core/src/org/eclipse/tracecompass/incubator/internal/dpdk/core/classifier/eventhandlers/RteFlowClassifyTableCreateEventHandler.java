package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.DpdkClassifierStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteFlowClassifyTableCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteFlowClassifyTableCreateEventHandler(@NonNull DpdkClassifierAnalysisEventLayout layout, DpdkClassifierStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkClassifierAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();

        String clsName = content.getFieldValue(String.class, layout.fieldClsName());

        String tblName = content.getFieldValue(String.class, layout.fieldTblName());
        Integer tblId = content.getFieldValue(Integer.class, layout.fieldTblPointer());
        Integer tblType = content.getFieldValue(Integer.class, layout.fieldTableType());

        if (clsName == null || tblName == null || tblId == null || tblType == null) {
            throw new IllegalArgumentException(layout.eventRteFlowClassifyTableCreate() +
                    " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fClassifierStateProvier.addTableToClassifier(clsName, tblName, tblId);
    }

}
