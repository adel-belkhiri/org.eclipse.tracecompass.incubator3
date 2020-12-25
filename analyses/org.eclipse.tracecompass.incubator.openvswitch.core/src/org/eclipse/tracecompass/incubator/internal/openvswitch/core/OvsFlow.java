package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

/**
 * @author Adel Belkhiri
 *
 */
public class OvsFlow {
    private String fUFID;
    private FlowState currentState;
    private long nbHit;
    private long nbDump;
    private long nbEviction;
//    private int currentRevalidatorId;


    /**
     * @param ufid xx
     */
    public OvsFlow(String ufid) {
        this.fUFID = ufid;
        this.nbHit = 0;
        this.nbDump = 0;
        this.nbEviction = 0;
        this.currentState = FlowState.FLOW_UNKNOWN_STATUS;
//      this.currentRevalidatorId = -1;
    }

    /**
     * @return xx
     */
    public Long getNbHit() {
        return this.nbHit;
    }
    /**
     * @return xx
     */
    public long getNbDump() {
        return this.nbDump;
    }

    /**
     * @return xx
     */
    public long getNbEviction() {
        return this.nbEviction;
    }

    /**
     * @return xx
     */
    public FlowState getCurrentState() {
        return this.currentState;
    }

    /**
     * Change the state of this flow
     * @param state xx
     */
    public void setState(FlowState state) {
        this.currentState = state;
    }

    /**
     * There is a packet which matched this flow
     */
    public void setMatch() {
        this.nbHit ++;
    }

    /**
     *
     */
    public void resetNbHitPerPeriod() {
        this.nbHit = 0;
    }

    /**
     * @return ufid
     */
    public String getUfid() {
        return this.fUFID;
    }
}
