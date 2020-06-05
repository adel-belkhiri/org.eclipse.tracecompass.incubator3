package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.LpmTableModel;
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
     * @param stateProvider
     */
    public RteLpmDeleteAllEventHandler(@NonNull DpdkLpmAnalysisEventLayout layout, DpdkLpmStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLpmAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();
        String name = content.getFieldValue(String.class, layout.fieldName());

        if (name == null) {
            throw new IllegalArgumentException(layout.eventRteLpmDeleteAll() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LpmTableModel table = fLpmStateProvier.getLpmTable(name);
        if(table != null) {
            table.deleteAllRules(ts);
        }
    }

}
