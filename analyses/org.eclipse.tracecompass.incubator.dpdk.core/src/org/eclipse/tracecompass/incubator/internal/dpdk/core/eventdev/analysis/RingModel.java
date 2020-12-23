package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class RingModel {

    private int quark;

    private final int fId;
    private final String fName;
    private final int fCapacity;

    private int nbEvents;


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
        return this.nbEvents;
    }

    public void setNbEvents(int nbEvt) {
        this.nbEvents = nbEvt;
    }

    public int getQuark() {
        return quark;
    }

    public void setQuark(int quark) {
        this.quark = quark;
    }

    public void enqueue(int nbEvts) {
        this.nbEvents += nbEvts;
    }

    public void dequeue(int nbEvts) {
        this.nbEvents -= nbEvts;
    }
}
