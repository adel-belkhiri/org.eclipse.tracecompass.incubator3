package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteLpmCreateEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     * @param stateProvider
     */
    public RteLpmCreateEventHandler(@NonNull DpdkLpmAnalysisEventLayout layout, DpdkLpmStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkLpmAnalysisEventLayout layout = getLayout();

        //Content   name=test_lpm_perf, max_rules=2000000, number_tlb8s=2048
        /* unpack the event */
        ITmfEventField content = event.getContent();
        //long ts = event.getTimestamp().getValue();

        String name = content.getFieldValue(String.class, layout.fieldName());
        Integer maxRules = content.getFieldValue(Integer.class, layout.fieldMaxRules());

        if (name == null || maxRules == null) {
            throw new IllegalArgumentException(layout.eventRteLpmCreate() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fLpmStateProvier.addLpmTable(name);
    }

}