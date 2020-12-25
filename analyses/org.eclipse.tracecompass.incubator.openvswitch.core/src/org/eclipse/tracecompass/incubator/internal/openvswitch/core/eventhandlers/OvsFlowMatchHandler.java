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
public class OvsFlowMatchHandler extends OpenvSwitchEventHandler {

    OvsFlowsStateProvider fOvsFlowsSP;

    /**
     * @param layout xx
     * @param stateProvider xx
     */
    public OvsFlowMatchHandler(IOpenvSwitchAnalysisEventLayout layout, OvsFlowsStateProvider stateProvider) {
        super(layout);
        this.fOvsFlowsSP = stateProvider;
    }

    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        String dpUfid = content.getFieldValue(String.class, layout.fieldUfid());
        String dpName = content.getFieldValue(String.class, layout.fieldDpName());
        long ts = event.getTimestamp().getValue();

        if (dpUfid == null || dpName == null) {
            throw new IllegalArgumentException("ovs_flow_match event does not have expected fields"); //$NON-NLS-1$ ;
        }

        DatapathModel dp = fOvsFlowsSP.getDatapath(dpName);
        if(dp != null) {
            FlowModel flow = dp.searchFlowByDpUfid(dpUfid);
            if(flow != null) {
                // the flow was used to process a packet
                flow.setMatch(ts);
            }
        }
    }
}
