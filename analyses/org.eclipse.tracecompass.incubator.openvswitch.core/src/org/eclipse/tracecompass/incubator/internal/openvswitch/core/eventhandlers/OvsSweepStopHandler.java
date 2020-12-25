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
public class OvsSweepStopHandler extends OpenvSwitchEventHandler {

    /**
     * @param layout :
     * @param stateProvider :
     */
    public OvsSweepStopHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsRevalidationStateProvider stateProvider) {
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
        String dp_name     = content.getFieldValue(String.class, layout.fieldDpName());
        Integer revalidator_id     = content.getFieldValue(Integer.class, layout.fieldRevalidatorId());
        Long dump_seq     = content.getFieldValue(Long.class, layout.fieldDumpSeq());

        if (revalidator_id == null || dump_seq == null || dp_name == null) {
            throw new IllegalArgumentException("ovs_dpif:revalidation_stop event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* update the state system */
        Integer dpQuark = ss.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.DATAPATHS, dp_name);
        Integer revalidatorQuark = ss.getQuarkRelativeAndAdd(dpQuark, revalidator_id.toString());

        ss.removeAttribute(ts, revalidatorQuark);
    }

}
