package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.Activator;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsUpcallsStateProvider;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.HandlerThreadModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class OvsUpcallUserspaceExecEndHandler extends OpenvSwitchEventHandler {

    private final OvsUpcallsStateProvider fStatePovider;

    /**
     * @param layout xx
     * @param stateProvider xx
     */
    public OvsUpcallUserspaceExecEndHandler(IOpenvSwitchAnalysisEventLayout layout, OvsUpcallsStateProvider stateProvider) {
        super(layout);
        this.fStatePovider = stateProvider;
    }

    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {

        ITmfEventField content = event.getContent();
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        Integer skbMark = content.getFieldValue(Integer.class, layout.fieldSkbMark());
        long ts = event.getTimestamp().getValue();

        if (skbMark == null) {
            throw new IllegalArgumentException("ovs_dpif:upcall_userspace_exec_stop event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* look for the handler */
        HandlerThreadModel handler = fStatePovider.getHandlerByUpcallId(skbMark);
        if(handler != null) {
            handler.terminateUpcall(skbMark, ts);
        }
        else {
            Activator.getInstance().logError("upcall handler missing : upcall number " + skbMark ); //$NON-NLS-1$
        }
    }
}
