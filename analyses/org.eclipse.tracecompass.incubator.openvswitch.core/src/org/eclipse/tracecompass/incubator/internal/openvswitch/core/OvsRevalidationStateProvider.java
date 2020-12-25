package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.Map;

import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.IOpenvSwitchAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsFlowLimitUpdateHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsRevalidationStartHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsRevalidationStopHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsSweepStartHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsSweepStopHandler;
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

public class OvsRevalidationStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    //private final Map<Integer, RevalidatorModel> fRevalidators = new HashMap<>();

    /* Events layout */
    private final IOpenvSwitchAnalysisEventLayout fLayout;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, OpenvSwitchEventHandler> fEventNames;


    /**
     * @param experiment : trace
     * @param layout : layout
     * @param id : id
     */
    protected OvsRevalidationStateProvider(TmfExperiment experiment, IOpenvSwitchAnalysisEventLayout layout, String id) {
        super(experiment, id);
        fLayout = layout;
        fEventNames = buildEventNames(layout);
    }


    /**
     * buildEventNames() : Map the events needed for this analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, OpenvSwitchEventHandler> buildEventNames(IOpenvSwitchAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, OpenvSwitchEventHandler> builder = ImmutableMap.builder();

         /* OpenvSwitch Events */
         builder.put(layout.eventOvsFlowLimitUpdate(),  new OvsFlowLimitUpdateHandler(layout, this));

         builder.put(layout.eventOvsRevalidationStart(),  new OvsRevalidationStartHandler(layout, this));
         builder.put(layout.eventOvsRevalidationStop(),  new OvsRevalidationStopHandler(layout, this));
         builder.put(layout.eventOvsSweepStart(),  new OvsSweepStartHandler(layout, this));
         builder.put(layout.eventOvsSweepStop(),  new OvsSweepStopHandler(layout, this));
         return (builder.build());
    }

    /**
     * Get version of state system
     */
    @Override
    public int getVersion() {
        return VERSION;
    }

    @Override
    public TmfExperiment getTrace() {
        ITmfTrace trace = super.getTrace();
        if (trace instanceof TmfExperiment) {
            return (TmfExperiment) trace;
        }
        throw new IllegalStateException("OpenvSwitch Revalidation Analysis : The associated trace should be an experiment"); //$NON-NLS-1$
    }

    @Override
    public ITmfStateProvider getNewInstance() {
        return new OvsRevalidationStateProvider(this.getTrace(), this.fLayout, "OpenvSwitch revalidation analysis"); //$NON-NLS-1$
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
                Activator.getInstance().logError("Exception while building OVS Revalidation State System", e); //$NON-NLS-1$
            }
        }

    }


}
