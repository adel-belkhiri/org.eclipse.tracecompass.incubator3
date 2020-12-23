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
public class WriteToTxRingEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     * @param stateProvider
     */
    public WriteToTxRingEventHandler(@NonNull DpdkVhostAnalysisEventLayout layout, DpdkVhostStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkVhostAnalysisEventLayout layout = getLayout();

        //vid = 0, vq = 0x11FE86500, count = 32, nb_rx = 32, last_avail_idx = 32, avail_idx = 256 }
        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer vid = content.getFieldValue(Integer.class, layout.fieldVid());
        Integer availIdx = content.getFieldValue(Integer.class, layout.fieldAvailIdx());
        Integer lastAvailIdx = content.getFieldValue(Integer.class, layout.fieldLastAvailIdx());
        Integer vqPointer = content.getFieldValue(Integer.class, layout.fieldVqPointer());

        if (vid == null || availIdx == null || vqPointer == null || lastAvailIdx == null) {
            throw new IllegalArgumentException(layout.eventReadFromRxRing() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        VhostNetworkDeviceModel dev = fVhostStateProvier.searchDeviceByVid(vid);
        if(dev != null) {
            VirtualDeviceModel vidObj = dev.getVid(vid);
            if(vidObj != null) {
                vidObj.calculatePercentageOfQueueOccupancy(vqPointer, availIdx, lastAvailIdx, ts);
            }
        }
    }

}
