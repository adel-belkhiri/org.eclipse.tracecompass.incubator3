package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.DpdkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.DpdkLpmAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.RteLpmAddEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.RteLpmCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.RteLpmDeleteAllEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.RteLpmDeleteEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.RteLpmLookupBulkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.RteLpmLookupEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers.RteLpmLookupX4EventHandler;
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
public class DpdkLpmStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, DpdkEventHandler> fEventNames;

    /* Events layout */
    private final DpdkLpmAnalysisEventLayout fLayout;

    private final Map<String, LpmTableModel> fTables = new HashMap<>();


    /**
     * @param trace trace
     * @param layout layout
     * @param id id
     */
    protected DpdkLpmStateProvider(TmfTrace trace, DpdkLpmAnalysisEventLayout layout, String id) {
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
        throw new IllegalStateException("Dpdk LPM Analysis : Incompatible trace type");
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new DpdkLpmStateProvider(this.getTrace(), this.fLayout, "Dpdk LPM Analysis");
    }


    /**
     * buildEventNames() : Map the events needed for this analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, DpdkEventHandler> buildEventNames(DpdkLpmAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, DpdkEventHandler> builder = ImmutableMap.builder();

         /* OpenvSwitch Events */
         builder.put(layout.eventRteLpmCreate(),  new RteLpmCreateEventHandler(layout, this));
         builder.put(layout.eventRteLpmAdd(),  new RteLpmAddEventHandler(layout, this));
         builder.put(layout.eventRteLpmDelete(),  new RteLpmDeleteEventHandler(layout, this));
         builder.put(layout.eventRteLpmDeleteAll(),  new RteLpmDeleteAllEventHandler(layout, this));
         builder.put(layout.eventRteLpmLookup(), new RteLpmLookupEventHandler(layout, this));
         builder.put(layout.eventRteLpmLookupx4(), new RteLpmLookupX4EventHandler(layout, this));
         builder.put(layout.eventRteLpmLookupBulk(), new RteLpmLookupBulkEventHandler(layout, this));

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
                Activator.getInstance().logError("Exception while building DPDK LPM State System", e);
            }
        }
    }

    /**
     * @param name
     */
    public void addLpmTable(String name) {
        if(!fTables.containsKey(name)) {
            fTables.put(name, new LpmTableModel(name, NonNullUtils.checkNotNull(getStateSystemBuilder())));
        }
    }

    /**
     * @param name
     * @return
     */
    public @Nullable LpmTableModel getLpmTable(String name) {
        if(fTables.containsKey(name)) {
            return fTables.get(name);
        }
        return null;
    }
}
