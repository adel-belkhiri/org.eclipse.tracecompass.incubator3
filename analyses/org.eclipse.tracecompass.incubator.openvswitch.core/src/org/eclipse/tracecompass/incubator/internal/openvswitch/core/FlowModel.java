package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;

/**
 * @author Adel Belkhiri
 *
 */
public class FlowModel {

    private final ITmfStateSystemBuilder fSs;
    private int fQuark;
    private OvsFlow fFlow;

    /**
     * @param ufid xx
     * @param dpQuark xx
     * @param ss xx
     */
    public FlowModel(String ufid, int dpQuark, ITmfStateSystemBuilder ss) {
        this.fSs = ss;
        this.fFlow = new OvsFlow(ufid);

        try {
            /* Get the quark of this flow */
            int flowsQuark = fSs.getQuarkRelative(dpQuark, IOpenVSwitchModelAttributes.FLOWS);
            this.fQuark = fSs.getQuarkRelativeAndAdd(flowsQuark, ufid);

            int stateQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IOpenVSwitchModelAttributes.FLOW_STATE);
            fSs.modifyAttribute(0, FlowState.FLOW_IDLE.toString(), stateQuark);

            int nbHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IOpenVSwitchModelAttributes.NB_HIT);
            fSs.modifyAttribute(0, Long.valueOf(0), nbHitQuark);

            int nbDumpQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IOpenVSwitchModelAttributes.NB_DUMP);
            fSs.modifyAttribute(0, Long.valueOf(0), nbDumpQuark);

            int nbEvictionQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IOpenVSwitchModelAttributes.NB_EVICTION);
            fSs.modifyAttribute(0, Long.valueOf(0), nbEvictionQuark);

        }
        catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change the state of a flow
     * @param ts xx
     * @param newState xx
     */
    public void setState(long ts, FlowState newState) {
        int stateQuark;
        fFlow.setState(newState);
        stateQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IOpenVSwitchModelAttributes.FLOW_STATE);
        fSs.modifyAttribute(ts, newState.toString(), stateQuark);

        if(newState == FlowState.FLOW_IDLE) {
            //fFlow.resetNbHitPerPeriod();
            //int nbHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IOpenVSwitchModelAttributes.NB_HIT);
            //fSs.modifyAttribute(ts, Long.valueOf(0), nbHitQuark);
        }
    }

    /**
     * The current flow was matched by a packet
     * @param ts xx
     *
     */
    public void setMatch(long ts) {
        fFlow.setMatch();
        try {
            int nbHitQuark = fSs.getQuarkRelative(this.fQuark, IOpenVSwitchModelAttributes.NB_HIT);
            fSs.modifyAttribute(ts, fFlow.getNbHit(), nbHitQuark);
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }
}
