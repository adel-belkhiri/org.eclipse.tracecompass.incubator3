package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import com.google.common.net.InetAddresses;
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
public class RteLpmLookupEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteLpmLookupEventHandler(@NonNull DpdkLookupObjectsAnalysisEventLayout layout, DpdkLpmObjectsStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLookupObjectsAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer id = content.getFieldValue(Integer.class, layout.fieldLpm());
        Long ipv4 = content.getFieldValue(Long.class, layout.fieldIPv4Addr());
        Integer depth = content.getFieldValue(Integer.class, layout.fieldRuleDepth());
        Integer ret = content.getFieldValue(Integer.class, layout.fieldOpResult());

        if (id == null || ipv4 == null || depth == null || ret == null) {
            throw new IllegalArgumentException(layout.eventRteLpmLookup() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LpmLookupObjectModel table = fLpmStateProvier.getLpmTable(id);
        if(table != null) {
            table.ruleLookup(InetAddresses.fromInteger(ipv4.intValue()), depth, /*nextHop,*/ (ret == 0), ts);
        }
    }

}
