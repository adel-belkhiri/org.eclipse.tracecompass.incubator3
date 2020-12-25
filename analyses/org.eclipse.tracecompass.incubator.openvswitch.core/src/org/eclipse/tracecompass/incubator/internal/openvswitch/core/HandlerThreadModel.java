package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfAttributePool;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfAttributePool.QueueType;
import org.eclipse.tracecompass.tmf.core.util.Pair;


/**
 * @author Adel Belkhiri
 *
 */
public class HandlerThreadModel {
    private int nProcessedUpcalls;
    private long threadId;
    private final Map<Integer /*ID*/, Pair<Integer /*Quark*/, UpcallModel>>  fUpcallsBuilders = new HashMap<>();
    private final ITmfStateSystemBuilder fSs;
    private int fHandlerQuark;
    private final TmfAttributePool fUpcallsPool;
    private List<Integer> quarksInUse;

    /*Methods*/
    /**
     * HandlerThreadModel class constructor
     * @param threadId xx
     * @param ss xx
     */
    public HandlerThreadModel(long threadId, ITmfStateSystemBuilder ss) {
        this.nProcessedUpcalls = 0;
        this.threadId = threadId;
        this.quarksInUse =  new ArrayList<>();
        fSs = ss;

        fHandlerQuark = fSs.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.HANDLERS, String.valueOf(threadId));


        /* Handler thread identifier */
        int handlerIdQuark = fSs.getQuarkRelativeAndAdd(fHandlerQuark,IOpenVSwitchModelAttributes.HANDLER_ID);
        fSs.modifyAttribute(0, this.threadId, handlerIdQuark);

        /* Number of waiting and processed upcalls */
        int nProcessedUpcallsQuark = fSs.getQuarkRelativeAndAdd(fHandlerQuark,IOpenVSwitchModelAttributes.N_PROCESSED_UPCALLS);
        fSs.modifyAttribute(0, this.nProcessedUpcalls, nProcessedUpcallsQuark);

        /* List of upcalls */
        int upcallsListQuark = fSs.getQuarkRelativeAndAdd(fHandlerQuark,IOpenVSwitchModelAttributes.UPCALLS);
        fUpcallsPool = new TmfAttributePool(this.fSs, upcallsListQuark, QueueType.PRIORITY);
    }


    /**
     * Add an upcall to the list of upcalls handled by this thread
     * @param upcall : an upcall sent by kernel to ovs-vswitchd
     * @param sendingTime xx
     * @param ts : timestamp
     */
    public void processUpcall(UpcallModel upcall, long sendingTime, long ts) {

        int OverlapDepth = getOverlapLevelNumber(sendingTime);

        List<Integer> freeSlotQuarks = createUpcall(upcall, sendingTime, OverlapDepth);
        if(freeSlotQuarks.size() > 0) {
            updateUpcallStatus(upcall, ts);
        }

        /*remove the current used quark from the list */
        freeSlotQuarks.remove(freeSlotQuarks.size()-1);

        for(int upcallQuark : freeSlotQuarks) {
            if(upcallQuark > 0) {
                fUpcallsPool.recycle(upcallQuark, ts);
            }
        }
    }

    /**
     * Terminate an upcall in the state system since it was fully processed
     * @param upcallId : an upcall sent by kernel to ovs-vswitchd
     * @param ts : timestamp
     */
    public void terminateUpcall(int upcallId, long ts) {

        Pair<Integer, UpcallModel> upcall = fUpcallsBuilders.get(upcallId);
        if(upcall != null) {
            Integer upcallQuark = upcall.getFirst();
            if(upcallQuark > 0) {
                this.quarksInUse.remove(upcallQuark);
                fUpcallsPool.recycle(upcallQuark, ts);
            }
            fUpcallsBuilders.remove(upcallQuark);
        }

    }

    /**
     * @throws AttributeNotFoundException
     *
     */

    private int getOverlapLevelNumber(long startTime) {
        int counter = 1;
        int upcallsListQuark = -1;
        int upcallStatusQuark;
        long endTime = -1;

        try {
            upcallsListQuark = fSs.getQuarkRelative(fHandlerQuark,IOpenVSwitchModelAttributes.UPCALLS);
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
        List<Integer> children = fSs.getSubAttributes(upcallsListQuark, false);

        for(int child : children) {

            if(this.quarksInUse.contains(child)) {
                continue;
            }

            try {
                upcallStatusQuark = fSs.getQuarkRelative(child, IOpenVSwitchModelAttributes.UPCALL_STATUS);
                endTime = fSs.getOngoingStartTime(upcallStatusQuark);
            }
            catch (AttributeNotFoundException e) {
                e.printStackTrace();
            }

            if(startTime >= endTime) {
                // this level can hold
                return counter;
            }
            counter ++;
        }

        //create a new level
        return counter;
    }
    /**
     *
     * @param upcall
     * @param sendingStartTime
     * @return
     */
    private List<Integer>  createUpcall(UpcallModel upcall, long sendingStartTime, int depth) {
        int nProcessedUpcallsQuark, freeSlotQuark = -1;
        List<Integer> freeSlotQuarks = new ArrayList<>();

        for(int i=0; i<depth; i++) {
            freeSlotQuark = fUpcallsPool.getAvailable();
            freeSlotQuarks.add(freeSlotQuark);
        }

        /* try to add the upcall details to the state system */
        try {
            nProcessedUpcallsQuark = fSs.getQuarkRelative(fHandlerQuark,IOpenVSwitchModelAttributes.N_PROCESSED_UPCALLS);
            //nWaitingUpcallsQuark = fSs.getQuarkRelative(fHandlerQuark,IOpenVSwitchModelAttributes.N_PROCESSED_UPCALLS);
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
            return freeSlotQuarks;
        }

        int upcallIdQuark = fSs.getQuarkRelativeAndAdd(freeSlotQuark,String.valueOf(IOpenVSwitchModelAttributes.UPCALL_ID));
        fSs.modifyAttribute(sendingStartTime, upcall.getId(), upcallIdQuark);

        int upcallTypeQuark = fSs.getQuarkRelativeAndAdd(freeSlotQuark,String.valueOf(IOpenVSwitchModelAttributes.UPCALL_TYPE));
        fSs.modifyAttribute(sendingStartTime, upcall.getType(), upcallTypeQuark);

        int upcallInPortQuark = fSs.getQuarkRelativeAndAdd(freeSlotQuark,String.valueOf(IOpenVSwitchModelAttributes.UPCALL_IN_PORT));
        fSs.modifyAttribute(sendingStartTime, upcall.getInPort(), upcallInPortQuark);

        int upcallStatusQuark = fSs.getQuarkRelativeAndAdd(freeSlotQuark,String.valueOf(IOpenVSwitchModelAttributes.UPCALL_STATUS));
        fSs.modifyAttribute(sendingStartTime, UpcallStatus.UPCALL_WAITING.toString(), upcallStatusQuark);

        /* add the upcall to the list */
        fUpcallsBuilders.put(upcall.getId(), new Pair<>(freeSlotQuark, upcall));

        /* and update the number of processed upcalls*/
        fSs.modifyAttribute(sendingStartTime, fUpcallsBuilders.size(), nProcessedUpcallsQuark);
        //fSs.modifyAttribute(sendingStartTime, fUpcallsBuilders.size(), nWaitingUpcallsQuark);

        this.quarksInUse.add(freeSlotQuark);

        return freeSlotQuarks;

    }

    /**
     * Get the quark of the thread handler
     * @return a quark
     */
    public Integer getQuark() {
        return fHandlerQuark;
    }


    /**
     * Get the quark of the thread handler
     * @param upcallId xx
     * @return an UpcallModel
     */
    public @Nullable UpcallModel getUpcallById(int upcallId) {
        Pair<Integer, UpcallModel>  u = fUpcallsBuilders.get(upcallId);
        if(u != null) {
            return u.getSecond();
        }
        return null;
    }

    /**
     * Add an upcall to the list of upcalls handled by this thread
     * @param upcall : an upcall sent by kernel to ovs-vswitchd
     * @param sendingTime xx
     * @param ts : timestamp
     */
    public void updateUpcallStatus(UpcallModel upcall, long ts) {
        Pair<Integer, UpcallModel>  u = fUpcallsBuilders.get(upcall.getId());
        if(u != null){
            Integer quark = u.getFirst();
            int upcallStatusQuark = fSs.getQuarkRelativeAndAdd(quark.intValue(), IOpenVSwitchModelAttributes.UPCALL_STATUS);
            fSs.modifyAttribute(ts, upcall.getStatus().toString(), upcallStatusQuark);
        }
    }
}
