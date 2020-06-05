package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.DpdkVhostStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class EthernetDeviceVhostCreateEventHandler extends DpdkEventHandler {

    //DpdkVhostStateProvider fVhostStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public EthernetDeviceVhostCreateEventHandler(@NonNull DpdkVhostAnalysisEventLayout layout, DpdkVhostStateProvider stateProvider) {
        super(layout, stateProvider);
    }


    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {

        DpdkVhostAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        String devName = content.getFieldValue(String.class, layout.fieldDevName());
        String ifceName = content.getFieldValue(String.class, layout.fieldIfceName());
        long[] hwAddr = content.getFieldValue(long[].class, layout.fieldHwAddr());



        if (devName == null || ifceName == null || hwAddr == null) {
            throw new IllegalArgumentException(layout.eventEthDevVhostCreate() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        fVhostStateProvier.addDevice(devName, ifceName, hwAddr);
    }
}
