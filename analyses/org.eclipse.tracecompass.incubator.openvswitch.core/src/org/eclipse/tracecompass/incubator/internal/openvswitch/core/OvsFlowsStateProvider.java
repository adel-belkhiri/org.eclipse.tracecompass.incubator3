package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.IOpenvSwitchAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsDatapathUpcallHandler2;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsEmcCacheHitHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsFlowCmdDelHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsFlowCmdNewHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsFlowMatchHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsMegaflowCacheHitHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsProcessUpcallHandler;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateValueTypeException;
import org.eclipse.tracecompass.statesystem.core.exceptions.TimeRangeException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.statesystem.AbstractTmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.experiment.TmfExperiment;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adel Belkhiri
 *
 */
public class OvsFlowsStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, OpenvSwitchEventHandler> fEventNames;

    /* Events layout */
    private final IOpenvSwitchAnalysisEventLayout fLayout;

    private final Map<String, DatapathModel> fDatapaths = new HashMap<>();

    /* Statistics */
    private long nbEmcCacheHit;
    private long nbMegaflowCacheHit;
    private long nbMissUpcalls;
    /**
     * @param experiment : trace
     * @param layout : layout
     * @param id : id
     */
    protected OvsFlowsStateProvider(TmfExperiment experiment, IOpenvSwitchAnalysisEventLayout layout, String id) {
        super(experiment, id);
        fLayout = layout;
        fEventNames = buildEventNames(layout);

        nbEmcCacheHit = 0;
        nbMegaflowCacheHit = 0;
        nbMissUpcalls = 0;
    }


    /**
     * Get the version of this state provider
     */
    @Override
    public int getVersion() {
        return VERSION;
    }


    /**
     * Get a trace
     */
    @Override
    public TmfExperiment getTrace() {
        ITmfTrace trace = super.getTrace();
        if (trace instanceof TmfExperiment) {
            return (TmfExperiment) trace;
        }
        throw new IllegalStateException("OpenvSwitch Flow Analysis : Associated trace should be an experiment"); //$NON-NLS-1$
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new OvsFlowsStateProvider(this.getTrace(), this.fLayout, "OpenvSwitch Flows Analysis"); //$NON-NLS-1$
    }


    /**
     * buildEventNames() : Map the events needed for this analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, OpenvSwitchEventHandler> buildEventNames(IOpenvSwitchAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, OpenvSwitchEventHandler> builder = ImmutableMap.builder();

         /* OpenvSwitch Events */
         builder.put(layout.eventOvsFlowCmdNew(),  new OvsFlowCmdNewHandler(layout, this));
         builder.put(layout.eventOvsFlowCmdDel(),  new OvsFlowCmdDelHandler(layout, this));
         builder.put(layout.eventOvsFlowMatch() ,  new OvsFlowMatchHandler(layout, this));
         builder.put(layout.eventOvsEmcCacheHit() ,  new OvsEmcCacheHitHandler(layout, this));
         builder.put(layout.eventOvsMegaflowCacheHit() ,  new OvsMegaflowCacheHitHandler(layout, this));
         builder.put(layout.eventOvsDpUpcall(),   new OvsDatapathUpcallHandler2(layout, this));
         builder.put(layout.eventOvsProcessUpcall(), new OvsProcessUpcallHandler(layout, this));
         return (builder.build());
    }


    /**
     * Dispatch required events to their handler while processing the trace.
     * @param event : event currently processed.
     *
     */
    @Override
    protected void eventHandle(ITmfEvent event) {

        String eventName = event.getName();

        final ITmfStateSystemBuilder ss = NonNullUtils.checkNotNull(getStateSystemBuilder());

        OpenvSwitchEventHandler eventHandler = fEventNames.get(eventName);
        if (eventHandler != null) {
            try {
                eventHandler.handleEvent(ss, event);
            }
            catch (TimeRangeException | StateValueTypeException | AttributeNotFoundException e) {
                Activator.getInstance().logError("Exception while building OVS Flows State System", e); //$NON-NLS-1$
            }
        }
    }


    /**
     * @param id xx
     * @return xx
     */
    public @Nullable DatapathModel getDatapath(String id) {
        DatapathModel dp= fDatapaths.get(id);
        if(dp == null) {
            dp = new DatapathModel(id, NonNullUtils.checkNotNull(getStateSystemBuilder()));
            fDatapaths.put(id, dp);
        }

        return dp;
    }

    /**
     * Search a datapath using the absolute id of the flow
     * @param ufid xx
     * @return xx
     */
    public @Nullable DatapathModel getDatapathByUfid(String ufid) {
        for(DatapathModel dp : fDatapaths.values()) {
            if(dp.getFlow(ufid) != null) {
                return dp;
            }
        }
        return null;
    }


    /**
     * Search a datapath using the temporary id of the flow
     * @param dpUfid xxx
     * @return xx
     */
    public @Nullable DatapathModel searchDatapathByDpUfid(String dpUfid) {
        for(DatapathModel dp : fDatapaths.values()) {
            if(dp.searchFlowByDpUfid(dpUfid) != null) {
                return dp;
            }
        }
        return null;
    }


    /**
     * update statistics
     * @param ts xx
     * @param isEmcCacheHit xx
     */
    public void incrementCacheHitNumbers(long ts, boolean isEmcCacheHit) {
        final ITmfStateSystemBuilder ss = NonNullUtils.checkNotNull(getStateSystemBuilder());
        if(isEmcCacheHit) {
            nbEmcCacheHit ++;
            /* update the stat related to Emc type cache hit */
            int nbEmcCacheHitQuark = ss.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_EMC_CACHE_HIT);
            ss.modifyAttribute(ts, this.nbEmcCacheHit, nbEmcCacheHitQuark);

        } else {
            nbMegaflowCacheHit ++;
            /* update the stat related to Megaflow type cache hit */
            int nbMegaflowCacheHitQuark = ss.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_MEGAFLOW_CACHE_HIT);
            ss.modifyAttribute(ts, this.nbMegaflowCacheHit, nbMegaflowCacheHitQuark);
        }
    }

    /**
     * @param ts
     */
    public void incrementNbMissUpcalls(long ts) {

        nbMissUpcalls ++;

        final ITmfStateSystemBuilder ss = NonNullUtils.checkNotNull(getStateSystemBuilder());

        /* update the stat related the number of miss upcalls */
        int nbMissUpcallQuark = ss.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_MISS_UPCALLS);
        ss.modifyAttribute(ts, this.nbMissUpcalls, nbMissUpcallQuark);

    }
}
