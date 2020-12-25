package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.HashMap;
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
public class VirtualPortModel {

    private int inPort;
    private String inPortName;
    private final Map<Integer /*ID*/, Pair<Integer /*Quark*/, UpcallModel>> fUpcallsBuilders  = new HashMap<>();
    private final ITmfStateSystemBuilder fSs;
    private int fVirtPortQuark;
    private final TmfAttributePool fUpcallsPool;

    private int nbMissUpcalls;
    private int nbUserspaceUpcalls;
                                            /*Methods*/
    /**
     * VirtualPortModel class constructor
     * @param inPort : in-port
     * @param ss : State system
     */
    public VirtualPortModel(int inPort, String inPortName, ITmfStateSystemBuilder ss) {
        this.inPort = inPort;
        this.inPortName = inPortName;
        this.fSs = ss;
        this.nbMissUpcalls = 0;
        this.nbUserspaceUpcalls = 0;

        /* set the quark of this vPort */
        this.fVirtPortQuark = fSs.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.VPORTS, this.inPortName);

        /* set its ID */
        int inPortIdQuark = fSs.getQuarkRelativeAndAdd(fVirtPortQuark,IOpenVSwitchModelAttributes.VPORT_ID);
        fSs.modifyAttribute(0, this.inPort, inPortIdQuark);

        /* set the number of sent upcalls to zero */
        int nSentMissUpcallsQuark = fSs.getQuarkRelativeAndAdd(fVirtPortQuark, IOpenVSwitchModelAttributes.NB_MISS_UPCALLS);
        fSs.modifyAttribute(0, this.nbMissUpcalls, nSentMissUpcallsQuark);

        int nSentUserspaceUpcallsQuark = fSs.getQuarkRelativeAndAdd(fVirtPortQuark, IOpenVSwitchModelAttributes.NB_USERSPACE_UPCALLS);
        fSs.modifyAttribute(0, this.nbUserspaceUpcalls, nSentUserspaceUpcallsQuark);

        /* add a list of its sent upcalls */

        int upcallsListQuark = fSs.getQuarkRelativeAndAdd(this.fVirtPortQuark,IOpenVSwitchModelAttributes.UPCALLS);

        fUpcallsPool = new TmfAttributePool(this.fSs, upcallsListQuark, QueueType.PRIORITY);
    }

    /**
     * Add an upcall to the list of upcalls handled by this thread
     * @param upcallId xx
     * @param type xx
     * @param ts : timestamp
     */
    public void sendUpcall(int upcallId, UpcallType type, long ts) {

        int nSentUpcallsQuark;

        /* create an upcall object and add it to the list */
        UpcallModel newUpcall = new UpcallModel(upcallId, this.inPort);
        newUpcall.setStatus(UpcallStatus.UPCALL_WAITING);


        /* try to add the upcall details to the state system */
        try {
            if(UpcallType.MISS_UPCALL == type) {
                nSentUpcallsQuark = fSs.getQuarkRelative(this.fVirtPortQuark, IOpenVSwitchModelAttributes.NB_MISS_UPCALLS);
                fSs.modifyAttribute(ts, ++ this.nbMissUpcalls, nSentUpcallsQuark);
            }
            else {
                nSentUpcallsQuark = fSs.getQuarkRelative(this.fVirtPortQuark, IOpenVSwitchModelAttributes.NB_USERSPACE_UPCALLS);
                fSs.modifyAttribute(ts, ++ this.nbUserspaceUpcalls, nSentUpcallsQuark);
            }

        }
        catch (AttributeNotFoundException e) {
            e.printStackTrace();
            return;
        }

        /* add a new upcall in the state system */

        /****************************************************
         *  Commented because it makes the processing slow.
         * So dont keep it in the state system
         * ***************************************************/

       /* int freeSlotQuark = fUpcallsPool.getAvailable();

        int upcallIdQuark = fSs.getQuarkRelativeAndAdd(freeSlotQuark,String.valueOf(IOpenVSwitchModelAttributes.UPCALL_ID));
        fSs.modifyAttribute(ts, upcallId, upcallIdQuark);

        // set the type of this upcall : Bad upcall, Miss upcall or UserAction upcall
        int upcallTypeQuark = fSs.getQuarkRelativeAndAdd(freeSlotQuark,String.valueOf(IOpenVSwitchModelAttributes.UPCALL_TYPE));
        fSs.modifyAttribute(ts, type.toString(), upcallTypeQuark);

        // set waiting status for this upcall
        int upcallStatusQuark = fSs.getQuarkRelativeAndAdd(freeSlotQuark,String.valueOf(IOpenVSwitchModelAttributes.UPCALL_STATUS));
        fSs.modifyAttribute(ts, UpcallStatus.UPCALL_WAITING.toString(), upcallStatusQuark);


        fUpcallsBuilders.put(newUpcall.getId(), new Pair<>(freeSlotQuark, newUpcall));
        */

    }

    /**
     *
     * @param upcallId xx
     * @param ts xx
     */
    public void terminateUpcall(int upcallId, long ts) {
        Integer quark = getUpcallQuark(upcallId);
        if(quark != null) {
            fUpcallsPool.recycle(quark.intValue(), ts);
            /* remove the upcall from memory to save mem space */
            fUpcallsBuilders.remove(upcallId);
        }
    }

    /**
     * Research an upcall using its ID (skb_mark)
     * @param id : the identifier of the upcall
     * @return the associated upcall
     */
    public @Nullable UpcallModel getUpcallById(int id) {

        Pair<Integer, UpcallModel> upcall = fUpcallsBuilders.get(id);
        if(upcall != null) {
            return upcall.getSecond();
        }
        return null;
    }

    /**
     * Research an upcall quark using its ID (skb_mark)
     * @param id : the identifier of the upcall
     * @return the associated upcall quark
     */
    public @Nullable Integer getUpcallQuark(int id) {

        Pair<Integer, UpcallModel> upcall = fUpcallsBuilders.get(id);
        if(upcall != null) {
            return upcall.getFirst();
        }
        return null;
    }

    /**
     * Get the quark of ovs virtual port
     * @return a quark
     */
    public Integer getQuark() {
        return fVirtPortQuark;
    }

    /**
     * Get the sending time of the upcall
     * @param id Upcall identifier
     * @return A timestamp
     */
    public long getUpcallSendingTime(int id) {
        Integer upcallQuark = getUpcallQuark(id);
        if(upcallQuark != null) {
            try {
                int upcallIdQuark = fSs.getQuarkRelative(upcallQuark.intValue(), IOpenVSwitchModelAttributes.UPCALL_ID);
                return fSs.getOngoingStartTime(upcallIdQuark);
            } catch (AttributeNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        return -1;
    }

}
