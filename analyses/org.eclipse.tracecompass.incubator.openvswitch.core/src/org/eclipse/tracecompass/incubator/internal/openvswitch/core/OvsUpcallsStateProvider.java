package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.IOpenvSwitchAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsDatapathUpcallHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsDowncallTransactMultipleHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsQueueAmsgControllerHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsUpcallReceiveHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsUpcallUserspaceExecEndHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.OvsUpcallUserspaceExecStartHandler;
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

public class OvsUpcallsStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    private final Map<Long, HandlerThreadModel> fHandlers = new HashMap<>();

    private final Map<Integer, VirtualPortModel> fPorts = new HashMap<>();

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, OpenvSwitchEventHandler> fEventNames;

    /* Events layout */
    private final IOpenvSwitchAnalysisEventLayout fLayout;

    /* Size of upcalls waiting queue */
    private long nWaitingUpcalls;


    /**
     * @param experiment : trace
     * @param layout : layout
     * @param id : id
     */
    protected OvsUpcallsStateProvider(TmfExperiment experiment, IOpenvSwitchAnalysisEventLayout layout, String id) {
        super(experiment, id);
        fLayout = layout;
        fEventNames = buildEventNames(layout);
        nWaitingUpcalls = 0;
    }


    /**
     * buildEventNames() : Map the events needed for this analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, OpenvSwitchEventHandler> buildEventNames(IOpenvSwitchAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, OpenvSwitchEventHandler> builder = ImmutableMap.builder();

         /* OpenvSwitch Events */
         builder.put(layout.eventOvsDpUpcall(),  new OvsDatapathUpcallHandler(layout, this));
         builder.put(layout.eventOvsUpcallReceive(),  new OvsUpcallReceiveHandler(layout, this));
         builder.put(layout.eventUpcallUserspaceExecStart(),  new OvsUpcallUserspaceExecStartHandler(layout, this));
         builder.put(layout.eventUpcallUserspaceExecEnd(),  new OvsUpcallUserspaceExecEndHandler(layout, this));
         builder.put(layout.eventDowncallTransactMultiple(),  new OvsDowncallTransactMultipleHandler(layout, this));
         builder.put(layout.eventOvsQueueAmsgController(),  new OvsQueueAmsgControllerHandler(layout, this));
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
        throw new IllegalStateException("OpenvSwitch Analysis : The associated trace should be an experiment"); //$NON-NLS-1$
    }

    @Override
    public ITmfStateProvider getNewInstance() {
        return new OvsUpcallsStateProvider(this.getTrace(), this.fLayout, "OpenvSwitch Upcalls Analysis"); //$NON-NLS-1$
    }

    /**
     * @param id : ID of handler thread spwan by ovs-vswitchd
     * @return an instance of HandlerThreadModel
     */
    public HandlerThreadModel getHandler(Long id) {
        HandlerThreadModel handler = fHandlers.get(id);
        if(handler == null) {
            handler = new HandlerThreadModel(id, checkNotNull(getStateSystemBuilder()));
            fHandlers.put(id, handler);
        }
        return handler;
    }

    /**
     * @param id : ID of handler thread spwan by ovs-vswitchd
     * @return an instance of HandlerThreadModel
     */
    public @Nullable HandlerThreadModel getHandlerByUpcallId(int id) {

        for(HandlerThreadModel handler : fHandlers.values()) {
            if(handler.getUpcallById(id) != null) {
              return handler;
            }
        }
        return null;
    }

    /**
     * Get a port
     * @param id : ID of the port though which the upcall was sent
     * @return a port if it already exists or a new instance of VirtualPortModel
     */
    public VirtualPortModel getPortById(Integer id, String inPortName) {
        VirtualPortModel port = fPorts.get(id);
        if(port == null) {
            port = new VirtualPortModel(id, inPortName, checkNotNull(getStateSystemBuilder()));
            fPorts.put(id, port);
        }
        return port;
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
                Activator.getInstance().logError("Exception while building OVS state system", e); //$NON-NLS-1$
            }
        }

    }

    /**
    *
    */
   public void updateWaitingUpcallsNumber(long ts, boolean increment) {
       if(increment == true) {
           nWaitingUpcalls ++;
       } else {
           nWaitingUpcalls --;
       }

       /* Decrement the number of waiting upcalls*/
       ITmfStateSystemBuilder ss = getStateSystemBuilder();
       if(ss != null) {
           int nWaitingUpcallsQuark = ss.getQuarkAbsoluteAndAdd(IOpenVSwitchModelAttributes.VPORTS, IOpenVSwitchModelAttributes.N_WAITING_UPCALLS);
           ss.modifyAttribute(ts, nWaitingUpcalls, nWaitingUpcallsQuark);
       }
   }


}
