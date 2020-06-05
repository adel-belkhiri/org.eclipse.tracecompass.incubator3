package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import com.google.common.net.InetAddresses;
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
public class RteLpmLookupEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     * @param stateProvider
     */
    public RteLpmLookupEventHandler(@NonNull DpdkLpmAnalysisEventLayout layout, DpdkLpmStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLpmAnalysisEventLayout layout = getLayout();

        // name=test_lpm_perf, ipv4=12345, rule_depth=3, next_hop=170
        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        String name = content.getFieldValue(String.class, layout.fieldName());
        Long ipv4 = content.getFieldValue(Long.class, layout.fieldIPv4Addr());
        Integer depth = content.getFieldValue(Integer.class, layout.fieldRuleDepth());
        //Integer nextHop = content.getFieldValue(Integer.class, layout.fieldNextHop());
        Integer ret = content.getFieldValue(Integer.class, layout.fieldOpResult());

        if (name == null || ipv4 == null || depth == null || /*nextHop == null ||*/ ret == null) {
            throw new IllegalArgumentException(layout.eventRteLpmLookup() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LpmTableModel table = fLpmStateProvier.getLpmTable(name);
        if(table != null) {
            table.ruleLookup(InetAddresses.fromInteger(ipv4.intValue()), depth, /*nextHop,*/ (ret == 0), ts);
        }
    }

}
