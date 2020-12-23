package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.DpdkVhostStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.VhostNetworkDeviceModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.VirtualDeviceModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class QueueSetupEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     * @param stateProvider
     */
    public QueueSetupEventHandler(@NonNull DpdkVhostAnalysisEventLayout layout, DpdkVhostStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkVhostAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        //dev_name = "net_vhost0", vid = 0, port = 0, virtqueue_id = 1, type = "Rx"
        ITmfEventField content = event.getContent();
        String devName = content.getFieldValue(String.class, layout.fieldDevName());
        Integer vid = content.getFieldValue(Integer.class, layout.fieldVid());
        Integer virtqueueId = content.getFieldValue(Integer.class, layout.fieldVirtqueueId());
        String queueType = content.getFieldValue(String.class, layout.fieldTypeQueue());

        if (devName == null || vid == null || virtqueueId == null || queueType == null) {
            throw new IllegalArgumentException(layout.eventQueueSetup() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        if(vid >= 0) {
            VhostNetworkDeviceModel dev = fVhostStateProvier.getDevice(devName);
            if(dev != null) {
                VirtualDeviceModel vidObj = dev.getVid(vid);
                if(vidObj != null) {
                    vidObj.addQueue(virtqueueId, queueType);
                }
            }
        }
    }

}
