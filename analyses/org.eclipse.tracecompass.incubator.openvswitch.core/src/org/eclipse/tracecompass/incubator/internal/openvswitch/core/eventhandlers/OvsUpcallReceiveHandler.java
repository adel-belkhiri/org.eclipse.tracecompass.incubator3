package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsUpcallsStateProvider;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.HandlerThreadModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.UpcallModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.UpcallStatus;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.UpcallType;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.VirtualPortModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class OvsUpcallReceiveHandler extends OpenvSwitchEventHandler {

    private final OvsUpcallsStateProvider fStatePovider;

    /**
     * @param layout :
     * @param stateProvider :
     */
    public OvsUpcallReceiveHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsUpcallsStateProvider stateProvider) {
        super(layout);
        this.fStatePovider = stateProvider;
    }

    /**
     * @param ss : ITmfStateSystemBuilder
     * @param event : ITmfEvent
     * @throws AttributeNotFoundException :
     */
    /*
     * { handler_id = 2, in_port = 2, pkt_mark = 321 }
     */
    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {

        ITmfEventField content = event.getContent();
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        Long handlerId = content.getFieldValue(Long.class, layout.fieldHandlerId());
        Integer inPort = content.getFieldValue(Integer.class, layout.fieldInPort());
        Integer skbMark = content.getFieldValue(Integer.class, layout.fieldSkbMark());
        Integer upcallType = content.getFieldValue(Integer.class, layout.fieldUpcallType());
        Integer slowPathReason = content.getFieldValue(Integer.class, layout.fieldSlowPathReason());


        long ts = event.getTimestamp().getValue();

        if (handlerId == null || inPort == null || skbMark == null || upcallType == null || slowPathReason ==  null) {
            throw new IllegalArgumentException("ovs_dpif:upcall_read event does not have expected fields"); //$NON-NLS-1$ ;
        }

        // decrease the size of the waiting queue
        fStatePovider.updateWaitingUpcallsNumber(ts, /*increment*/ false);

        /* look for the port through which it was sent */
        VirtualPortModel port = fStatePovider.getPortById(inPort, "<no-name>"); //$NON-NLS-1$
        UpcallModel upcall = port.getUpcallById(skbMark);
        if(upcall != null) {
            /* upate the type and the status of the upcall */
            upcall.setType(UpcallType.fromInt(upcallType.intValue()), slowPathReason);
            upcall.setStatus(UpcallStatus.UPCALL_PROCESSING);

            /* tell the thread handler to process this upcall */
            HandlerThreadModel handler = fStatePovider.getHandler(handlerId);
            long upcallStartTime = port.getUpcallSendingTime(upcall.getId());
            assert (upcallStartTime > 0);
            handler.processUpcall(upcall, upcallStartTime, ts);

            port.terminateUpcall(upcall.getId(), ts);
        }
        else {
            //Activator.getInstance().logError("Ovs_upcall_receive_handler event missing : upcall number " + skbMark + " was not found"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
