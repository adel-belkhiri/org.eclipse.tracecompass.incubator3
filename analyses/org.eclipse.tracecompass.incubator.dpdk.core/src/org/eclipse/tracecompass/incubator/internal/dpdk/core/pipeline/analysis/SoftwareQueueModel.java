package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.tmf.core.util.Pair;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class SoftwareQueueModel {

    //private final int fId;
    private final int fQueueSize;
    private int currentNbPackets;

    private final String fName;
    private final int fQuark;
    private final List<Pair<Integer, Long>> fQueueLatency = new ArrayList<>();
    private final ITmfStateSystemBuilder fSs;


    public SoftwareQueueModel(String name, int queueSize, ITmfStateSystemBuilder ss) {
        this.fQueueSize = queueSize;
        this.fName = name;
        this.currentNbPackets = 0;
        this.fSs = ss;

        fQuark = this.fSs.getQuarkAbsoluteAndAdd(IDpdkPipelineModelAttributes.SW_QUEUES, getName());

        int queueSizeQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.QUEUE_CAPACITY);
        this.fSs.modifyAttribute(0, this.fQueueSize, queueSizeQuark);

        int nbPktQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_PKT);
        this.fSs.modifyAttribute(0, this.currentNbPackets, nbPktQuark);

        int queueLatencyQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.QUEUE_LATENCY);
        this.fSs.modifyAttribute(0, 0L, queueLatencyQuark);

    }

    public int getCapacity() {
        return this.fQueueSize;
    }

    public String getName() {
        return this.fName;
    }

    /**
     * Enqueue packets to the ring queue
     *
     * @param nbPkts
     * @param ts
     */
    public void enqueuePackets(int nbPkts, long ts) {
        //to address the case where recv event is fired before the send event
        if(this.currentNbPackets >= 0) {
            fQueueLatency.add(new Pair<>(nbPkts, ts));
        }

        this.currentNbPackets += nbPkts;

        int nbPktQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_PKT);
        this.fSs.modifyAttribute(ts, this.currentNbPackets, nbPktQuark);
    }


    /**
     * Dequeue packets from the ring queue
     *
     * @param nbPkts
     * @param ts
     */
    public void dequeuePackets(int nbPkts, long ts) {

        this.currentNbPackets -= nbPkts;
        int nbPktQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_PKT);
        this.fSs.modifyAttribute(ts, this.currentNbPackets, nbPktQuark);

        int nbPktsOut = nbPkts;
        long sum = 0;

        while((nbPktsOut > 0) && (fQueueLatency.size() != 0)) {
            Pair<Integer, Long> elem = fQueueLatency.get(0);

            if(elem != null) {
                Integer nbPktsIn = elem.getFirst();
                Long duration = ts - elem.getSecond();

                if(nbPktsOut >= nbPktsIn) {
                    sum = sum + (nbPktsIn * duration);
                    nbPktsOut -= nbPktsIn;
                    fQueueLatency.remove(0);
                } else {
                    sum = sum + (nbPktsOut * duration);
                    nbPktsIn -= nbPktsOut;
                    fQueueLatency.set(0, new Pair<>(nbPktsIn, elem.getSecond()));
                    nbPktsOut = 0;
                }
            }
        }

        long avg_latency = (long)(nbPkts <= 0 ? 0.0 :  (sum / nbPkts));
        int queueLatencyQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.QUEUE_LATENCY);
        this.fSs.modifyAttribute(ts, avg_latency , queueLatencyQuark);
    }

}
