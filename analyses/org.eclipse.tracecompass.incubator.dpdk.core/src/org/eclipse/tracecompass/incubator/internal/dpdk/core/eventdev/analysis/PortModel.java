package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class PortModel {

    private final int fQuark;
    private final ITmfStateSystemBuilder fSs;

    private final int fId;

    private PortStatus status;
    private long migrationStartTimestamp = 0L;
    private int migrationDurationMinimum = Integer.MAX_VALUE;
    private int migrationDurationMaximum = Integer.MIN_VALUE;
    private int migrationTotalDurations = 0;

    private final int fNewEventThreshold;
    private final int fDequeueDepth;
    private final int fEnqueueDepth;

    private final RingModel fRingBufferRx;
    private final RingModel fRingBufferCq;

    private int inflightCredit;

    private long nbEnqEvents = 0;
    private long nbDeqEvents = 0;
    private long nbMigrations = 0;

    private long zeroPolls = 0L;
    private long totPolls = 0L;

    public PortModel(int portId, int newEventThreshold, int enqueueDepth, int dequeueDepth,
            RingModel ringRx, RingModel ringCq, EventDevBackendType backendType, int devQuark, @NonNull ITmfStateSystemBuilder ss) {
        this.fId = portId;
        this.fNewEventThreshold = newEventThreshold;
        this.fEnqueueDepth = enqueueDepth;
        this.fDequeueDepth = dequeueDepth;
        this.fSs = ss;
        this.fRingBufferRx = ringRx;
        this.fRingBufferCq = ringCq;
        this.inflightCredit = 0;
        this.status = PortStatus.UNKNOWN;
        this.zeroPolls = 0L;
        this.totPolls = 0L;

        int portSetQuark = fSs.getQuarkRelativeAndAdd(devQuark, IDpdkEventDevModelAttributes.PORTS);
        this.fQuark = fSs.getQuarkRelativeAndAdd(portSetQuark, String.valueOf(this.fId));

        if(backendType == EventDevBackendType.DSW) {
            this.status = PortStatus.IDLE;

            int portStatusQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_STATUS);
            fSs.modifyAttribute(0, this.status.name(), portStatusQuark);

            int portLoadQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_LOAD);
            fSs.modifyAttribute(0, 0, portLoadQuark);
        }

        int newThresholdQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_NEW_THRESHOLD);
        fSs.modifyAttribute(0, this.fNewEventThreshold, newThresholdQuark);

        int enqueueDepthQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_ENQUEUE_DEPTH);
        fSs.modifyAttribute(0, this.fEnqueueDepth, enqueueDepthQuark);

        int dequeueDepthQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_DEQUEUE_DEPTH);
        fSs.modifyAttribute(0, this.fDequeueDepth, dequeueDepthQuark);

        int rxQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_RX);
        fSs.modifyAttribute(0, 0L, rxQuark);

        int txQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_TX);
        fSs.modifyAttribute(0, 0L, txQuark);

        int nbMigrationQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.NB_FLOW_MIGRATION);
        fSs.modifyAttribute(0, this.nbMigrations, nbMigrationQuark);

        int inflightQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.INFLIGHTS_CREDIT);
        fSs.modifyAttribute(0, this.inflightCredit, inflightQuark);

        int zeroPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.ZERO_POLLS);
        fSs.modifyAttribute(0, 0L, zeroPollsQuark);

        int totPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.TOT_POLLS);
        fSs.modifyAttribute(0, 0L, totPollsQuark);

        /* Setting up RX and CQ rings */
        int ringSetQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.RINGS);
        int quarkRingRx = fSs.getQuarkRelativeAndAdd(ringSetQuark, fRingBufferRx.getName());
        int quarkRingCq = fSs.getQuarkRelativeAndAdd(ringSetQuark, fRingBufferCq.getName());

        this.fRingBufferRx.setQuark(quarkRingRx, fSs);
        this.fRingBufferCq.setQuark(quarkRingCq, fSs);
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

    /**
     * The port enqueue @p nbEvents to the eventdev
     * @param nbEvents
     * @param ts
     */
    public void enqueueEvents(int nbEvents, long ts) {
        this.nbEnqEvents += nbEvents;

        try {
            int txQuark = fSs.getQuarkRelative(this.fQuark, IDpdkEventDevModelAttributes.EVENT_RX);
            fSs.modifyAttribute(ts, this.nbEnqEvents, txQuark);

            //if(fRingBufferRx != null) {
            //    fRingBufferRx.enqueue(nbEvents, ts);
            //}
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * The port dequeue @nbEvents from the eventdev
     *
     * @param nbEvents
     * @param ts
     */
    public void dequeueEvents(int nbEvents, long zeroPollValue, long totPollValue, long ts) {
        this.nbDeqEvents += nbEvents;

        try {
            int rxQuark = fSs.getQuarkRelative(this.fQuark, IDpdkEventDevModelAttributes.EVENT_TX);
            fSs.modifyAttribute(ts, this.nbDeqEvents, rxQuark);

            int zeroPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.ZERO_POLLS);
            fSs.modifyAttribute(ts, (zeroPollValue - this.zeroPolls), zeroPollsQuark);

            int totPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.TOT_POLLS);
            fSs.modifyAttribute(ts, (totPollValue - this.totPolls), totPollsQuark);

        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }

        this.totPolls = totPollValue;
        this.zeroPolls = zeroPollValue;
    }

    public void pushToRingBufferCq(int nbEvents, long ts) {
        if(fRingBufferCq != null) {
            fRingBufferCq.enqueue(nbEvents, ts);
        }
    }

    public void pullFromRingBufferRx(int nbEvents, long ts) {
        if(fRingBufferRx != null) {
            fRingBufferRx.dequeue(nbEvents, ts);
        }
    }


    public void updateInflight(int portInflightCredit, long ts) {
        this.inflightCredit = portInflightCredit;

        try {
            int inflightQuark = fSs.getQuarkRelative(this.fQuark, IDpdkEventDevModelAttributes.INFLIGHTS_CREDIT);
            fSs.modifyAttribute(ts, this.inflightCredit, inflightQuark);
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateLoad(int portLoad, long ts) {
        try {
            int portLoadQuark = fSs.getQuarkRelative(this.fQuark, IDpdkEventDevModelAttributes.PORT_LOAD);
            fSs.modifyAttribute(ts, portLoad, portLoadQuark);
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(PortStatus newStatus, long ts) {
        this.status = newStatus;

        int portStatusQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PORT_STATUS);
        fSs.modifyAttribute(ts, this.status.name(), portStatusQuark);

        /* New migration is started */
        if(newStatus == PortStatus.PAUSING) {
            nbMigrations ++;
            this.migrationStartTimestamp = ts;

            int nbMigrationQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.NB_FLOW_MIGRATION);
            fSs.modifyAttribute(ts, this.nbMigrations, nbMigrationQuark);
        }
        else if(newStatus == PortStatus.IDLE) {
            int migrationDuration = (int) ((ts - this.migrationStartTimestamp) / 1000);
            if(migrationDuration < this.migrationDurationMinimum) {
                this.migrationDurationMinimum = migrationDuration;
            } else if (migrationDuration > this.migrationDurationMaximum) {
                this.migrationDurationMaximum =  migrationDuration;
            }

            this.migrationTotalDurations += migrationDuration;

            int migrationLatencyQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.FLOW_MIGRATION_LATENCY);
            fSs.modifyAttribute(this.migrationStartTimestamp, migrationDuration, migrationLatencyQuark);

            fSs.modifyAttribute(ts, 0, migrationLatencyQuark);
        }
    }

    public PortStatus getStatus() {
        return this.status;
    }

    @Override
    public void finalize() {
        System.out.println("[Migration] Port " + this.fId + " :\n" +
                "\tMinimum Latency = " + this.migrationDurationMinimum + "\n" +
                "\tAverage Latency = " + (this.migrationTotalDurations / this.nbMigrations) + "\n" +
                "\tMaximum Latency = " + this.migrationDurationMaximum + "\n");
    }
}
