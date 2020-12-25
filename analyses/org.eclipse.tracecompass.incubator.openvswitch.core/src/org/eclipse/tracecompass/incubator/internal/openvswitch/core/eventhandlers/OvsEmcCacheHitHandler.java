package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.DatapathModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.FlowModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsFlowsStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class OvsEmcCacheHitHandler extends OpenvSwitchEventHandler {

    OvsFlowsStateProvider fOvsFlowsSP;

    /**
     * @param layout xx
     * @param stateProvider xx
     */
    public OvsEmcCacheHitHandler(IOpenvSwitchAnalysisEventLayout layout, OvsFlowsStateProvider stateProvider) {
        super(layout);
        this.fOvsFlowsSP = stateProvider;
    }

    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        String dpUfid = content.getFieldValue(String.class, layout.fieldUfid());
        long ts = event.getTimestamp().getValue();

        if (dpUfid == null) {
            throw new IllegalArgumentException("ovs_emc_cache_hit event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* First update some statistics */
        fOvsFlowsSP.incrementCacheHitNumbers(ts, true /*isEmcCacheHit*/);

        DatapathModel dp = fOvsFlowsSP.getDatapathByUfid(dpUfid);
        if(dp != null) {
            FlowModel flow = dp.searchFlowByDpUfid(dpUfid);
            if(flow != null) {
                flow.setMatch(ts);
            }
        }
    }

}
