package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.DpdkVhostStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.VhostNetworkDeviceModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

public class VhostUserAddConnectionEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     * @param stateProvider
     */
    public VhostUserAddConnectionEventHandler(@NonNull DpdkVhostAnalysisEventLayout layout, DpdkVhostStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkVhostAnalysisEventLayout layout = getLayout();

        //{ vid = 0, connfd = 571, name = "/tmp/vhost-user1"
        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer vid = content.getFieldValue(Integer.class, layout.fieldVid());
        Integer connfd = content.getFieldValue(Integer.class, layout.fieldConnFd());
        String ifceName = content.getFieldValue(String.class, layout.fieldIfceName());
        //long ts = event.getTimestamp().getValue();


        if (vid == null || connfd == null || ifceName == null) {
            throw new IllegalArgumentException(layout.eventVhostUserAddConnection() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        VhostNetworkDeviceModel dev = fVhostStateProvier.searchDeviceByIfaceName(ifceName);
        if(dev != null) {
            dev.attachVirtDevice(vid, connfd);
        }
    }

}
