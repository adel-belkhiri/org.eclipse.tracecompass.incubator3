package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsUpcallsStateProvider;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.HandlerThreadModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.UpcallModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.UpcallStatus;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class OvsDowncallTransactMultipleHandler extends OpenvSwitchEventHandler {
    private final OvsUpcallsStateProvider fStatePovider;

    /**
     * @param layout xx
     * @param stateProvider xx
     */
    public OvsDowncallTransactMultipleHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsUpcallsStateProvider stateProvider) {
        super(layout);
        this.fStatePovider = stateProvider;
    }

    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {
        ITmfEventField content = event.getContent();
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        Integer upcallsNumber = content.getFieldValue(Integer.class, layout.fieldUpcallsIdsLength());
        long[] upcallsIdsArray = content.getFieldValue(long[].class, layout.fieldUpcallsIds());
        long ts = event.getTimestamp().getValue();

        if (upcallsNumber == null) {
            throw new IllegalArgumentException("ovs_dpif:downcall_transact_multiple event does not have expected fields"); //$NON-NLS-1$ ;
        }

        if(upcallsNumber == 0 || upcallsIdsArray == null) {
            return;
        }

        /* look for the handler */
        for(long upcallId : upcallsIdsArray) {
            HandlerThreadModel handler = fStatePovider.getHandlerByUpcallId((int)upcallId);
            if(handler != null) {
                UpcallModel upcall = handler.getUpcallById((int)upcallId);
                if(upcall != null && upcall.getStatus() == UpcallStatus.UPCALL_PROCESSING) {
                    handler.terminateUpcall((int) upcallId, ts);
                }
            }
            else {
                //Activator.getInstance().logError("upcall handler missing : upcall number " + upcallId ); //$NON-NLS-1$
            }
        }

    }

}
