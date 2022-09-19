package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class PortStatModel {

    private final int fQuark;
    private final ITmfStateSystemBuilder fSs;

    private final int fId;
    private final int fPriority;

    private long nbEvents = 0;

    private final Map<@NonNull Integer /*flow id*/, @NonNull Integer /*nb pkts*/> fFlows = new HashMap<>();

    public PortStatModel(int portId, int priority, int queueQuark, @NonNull ITmfStateSystemBuilder ss) {
        this.fId = portId;
        this.fSs = ss;
        this.fPriority = priority;

        int portSetQuark = fSs.getQuarkRelativeAndAdd(queueQuark, IDpdkEventDevModelAttributes.ATTACHED_PORTS);
        this.fQuark = fSs.getQuarkRelativeAndAdd(portSetQuark, "Port\\" + String.valueOf(this.fId)); //$NON-NLS-1$


        int prioQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PRIORITY);
        fSs.modifyAttribute(0, priority, prioQuark);

        int rxQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_TX);
        fSs.modifyAttribute(0, nbEvents, rxQuark);
    }


    public int getId() {
        return this.fId;
    }

    public int getPriority() {
        return this.fPriority;
    }

    /**
     *
     * @param ts
     */
    public void sendEvent(Integer flowId, long ts) {
        this.nbEvents++;
        int rxQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_TX);
        fSs.modifyAttribute(ts, nbEvents, rxQuark);

        /* Keep track of flows only for atomic and parallel queues */
        if(flowId != null) {
            Integer nbPktsPerFlow = fFlows.get(flowId);
            if(nbPktsPerFlow != null) {
                fFlows.put(flowId, nbPktsPerFlow + 1);
            } else {
                fFlows.put(flowId, 1);
            }

            int flowsQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.FLOWS);
            int flowQuark = fSs.getQuarkRelativeAndAdd(flowsQuark, String.valueOf(flowId));
            int countFlowQuark = fSs.getQuarkRelativeAndAdd(flowQuark, IDpdkEventDevModelAttributes.COUNT_PER_FLOW);
            fSs.modifyAttribute(ts, fFlows.get(flowId), countFlowQuark);

            int nbFlowsQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.NB_FLOWS);
            fSs.modifyAttribute(ts, fFlows.size(), nbFlowsQuark);
        }
    }

    public void pinFlow(Integer flowId, long ts) {

        /* Keep track of flows only for atomic and parallel queues */
        if(flowId != null) {
            int flowsQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.FLOWS);
            int flowQuark = fSs.getQuarkRelativeAndAdd(flowsQuark, String.valueOf(flowId));
            fSs.modifyAttribute(ts,  "pinned (#" + String.valueOf(flowId) + ")" , flowQuark); //$NON-NLS-1$
        }
    }

    public void unpinFlow(Integer flowId, long ts) {

        /* Keep track of flows only for atomic and parallel queues */
        if(flowId != null) {
            int flowsQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.FLOWS);
            int flowQuark = fSs.getQuarkRelativeAndAdd(flowsQuark, String.valueOf(flowId));
            fSs.modifyAttribute(ts, null, flowQuark);
        }
    }
}
