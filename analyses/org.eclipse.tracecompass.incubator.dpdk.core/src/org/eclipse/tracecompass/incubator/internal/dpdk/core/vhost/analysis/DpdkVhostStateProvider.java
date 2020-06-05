package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.DequeueBurstEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.DpdkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.DpdkVhostAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.EnqueueBurstEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.EthernetDeviceVhostCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.QueueSetupEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.ReadFromRxRingEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.SetVringEnabledEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.VhostUserAddConnectionEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers.WriteToTxRingEventHandler;
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
public class DpdkVhostStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, DpdkEventHandler> fEventNames;

    /* Events layout */
    private final DpdkVhostAnalysisEventLayout fLayout;

    private final Map<String, NetworkDeviceModel> fDevices = new HashMap<>();


    /**
     * @param trace trace
     * @param layout layout
     * @param id id
     */
    protected DpdkVhostStateProvider(TmfTrace trace, DpdkVhostAnalysisEventLayout layout, String id) {
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
        throw new IllegalStateException("Dpdk Vhost Analysis : Incompatible trace type");
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new DpdkVhostStateProvider(this.getTrace(), this.fLayout, "Dpdk Vhost Analysis");
    }


    /**
     * buildEventNames() : Map the events needed for this analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, DpdkEventHandler> buildEventNames(DpdkVhostAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, DpdkEventHandler> builder = ImmutableMap.builder();

         /* OpenvSwitch Events */
         builder.put(layout.eventEthDevVhostCreate(),  new EthernetDeviceVhostCreateEventHandler(layout, this));
         builder.put(layout.eventVhostUserAddConnection(),  new VhostUserAddConnectionEventHandler(layout, this));
         builder.put(layout.eventQueueSetup(),  new QueueSetupEventHandler(layout, this));
         builder.put(layout.eventSetVringEnabled(),  new SetVringEnabledEventHandler(layout, this));
         builder.put(layout.eventReadFromRxRing(),  new ReadFromRxRingEventHandler(layout, this));
         builder.put(layout.eventDequeueBurst(),  new DequeueBurstEventHandler(layout, this));
         builder.put(layout.eventEnqueueBurst(),  new EnqueueBurstEventHandler(layout, this));
         builder.put(layout.eventWriteToTxRing(),  new WriteToTxRingEventHandler(layout, this));

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
                Activator.getInstance().logError("Exception while building DPDK Vhost State System", e);
            }
        }
    }


    /**
     * @param id Device Id
     * @param ifceName Interface Name
     * @param mac Mac address
     * @return NetworkDeviceModel
     */
    public NetworkDeviceModel addDevice(String devName, String ifceName, long[] mac) {
        NetworkDeviceModel dev = fDevices.get(devName);
        if(dev == null) {
            dev = new NetworkDeviceModel(devName, ifceName, mac, NonNullUtils.checkNotNull(getStateSystemBuilder()));
            fDevices.put(devName, dev);
        }
        return dev;
    }

    /**
     * @param devName
     * @return NetworkDeviceModel
     */
    public NetworkDeviceModel getDevice(String devName) {
        return fDevices.get(devName);
    }

    /**
     * @param vid The id of the attached device
     * @return the NetworkDeviceModel
     */
    public @Nullable NetworkDeviceModel searchDeviceByVid(int vid) {
        for(NetworkDeviceModel dev : fDevices.values()) {
            if(dev.isVidAttached(vid)) {
                return dev;
            }
        }

        return null;
    }

    /**
     * @param vid The id of the attached device
     * @return the NetworkDeviceModel
     */
    public @Nullable NetworkDeviceModel searchDeviceByIfaceName(String ifaceName) {
        for(NetworkDeviceModel dev : fDevices.values()) {
            if(dev.getIfceName().equals(ifaceName)) {
                return dev;
            }
        }

        return null;
    }
}
