package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmObjectsStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.LpmLookupObjectModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

import com.google.common.net.InetAddresses;

/**
 * @author Adel Belkhiri
 *
 */
public class RteLpmDeleteEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteLpmDeleteEventHandler(@NonNull DpdkLookupObjectsAnalysisEventLayout layout, DpdkLpmObjectsStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLookupObjectsAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer lpm = content.getFieldValue(Integer.class, layout.fieldLpm());
        Long ipv4 = content.getFieldValue(Long.class, layout.fieldIPv4Addr());
        Integer depth = content.getFieldValue(Integer.class, layout.fieldDepth());
        Integer ret = content.getFieldValue(Integer.class, layout.fieldOpResult());
        if (lpm == null || ipv4 == null || depth == null || ret == null) {
            throw new IllegalArgumentException(layout.eventRteLpmDelete() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        if(ret ==0) {
            LpmLookupObjectModel table = fLpmStateProvier.getLpmTable(lpm);
            if(table != null) {
                table.deleteRule(InetAddresses.fromInteger(ipv4.intValue()), depth, ts);
            }
        }
    }

}
