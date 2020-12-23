package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class PortModel {

    private final int fQuark;
    private final ITmfStateSystemBuilder fSs;

    private final int fId;
    private final int fNewEventThreshold;
    private final int fDequeueDepth;
    private final int fEnqueueDepth;

    private final RingModel fRingBufferRx;
    private final RingModel fRingBufferCq;

    //private int inflightCredit;
    //private int inflights;

    //private long nbRxEvt;
    //private long nbTxEvt;


    public PortModel(int portId, int newEventThreshold, int enqueueDepth, int dequeueDepth,
            RingModel ringRx, RingModel ringCq, int devQuark, @NonNull ITmfStateSystemBuilder ss) {
        this.fId = portId;
        this.fNewEventThreshold = newEventThreshold;
        this.fEnqueueDepth = enqueueDepth;
        this.fDequeueDepth = dequeueDepth;
        this.fSs = ss;
        this.fRingBufferRx = ringRx;
        this.fRingBufferCq = ringCq;

        int portSetQuark = fSs.getQuarkRelativeAndAdd(devQuark, IDpdkEventDevModelAttributes.PORTS);
        this.fQuark = fSs.getQuarkRelativeAndAdd(portSetQuark, String.valueOf(this.fId));

        int newThresholdQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_NEW_THRESHOLD);
        fSs.modifyAttribute(0, this.fNewEventThreshold, newThresholdQuark);

        int enqueueDepthQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_ENQUEUE_DEPTH);
        fSs.modifyAttribute(0, this.fEnqueueDepth, enqueueDepthQuark);

        int dequeueDepthQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_DEQUEUE_DEPTH);
        fSs.modifyAttribute(0, this.fDequeueDepth, dequeueDepthQuark);

        int rxQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_RX);
        fSs.modifyAttribute(0, 0, rxQuark);

        int txQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_TX);
        fSs.modifyAttribute(0, 0, txQuark);

        int droppedQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_DROPPED);
        fSs.modifyAttribute(0, 0, droppedQuark);

        /* Setting up RX and CQ rings */
        int ringSetQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.RINGS);
        int quarkRingRx = fSs.getQuarkRelativeAndAdd(ringSetQuark, String.valueOf(this.fRingBufferRx.getId()));

        int nameQuark = fSs.getQuarkRelativeAndAdd(quarkRingRx, IDpdkEventDevModelAttributes.RING_NAME);
        fSs.modifyAttribute(0, ringRx.getName(), nameQuark);

        int capacityQuark = fSs.getQuarkRelativeAndAdd(quarkRingRx, IDpdkEventDevModelAttributes.RING_CAPACITY);
        fSs.modifyAttribute(0, ringRx.getCapacity(), capacityQuark);

        int nbEventsQuark = fSs.getQuarkRelativeAndAdd(quarkRingRx, IDpdkEventDevModelAttributes.NB_EVENTS);
        fSs.modifyAttribute(0, 0, nbEventsQuark);

        int quarkRingCq = fSs.getQuarkRelativeAndAdd(ringSetQuark, String.valueOf(this.fRingBufferCq.getId()));

        nameQuark = fSs.getQuarkRelativeAndAdd(quarkRingCq, IDpdkEventDevModelAttributes.RING_NAME);
        fSs.modifyAttribute(0, ringRx.getName(), nameQuark);

        capacityQuark = fSs.getQuarkRelativeAndAdd(quarkRingCq, IDpdkEventDevModelAttributes.RING_CAPACITY);
        fSs.modifyAttribute(0, ringRx.getCapacity(), capacityQuark);

        nbEventsQuark = fSs.getQuarkRelativeAndAdd(quarkRingCq, IDpdkEventDevModelAttributes.NB_EVENTS);
        fSs.modifyAttribute(0, 0, nbEventsQuark);

        this.fRingBufferRx.setQuark(quarkRingRx);
        this.fRingBufferRx.setQuark(quarkRingCq);
    }


    public int getId() {
        return this.fId;
    }

    public int getNewEventThreshold() {
        return this.fNewEventThreshold;
    }

    public int getDequeueDepth() {
        return this.fDequeueDepth;
    }

    public int getEnqueueDepth() {
        return this.fEnqueueDepth;
    }

    public void enqueueEvents(int nbEvents, long ts) {
        if(fRingBufferRx != null) {
            fRingBufferRx.enqueue(nbEvents);

            int nbEventsQuark = fSs.getQuarkRelativeAndAdd(fRingBufferRx.getQuark(), IDpdkEventDevModelAttributes.NB_EVENTS);
            fSs.modifyAttribute(ts, fRingBufferRx.getNbEvents(), nbEventsQuark);
        }
    }

    public void dequeueEvents(int nbEvents, long ts) {
        if(fRingBufferCq != null) {
            fRingBufferCq.dequeue(nbEvents);

            int nbEventsQuark = fSs.getQuarkRelativeAndAdd(fRingBufferCq.getQuark(), IDpdkEventDevModelAttributes.NB_EVENTS);
            fSs.modifyAttribute(ts, fRingBufferCq.getNbEvents(), nbEventsQuark);
        }
    }
}
