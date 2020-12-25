package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.DatapathModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.FlowState;
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
public class OvsFlowCmdNewHandler extends OpenvSwitchEventHandler {

    OvsFlowsStateProvider fOvsFlowsSP;

    /**
     * @param layout xx
     * @param stateProvider xx
     */
    public OvsFlowCmdNewHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsFlowsStateProvider stateProvider) {
        super(layout);
        this.fOvsFlowsSP = stateProvider;
    }


    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        String dpName = content.getFieldValue(String.class, layout.fieldDpName());
        long ts = event.getTimestamp().getValue();

        /* This is the id of the flow from the point of view of the datapath. It is a relative key */
        String dpUfid = content.getFieldValue(String.class, layout.fieldUfid());

        /* This is the aboslute key of the flow */
        ITmfEventField swFlowKey = content.getField(layout.fieldSwKey());

        if (dpName == null || swFlowKey == null || dpUfid == null) {
            throw new IllegalArgumentException("ovs_flow_cmd_new event does not have expected fields"); //$NON-NLS-1$ ;
        }

        String strKey = parseSwFlowKey(swFlowKey);
        //String ovsUfid = UUID.nameUUIDFromBytes(strKey.getBytes()).toString();

        DatapathModel dp = fOvsFlowsSP.getDatapath(dpName);
        if(dp != null) {
            if(dp.getFlow(strKey) == null) {
                dp.createFlow(strKey, dpUfid);
            } else {
                /* the key exist but now will be used under a new (datapath) ufid */
                dp.updateFlowDatapathId(strKey, dpUfid);
            }
            dp.setFlowState(ts, strKey, FlowState.FLOW_KERNEL_INSTALLED);
        }
    }
}
