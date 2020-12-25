package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.DatapathModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.FlowModel;
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
public class OvsFlowCmdDelHandler extends OpenvSwitchEventHandler {

    OvsFlowsStateProvider fOvsFlowsSP;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public OvsFlowCmdDelHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsFlowsStateProvider stateProvider) {
        super(layout);
        this.fOvsFlowsSP = stateProvider;
    }


    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        String dpName = content.getFieldValue(String.class, layout.fieldDpName());
        String dpUfid = content.getFieldValue(String.class, layout.fieldUfid());
        long ts = event.getTimestamp().getValue();

        ITmfEventField swFlowKey = content.getField(layout.fieldSwKey());

        if (dpName == null || dpUfid == null || swFlowKey == null) {
            throw new IllegalArgumentException("ovs_flow_cmd_del event does not have expected fields"); //$NON-NLS-1$ ;
        }

        //create a UUID for the flow from the representation of its flow key
        //String key = NonNullUtils.checkNotNull(swFlowKey.toString()) ;
        //String OvsUfid = UUID.nameUUIDFromBytes(key.getBytes()).toString();

        String strKey = parseSwFlowKey(swFlowKey);
        //String ovsUfid = UUID.nameUUIDFromBytes(strKey.getBytes()).toString();

        DatapathModel dp = fOvsFlowsSP.getDatapath(dpName);
        if(dp != null) {
            FlowModel flow = dp.getFlow(strKey);
            if(flow == null) {
                /* create a flow since there is a missing event ovs_flow_cmd_new */
                flow = dp.createFlow(strKey, dpUfid);
                dp.setFlowState(0, strKey, FlowState.FLOW_KERNEL_INSTALLED);
            }
            dp.setFlowState(ts, strKey, FlowState.FLOW_IDLE);
        }
    }
}
