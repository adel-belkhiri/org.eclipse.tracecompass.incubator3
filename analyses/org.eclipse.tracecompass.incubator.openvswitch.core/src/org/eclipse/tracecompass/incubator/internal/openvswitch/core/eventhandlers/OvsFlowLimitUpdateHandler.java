package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.IOpenVSwitchModelAttributes;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsRevalidationStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 * This class implements the ovs_dpif:flow_limit_update event handler
 *
 */
public class OvsFlowLimitUpdateHandler extends OpenvSwitchEventHandler {

    /**
     * @param layout :
     * @param stateProvider :
     */
    public OvsFlowLimitUpdateHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsRevalidationStateProvider stateProvider) {
        super(layout);
    }

    /**
     * @param ss : ITmfStateSystemBuilder
     * @param event : ITmfEvent
     * @throws AttributeNotFoundException :
     */
    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {

        ITmfEventField content = event.getContent();
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        long ts = event.getTimestamp().getValue();
        Long revalDuration = content.getFieldValue(Long.class, layout.fieldRevalidationDuration());
        Long flowLimit     = content.getFieldValue(Long.class, layout.fieldFlowLimit());
        String dp_name     = content.getFieldValue(String.class, layout.fieldDpName());

        if (revalDuration == null || flowLimit == null || dp_name == null) {
            throw new IllegalArgumentException("flow_limit_update event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* update the state system */
        Integer dpQuark = ss.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.STATISTICS, dp_name);
        Integer flowLimitQuark = ss.getQuarkRelativeAndAdd(dpQuark, IOpenVSwitchModelAttributes.FLOW_LIMIT);
        Integer revalDurationQuark = ss.getQuarkRelativeAndAdd(dpQuark, IOpenVSwitchModelAttributes.REVAL_DURATION);

        ss.modifyAttribute(ts, flowLimit, flowLimitQuark);
        ss.modifyAttribute(ts, revalDuration, revalDurationQuark);
    }

}
