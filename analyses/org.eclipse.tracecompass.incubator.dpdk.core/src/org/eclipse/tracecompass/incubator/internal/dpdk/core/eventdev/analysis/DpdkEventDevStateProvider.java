package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.DpdkEventDevAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.DpdkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.RteEventDevConfigureEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.RteEventRingCreateEventHandler;
//import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.RtePortSetupEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.RteQueueSetupEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.SwEventDequeueBurstEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.SwEventEnqueueBurstEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.SwPortSetupEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers.SwProbeEventHandler;
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
public class DpdkEventDevStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, DpdkEventHandler> fEventNames;

    /* Events layout */
    private final DpdkEventDevAnalysisEventLayout fLayout;

    private final Map<@NonNull Integer, EventDevModel> fEventdevs = new HashMap<>();
    private final Map<@NonNull Integer, RingModel> fPortRings = new HashMap<>();




    /**
     * @param trace trace
     * @param layout layout
     * @param id id
     */
    protected DpdkEventDevStateProvider(@NonNull TmfTrace trace, DpdkEventDevAnalysisEventLayout layout,
            @NonNull String id) {
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
        throw new IllegalStateException("Dpdk EventDev Analysis : Incompatible trace type");
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new DpdkEventDevStateProvider(this.getTrace(), this.fLayout, "Dpdk Eventdev Analysis");
    }


    /**
     * buildEventNames() : Map the events required for the analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, DpdkEventHandler> buildEventNames(DpdkEventDevAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, DpdkEventHandler> builder = ImmutableMap.builder();

         /* Classifier events */
         builder.put(layout.eventSwProbe(), new SwProbeEventHandler(layout, this));
         builder.put(layout.eventRteEventDevConfigure(), new RteEventDevConfigureEventHandler(layout, this));
         builder.put(layout.eventRteEventRingCreate(), new RteEventRingCreateEventHandler(layout, this));
         builder.put(layout.eventSwPortSetup(), new SwPortSetupEventHandler(layout, this));
         builder.put(layout.eventRteQueueSetup(), new RteQueueSetupEventHandler(layout, this));

         builder.put(layout.eventSwEventEnqueueBurst(), new SwEventEnqueueBurstEventHandler(layout, this));
         builder.put(layout.eventSwEventDequeueBurst(), new SwEventDequeueBurstEventHandler(layout, this));

         return (builder.build());
    }


    /**
     * Dispatch required events to their handler while processing the trace.
     *
     * @param event : Event being processed.
     *
     */
    @Override
    protected void eventHandle(ITmfEvent event) {

        String eventName = event.getName();

        DpdkEventHandler eventHandler = fEventNames.get(eventName);
        if (eventHandler != null) {
            try {
                eventHandler.handleEvent(NonNullUtils.checkNotNull(getStateSystemBuilder()), event);
            }
            catch (AttributeNotFoundException e) {
                Activator.getInstance().logError("Exception while building DPDK EventDev State System", e);
            }
        }
    }

    /**
     * @param name
     * @param devId
     * @param backend
     * @param serviceId
     * @param creditQuanta
     * @param schedQuanta
     * @param pipeName
     *          Pipeline name
     * @param id
     *          Pipeline Identifier
     * @return
     *          PipelineModel instance
     */
    public @NonNull EventDevModel addEventDevice(@NonNull String name, int devId, int backend, int serviceId,
            int creditQuanta, int schedQuanta) {
        EventDevModel eventdev = fEventdevs.get(devId);
        if(eventdev == null) {
            eventdev = new EventDevModel(name, devId, backend, serviceId, creditQuanta, schedQuanta,
                    NonNullUtils.checkNotNull(getStateSystemBuilder()));
            fEventdevs.put(devId, eventdev);
        }
        return eventdev;
    }

    /**
     * @param pipeName
     *          Pipeline name
     * @param id
     *          Pipeline Identifier
     * @return
     *          PipelineModel instance
     */
    public EventDevModel getEventDevice(@NonNull Integer id) {
        return fEventdevs.get(id);
    }

    /**
     * @param backendId
     * @return
     *          EventDevModel instance
     */
    public @Nullable EventDevModel searchEventDevByBackendId(@NonNull Integer backendId) {
        for(EventDevModel dev : fEventdevs.values()) {
            if(dev.getBackendId() == backendId) {
                return dev;
            }
        }

        return null;
    }

    /**
     * @param ringId
     * @param name
     * @param capacity
     * @return
     */
    public RingModel addRing(Integer ringId, String name, Integer capacity) {
        RingModel ring = fPortRings.get(ringId);
        if(ring == null) {
            ring = new RingModel(ringId, name, capacity);
            fPortRings.put(ringId, ring);
        }
        return ring;
    }

    public RingModel getRingBuffer(@NonNull Integer id) {
        return fPortRings.get(id);
    }
}
