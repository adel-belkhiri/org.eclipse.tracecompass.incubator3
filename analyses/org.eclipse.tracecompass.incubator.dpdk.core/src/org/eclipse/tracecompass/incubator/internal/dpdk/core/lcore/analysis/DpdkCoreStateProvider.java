package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkLcoreRoleChangeEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkServiceComponentRegisterEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkServiceLcoreReadyEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkServiceLcoreStopEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkServiceMapCoreEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkServiceRunEndEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkServiceRunStartEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkThreadLcoreReadyEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkThreadLcoreRunningEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkThreadLcoreWaitingEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers.DpdkAnalysisEventLayout;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.statesystem.AbstractTmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTrace;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("nls")
public class DpdkCoreStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, DpdkEventHandler> fEventNames;

    /* Events layout */
    private final DpdkAnalysisEventLayout fLayout;

    private final Map<Integer, LogicalCoreModel> fCores = new HashMap<>();
    private final Map<Integer, ServiceModel> fServices = new HashMap<>();

    /**
     * @param trace trace
     * @param layout layout
     * @param id id
     */
    protected DpdkCoreStateProvider(TmfTrace trace, DpdkAnalysisEventLayout layout, String id) {
        super(trace, id);
        fLayout = layout;
        fEventNames = buildEventNames(layout);
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
    public TmfTrace getTrace() {
        ITmfTrace trace = super.getTrace();
        if (trace instanceof TmfTrace) {
            return (TmfTrace)trace;
        }
        throw new IllegalStateException("Dpdk Cores Analysis : Incompatible trace type");
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new DpdkCoreStateProvider(this.getTrace(), this.fLayout, "Dpdk Cores Analysis");
    }


    /**
     * buildEventNames() : Map the events needed for this analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, DpdkEventHandler> buildEventNames(DpdkAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, DpdkEventHandler> builder = ImmutableMap.builder();

         /* OpenvSwitch Events */
         builder.put(layout.eventLcoreRoleChange(),  new DpdkLcoreRoleChangeEventHandler(layout, this));
         builder.put(layout.eventThreadLcoreReady(),  new DpdkThreadLcoreReadyEventHandler(layout, this));
         builder.put(layout.eventThreadLcoreRunning(),  new DpdkThreadLcoreRunningEventHandler(layout, this));
         builder.put(layout.eventThreadLcoreWaiting(),  new DpdkThreadLcoreWaitingEventHandler(layout, this));

         builder.put(layout.eventServiceComponentRegister(),  new DpdkServiceComponentRegisterEventHandler(layout, this));
         builder.put(layout.eventServiceMapLcore(),  new DpdkServiceMapCoreEventHandler(layout, this));
         builder.put(layout.eventServiceRunBegin(),  new DpdkServiceRunStartEventHandler(layout, this));
         builder.put(layout.eventServiceRunEnd(),  new DpdkServiceRunEndEventHandler(layout, this));
         builder.put(layout.eventServiceLcoreReady(),  new DpdkServiceLcoreReadyEventHandler(layout, this));
         builder.put(layout.eventServiceLcoreStop(), new DpdkServiceLcoreStopEventHandler(layout, this));
         return (builder.build());
    }


    /**
     * Dispatch required events to their handler while processing the trace.
     *
     * @param event : event being processed.
     *
     */
    @Override
    protected void eventHandle(ITmfEvent event) {

        String eventName = event.getName();

        final ITmfStateSystemBuilder ss = NonNullUtils.checkNotNull(getStateSystemBuilder());

        DpdkEventHandler eventHandler = fEventNames.get(eventName);
        if (eventHandler != null) {
            try {
                eventHandler.handleEvent(ss, event);
            }
            catch (AttributeNotFoundException e) {
                Activator.getInstance().logError("Exception while building DPDK lCores State System", e);
            }
        }
    }


    /**
     * @param id xx
     * @return xx
     */
    public LogicalCoreModel getCore(int id) {
        LogicalCoreModel core = fCores.get(id);
        if(core == null) {
            core = new LogicalCoreModel(id, NonNullUtils.checkNotNull(getStateSystemBuilder()));
            fCores.put(id, core);
        }

        return core;
    }


    /**
     * @param s xx
     */
    public void registerService(ServiceModel s) {
        if(!fServices.containsKey(s.getId())) {
            fServices.put(s.getId(), s);
        }
    }


    /**
     * @param id xx
     * @return xx
     */
    public @Nullable ServiceModel getService (int id) {
        if(fServices.containsKey(id)) {
            return fServices.get(id);
        }

        return null;
    }

}
