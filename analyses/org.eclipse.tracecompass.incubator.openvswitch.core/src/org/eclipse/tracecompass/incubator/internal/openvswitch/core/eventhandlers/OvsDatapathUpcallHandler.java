package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsUpcallsStateProvider;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.UpcallType;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.VirtualPortModel;
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
public class OvsDatapathUpcallHandler extends OpenvSwitchEventHandler {

    private final OvsUpcallsStateProvider fDatapathInterfaceSP;

    /**
     * @param layout :
     * @param stateProvider :
     */
    public OvsDatapathUpcallHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsUpcallsStateProvider stateProvider) {
        super(layout);
        this.fDatapathInterfaceSP = stateProvider;
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
        String inPortName = content.getFieldValue(String.class, layout.fieldPortName());
        Integer error = content.getFieldValue(Integer.class, layout.fieldError());


        ITmfEventField swFlowKey = content.getField(layout.fieldSwKey());
        ITmfEventField phyHeader = swFlowKey.getField(layout.fieldPhyHeader());
        Integer skbMark = phyHeader.getFieldValue(Integer.class, layout.fieldSkbMark());
        Integer inPort = phyHeader.getFieldValue(Integer.class, layout.fieldInPort());



        if (skbMark == null || upcallType == null || inPortName == null || inPort == null || error == null) {
            throw new IllegalArgumentException("ovs_dp_upcall event does not have expected fields"); //$NON-NLS-1$ ;
        }

        /* Was the upcall sent successfully by the datapath */
        if(error == 0) {
            /* Use the statesystem to look for the required in-port */
            fDatapathInterfaceSP.updateWaitingUpcallsNumber(ts, /*increment*/ true);
            VirtualPortModel port = fDatapathInterfaceSP.getPortById(inPort, inPortName);
            port.sendUpcall(skbMark, UpcallType.fromInt(upcallType), ts);
        }
    }

}
