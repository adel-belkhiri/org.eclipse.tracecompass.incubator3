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
public class RteLpmLookupX4EventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteLpmLookupX4EventHandler(@NonNull DpdkLpmAnalysisEventLayout layout, DpdkLpmStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLpmAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        String name = content.getFieldValue(String.class, layout.fieldName());
        long[] ips = content.getFieldValue(long[].class, layout.fieldIps());
        long[] nextHops = content.getFieldValue(long[].class, layout.fieldNextHops());
        long[] ruleDepths = content.getFieldValue(long[].class, layout.fieldRuleDepth());
        Integer defaultValue = content.getFieldValue(Integer.class, layout.fieldDefaultValue());

        if (name == null || ips == null || ruleDepths == null || nextHops == null || defaultValue == null) {
            throw new IllegalArgumentException(layout.eventRteLpmLookupx4() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LpmTableModel table = fLpmStateProvier.getLpmTable(name);
        if(table != null) {
            table.ruleLookupX4(ips, nextHops, ruleDepths, defaultValue, ts);
        }
    }

}
