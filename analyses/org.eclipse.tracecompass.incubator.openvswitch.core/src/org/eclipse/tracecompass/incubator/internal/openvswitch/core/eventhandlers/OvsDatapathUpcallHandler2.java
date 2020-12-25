package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsFlowsStateProvider;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.UpcallType;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 * This class implements the ovs_dp_upcall event handler
 *
 */
public class OvsDatapathUpcallHandler2 extends OpenvSwitchEventHandler {

    private final OvsFlowsStateProvider fFlowsSP;

    /**
     * @param layout :
     * @param stateProvider :
     */
    public OvsDatapathUpcallHandler2(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsFlowsStateProvider stateProvider) {
        super(layout);
        this.fFlowsSP = stateProvider;
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

        Integer upcallType = content.getFieldValue(Integer.class, layout.fieldUpcallType());
        String dpName = content.getFieldValue(String.class, layout.fieldDpName());

        ITmfEventField swFlowKey = content.getField(layout.fieldSwKey());
        ITmfEventField phyHeader = swFlowKey.getField(layout.fieldPhyHeader());
        Integer skbMark = phyHeader.getFieldValue(Integer.class, layout.fieldSkbMark());
        //Integer inPort = phyHeader.getFieldValue(Integer.class, layout.fieldInPort());



        if (skbMark == null || upcallType == null || dpName == null) {
            throw new IllegalArgumentException("ovs_dp_upcall2 event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* First update some statistics */
        if(UpcallType.fromInt(upcallType) == UpcallType.MISS_UPCALL) {
            fFlowsSP.incrementNbMissUpcalls(ts);
        }

    }
}
