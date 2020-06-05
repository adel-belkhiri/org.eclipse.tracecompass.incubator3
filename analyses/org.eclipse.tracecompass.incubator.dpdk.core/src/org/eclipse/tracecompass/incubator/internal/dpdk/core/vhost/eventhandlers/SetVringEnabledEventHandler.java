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
public class SetVringEnabledEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     * @param stateProvider
     */
    public SetVringEnabledEventHandler(@NonNull DpdkVhostAnalysisEventLayout layout, DpdkVhostStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkVhostAnalysisEventLayout layout = getLayout();

        //{ vid = 0, vring_idx = 0, vq = 0x11FEB2E80, size = 256, callfd = 672, kickfd = 671 }
        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer vid = content.getFieldValue(Integer.class, layout.fieldVid());
        Integer vringIdx = content.getFieldValue(Integer.class, layout.fieldVringIdx());
        Integer vqPointer = content.getFieldValue(Integer.class, layout.fieldVqPointer());
        Integer vqSize = content.getFieldValue(Integer.class, layout.fieldSize());
        Integer vqCallFd = content.getFieldValue(Integer.class, layout.fieldCallFd());
        Integer vqKickFd = content.getFieldValue(Integer.class, layout.fieldKickFd());

        if (vid == null || vringIdx == null || vqPointer == null || vqSize == null ||
                vqCallFd == null|| vqKickFd == null) {
            throw new IllegalArgumentException(layout.eventSetVringEnabled() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        if((vqSize > 0) && (vqCallFd > 0) && (vqKickFd > 0)) {
            NetworkDeviceModel dev = fVhostStateProvier.searchDeviceByVid(vid);
            if(dev != null) {
                VirtualDeviceModel vidObj = dev.getVid(vid);
                if(vidObj != null) {
                    vidObj.setQueueEnabled(vringIdx, vqPointer , vqSize /*, vqCallFd, vqKickFd*/);
                }
            }
        }
    }

}
