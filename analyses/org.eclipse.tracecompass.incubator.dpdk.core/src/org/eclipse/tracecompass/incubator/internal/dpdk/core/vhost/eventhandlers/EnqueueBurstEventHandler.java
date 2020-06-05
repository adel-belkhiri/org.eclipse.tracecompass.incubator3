package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.DpdkVhostStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.NetworkDeviceModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.VirtualDeviceModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class EnqueueBurstEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     * @param stateProvider
     */
    public EnqueueBurstEventHandler(@NonNull DpdkVhostAnalysisEventLayout layout, DpdkVhostStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkVhostAnalysisEventLayout layout = getLayout();

        //vid = 0, vq = 0x11FE86500, queue_id = 1, nb_rx = 32}
        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        Integer vid = content.getFieldValue(Integer.class, layout.fieldVid());
        Integer nbTx = content.getFieldValue(Integer.class, layout.fieldNbTx());
        Integer vqPointer = content.getFieldValue(Integer.class, layout.fieldVqPointer());

        if (vid == null || vqPointer == null || nbTx == null) {
            throw new IllegalArgumentException(layout.eventEnqueueBurst() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        NetworkDeviceModel dev = fVhostStateProvier.searchDeviceByVid(vid);
        if(dev != null) {
            VirtualDeviceModel vidObj = dev.getVid(vid);
            if(vidObj != null) {
                vidObj.setNumberOfmBuffer(vqPointer, nbTx, ts);
            }
        }
    }

}
