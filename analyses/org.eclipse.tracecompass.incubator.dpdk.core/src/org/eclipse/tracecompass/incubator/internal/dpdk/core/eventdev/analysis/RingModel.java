package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class RingModel {

    private int quark;
    private ITmfStateSystemBuilder ss;

    private final int fId;
    private final String fName;
    private final int fCapacity;


    private int nbAvailableEvents;


    public RingModel(int ringId, String name, int capacity) {
        this.fId = ringId;
        this.fName = name;
        this.fCapacity = capacity;
        this.setNbEvents(0);
    }

    public int getId() {
        return this.fId;
    }

    public String getName() {
        return this.fName;
    }

    public int getCapacity() {
        return this.fCapacity;
    }

    public int getNbEvents() {
        return this.nbAvailableEvents;
    }

    public void setNbEvents(int nbEvt) {
        this.nbAvailableEvents = nbEvt;
    }

    public int getQuark() {
        return quark;
    }

    public void setQuark(int quark, ITmfStateSystemBuilder ss) {
        this.quark = quark;
        this.ss = ss;

        int capacityQuark = ss.getQuarkRelativeAndAdd(this.quark, IDpdkEventDevModelAttributes.RING_CAPACITY);
        ss.modifyAttribute(0, this.fCapacity, capacityQuark);

        int nbEventsQuark = ss.getQuarkRelativeAndAdd(this.quark, IDpdkEventDevModelAttributes.NB_EVENTS);
        ss.modifyAttribute(0, 0, nbEventsQuark);
    }

    public void enqueue(int nbEvts, long ts) {
        this.nbAvailableEvents += nbEvts;

        if(this.quark > 0 && this.ss != null) {
            int nbEventsQuark = this.ss.getQuarkRelativeAndAdd(this.quark, IDpdkEventDevModelAttributes.NB_EVENTS);
            this.ss.modifyAttribute(ts, this.nbAvailableEvents, nbEventsQuark);
        }
    }

    public void dequeue(int nbEvts, long ts) {
        this.nbAvailableEvents -= nbEvts;

        if(this.quark > 0 && this.ss != null) {
            int nbEventsQuark = this.ss.getQuarkRelativeAndAdd(this.quark, IDpdkEventDevModelAttributes.NB_EVENTS);
            this.ss.modifyAttribute(ts, this.nbAvailableEvents, nbEventsQuark);
        }
    }
}
