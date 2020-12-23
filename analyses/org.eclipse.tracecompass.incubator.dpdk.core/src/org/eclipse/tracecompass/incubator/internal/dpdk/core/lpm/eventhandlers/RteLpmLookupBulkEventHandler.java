package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmObjectsStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.LpmLookupObjectModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteLpmLookupBulkEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteLpmLookupBulkEventHandler(@NonNull DpdkLookupObjectsAnalysisEventLayout layout, DpdkLpmObjectsStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLookupObjectsAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer id = content.getFieldValue(Integer.class, layout.fieldLpm());
        long[] ips = content.getFieldValue(long[].class, layout.fieldIps());
        long[] tblEntries = content.getFieldValue(long[].class, layout.fieldTblEntries());
        Integer n = content.getFieldValue(Integer.class, layout.fieldNumberOfIpAddresses());

        if (id == null || ips == null || tblEntries == null || n == null) {
            throw new IllegalArgumentException(layout.eventRteLpmLookupBulk() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LpmLookupObjectModel table = fLpmStateProvier.getLpmTable(id);
        if(table != null) {
            table.ruleLookupBulk(ips, tblEntries, n, ts);
        }
    }

}
