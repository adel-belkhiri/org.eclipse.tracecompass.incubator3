package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;

/**
 * @author Adel Belkhiri
 *
 */
public class DatapathModel {

    private final Map<String /*flow key*/, FlowModel> fFlows = new HashMap<>();
    private final Map<String /*flow ufid*/, String /*flow key*/> flowIdMap = new HashMap<>();

    private int nbCurrentInstalledFlows;
    private String datapathName;
    private int fDpQuark;
    private final ITmfStateSystemBuilder fSs;

    /**
     * A datapath is instancitaed by is a collection of virtual ports.
     * @param dp xx
     * @param ss xx
     */
    public DatapathModel(String dpName, ITmfStateSystemBuilder ss) {
        this.datapathName = dpName;
        this.fSs = ss;
        this.nbCurrentInstalledFlows = 0;

        /* Create a Stat subtreee within the state system */
        fSs.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_EMC_CACHE_HIT);
        //fSs.modifyAttribute(0, 0, nbEmcCacheHitQuark);
        fSs.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_MEGAFLOW_CACHE_HIT);
        //fSs.modifyAttribute(0, 0, nbEmcCacheHitQuark);

        /* create a Datapaths subtree within the state system */
        this.fDpQuark = fSs.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.DATAPATHS, this.datapathName);
        fSs.getQuarkRelativeAndAdd(this.fDpQuark, IOpenVSwitchModelAttributes.FLOWS);
        int nbCurrentFlowsQuark = fSs.getQuarkRelativeAndAdd(this.fDpQuark, IOpenVSwitchModelAttributes.NB_CURRENT_FLOWS);
        fSs.modifyAttribute(0, nbCurrentInstalledFlows, nbCurrentFlowsQuark);
    }

    /**
     * @return id
     */
    public String getName() {
        return datapathName;
    }

    /**
     * Create a flow
     * @param ovsUfid xx
     * @param dpUfid xx
     * @return An instance of FlowModel class
     */
    public FlowModel createFlow(String ovsUfid, @Nullable String dpUfid) {
        FlowModel flow = fFlows.get(ovsUfid);
        if(flow == null) {
            flow = new FlowModel(ovsUfid, this.fDpQuark, this.fSs);
            fFlows.put(ovsUfid, flow);

            if(dpUfid != null) {
                /* add a mapping between the tmp ufid and the abs ufid */
                flowIdMap.put(dpUfid, ovsUfid);
            }
        }
        return flow;
    }

    /**
     * Search a flow by its ID and then return it
     * @param ufid xx
     * @return An instance of FlowModel class
     */
    public @Nullable FlowModel getFlow(String ufid) {
         return fFlows.get(ufid);
    }

    /**
     * @param ovsUfid xx
     * @param dpUfid xx
     * @param create xx
     * @return An instance of FlowModel class
     */
    public @Nullable FlowModel searchFlowByDpUfid(String dpUfid) {
         String flowAbsId = flowIdMap.get(dpUfid);
        if(flowAbsId != null) {
            return fFlows.get(flowAbsId);
        }

        /* flow not found*/
        return null;
    }

    /**
     * Change the state of a flow (Cached in the datapath, not cached).
     * @param ts xx
     * @param ufid xx
     * @param state xx
     */
    public void setFlowState(long ts, String ufid, FlowState state) {
        FlowModel flow = fFlows.get(ufid);
        int nbCurrentFlowsQuark;
        try {
            nbCurrentFlowsQuark = fSs.getQuarkRelative(this.fDpQuark, IOpenVSwitchModelAttributes.NB_CURRENT_FLOWS);
            if(flow != null) {
                flow.setState(ts, state);
                /* Update the number of active flows in the state system*/
                if(state == FlowState.FLOW_KERNEL_INSTALLED) {
                    this.nbCurrentInstalledFlows++;
                }
                else
                    if(state == FlowState.FLOW_IDLE) {
                        this.nbCurrentInstalledFlows--;
                    }

                fSs.modifyAttribute(ts, this.nbCurrentInstalledFlows, nbCurrentFlowsQuark);
            }
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * When a flow is cached, it may use another ufid. This function is used to set the new ufid
     * @param flowKey A flow key representation
     * @param ufid the Ufid of the installed flow.
     */
    public void updateFlowDatapathId(String flowKey, String ufid) {
        flowIdMap.remove(ufid);
        flowIdMap.put(ufid, flowKey);
    }

}
