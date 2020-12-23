package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.DpdkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.DpdkPipelineAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RteEthdevConfigureEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineConnectInputPortToTableEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineForwardToNextTableEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineFreeEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineInputPortActionHandlerDropEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineInputPortCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineInputPortDisableEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineInputPortEnableEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineOutputPortActionHandlerDropEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineOutputPortCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineSetNextTableIDEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineTableCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineTableDefaultActionEntryAddEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePipelineTableDropPacketsEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortEthdevReaderCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortEthdevReaderRxEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortEthdevWriterCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortEthdevWriterTxEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortRingReaderCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortRingReaderRxEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortRingWriterCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortRingWriterTxEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortSinkCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortSinkTxEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortSourceCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RtePortSourceRxEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RteTableAclCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RteTableArrayCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.eventhandlers.RteTableLpmCreateEventHandler;
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
public class DpdkPipelineStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, DpdkEventHandler> fEventNames;

    /* Events layout */
    private final DpdkPipelineAnalysisEventLayout fLayout;

    private final Map<@NonNull Integer, PipelineModel> fPipelines = new HashMap<>();
    private final Map<@NonNull Integer, PortModel> fPorts = new HashMap<>();
    private final Map<@NonNull Integer /*port id*/, PipelineModel> fMapPortToPipeline = new HashMap<>();
    private final Map<@NonNull Integer, GenericTableModel> fTables = new HashMap<>();
    private final Map<@NonNull Integer, NetworkDeviceModel> fDevices = new HashMap<>();




    /**
     * @param trace trace
     * @param layout layout
     * @param id id
     */
    protected DpdkPipelineStateProvider(@NonNull TmfTrace trace, DpdkPipelineAnalysisEventLayout layout,
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
        throw new IllegalStateException("Dpdk Pipeline Analysis : Incompatible trace type");
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new DpdkPipelineStateProvider(this.getTrace(), this.fLayout, "Dpdk Pipeline Analysis");
    }


    /**
     * buildEventNames() : Map the events required for the analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, DpdkEventHandler> buildEventNames(DpdkPipelineAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, DpdkEventHandler> builder = ImmutableMap.builder();

         /* Classifier events */
         builder.put(layout.eventRtePipelineCreate(), new RtePipelineCreateEventHandler(layout, this));
         builder.put(layout.eventRtePipelineFree(), new RtePipelineFreeEventHandler(layout, this));
         builder.put(layout.eventRtePipelineInputPortCreate(), new RtePipelineInputPortCreateEventHandler(layout, this));
         builder.put(layout.eventRtePipelinePortOutCreate(), new RtePipelineOutputPortCreateEventHandler(layout, this));
         builder.put(layout.eventRtePipelineInputPortEnable(), new RtePipelineInputPortEnableEventHandler(layout, this));
         builder.put(layout.eventRtePipelineInputPortDisable(), new RtePipelineInputPortDisableEventHandler(layout, this));
         builder.put(layout.eventRtePipelineTableDefaultEntryAdd(),new RtePipelineTableDefaultActionEntryAddEventHandler(layout, this));
         builder.put(layout.eventRtePipelineSetNextTableID(),new RtePipelineSetNextTableIDEventHandler(layout, this));
         builder.put(layout.eventRtePipelineForwardToNextTable(),new RtePipelineForwardToNextTableEventHandler(layout, this));

         builder.put(layout.eventRtePipelineInputPortActionHandlerDrop(),new RtePipelineInputPortActionHandlerDropEventHandler(layout, this));
         builder.put(layout.eventRtePipelineOutputPortActionHandlerDrop(),new RtePipelineOutputPortActionHandlerDropEventHandler(layout, this));

         /* One class to handle 4 different events */
         builder.put(layout.eventRtePipelineTableDropByLookupHit(),new RtePipelineTableDropPacketsEventHandler(layout, this));
         builder.put(layout.eventRtePipelineTableDropByLookupMiss(),new RtePipelineTableDropPacketsEventHandler(layout, this));
         builder.put(layout.eventRtePipelineTableDropByLookupHitAH(),new RtePipelineTableDropPacketsEventHandler(layout, this));
         builder.put(layout.eventRtePipelineTableDropByLookupMissAH(),new RtePipelineTableDropPacketsEventHandler(layout, this));

         builder.put(layout.eventRtePortRingReaderCreate(), new RtePortRingReaderCreateEventHandler(layout, this));
         builder.put(layout.eventRtePortRingWriterCreate(), new RtePortRingWriterCreateEventHandler(layout, this));
         builder.put(layout.eventRtePortRingReaderRx(), new RtePortRingReaderRxEventHandler(layout, this));
         builder.put(layout.eventRtePortRingWriterTx(), new RtePortRingWriterTxEventHandler(layout, this));

         builder.put(layout.eventRtePortSinkCreate(), new RtePortSinkCreateEventHandler(layout, this));
         builder.put(layout.eventRtePortSinkTx(), new RtePortSinkTxEventHandler(layout, this));
         builder.put(layout.eventRtePortSourceCreate(), new RtePortSourceCreateEventHandler(layout, this));
         builder.put(layout.eventRtePortSourceRx(), new RtePortSourceRxEventHandler(layout, this));

         builder.put(layout.eventRteEthDevConfigure(), new RteEthdevConfigureEventHandler(layout, this));
         builder.put(layout.eventRtePortEthdevReaderCreate(), new RtePortEthdevReaderCreateEventHandler(layout, this));
         builder.put(layout.eventRtePortEthdevWriterCreate(), new RtePortEthdevWriterCreateEventHandler(layout, this));
         builder.put(layout.eventRtePortEthdevReaderRx(), new RtePortEthdevReaderRxEventHandler(layout, this));
         builder.put(layout.eventRtePortEthdevWriterTx(), new RtePortEthdevWriterTxEventHandler(layout, this));

         builder.put(layout.eventRtePipelineTableCreate(), new RtePipelineTableCreateEventHandler(layout, this));
         builder.put(layout.eventRteTableAclCreate(), new RteTableAclCreateEventHandler(layout, this));
         builder.put(layout.eventRteTableLpmCreate(), new RteTableLpmCreateEventHandler(layout, this));
         builder.put(layout.eventRteTableArrayCreate(), new RteTableArrayCreateEventHandler(layout, this));
         builder.put(layout.eventRtePipelinePortInConnectToTable(), new RtePipelineConnectInputPortToTableEventHandler(layout, this));


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
                Activator.getInstance().logError("Exception while building DPDK Pipeline State System", e);
            }
        }
    }

    /**
     * @param pipeName
     *          Pipeline name
     * @param id
     *          Pipeline Identifier
     * @return
     *          PipelineModel instance
     */
    public @NonNull PipelineModel createPipeline(@NonNull String pipeName, @NonNull Integer id) {
        PipelineModel pipeline = fPipelines.get(id);
        if(pipeline == null) {
            pipeline = new PipelineModel(pipeName,
                    NonNullUtils.checkNotNull(getStateSystemBuilder()));
            fPipelines.put(id, pipeline);
        }
        return pipeline;
    }

    /**
     * @param pipeName
     *          Pipeline name
     * @param id
     *          Pipeline Identifier
     * @return
     *          PipelineModel instance
     */
    public PipelineModel getPipeline(@NonNull Integer id) {
        return fPipelines.get(id);
    }

    /**
     * @param pipeId
     *          Pipeline identifier
     * @param tblName
     *          Table name
     * @param tblId
     *          Table Identifier
     */
    public void addTableToPipeline(int pipeId, int tblId, int tblIndex, long ts) {
        GenericTableModel table = fTables.get(tblId);
        if(table != null) {
            PipelineModel pipeline = fPipelines.get(pipeId);
            if(pipeline != null) {
                pipeline.addTable(table.getName(), tblId, tblIndex, table.getType(), ts);
            }
        }
    }

    /**
     * @param pipeId
     *          Pipeline identifier
     * @param tblName
     *          Table name
     * @param tblId
     *          Table Identifier
     */
    public void addTable(String tblName, int tblId, DpdkTableTypeEnum type) {
        GenericTableModel table = fTables.get(tblId);
        if(table == null) {
            table = new GenericTableModel(tblName, tblId, type);
            fTables.put(tblId, table);
        }
    }

    /**
     * @param pipeId xx
     * @param tableId xx
     * @param ts xx
     */
    public void removeTableFromPipeline(@NonNull Integer pipeId, Integer tableId, long ts) {
        PipelineModel pipeline = fPipelines.get(pipeId);
        if(pipeline != null) {
            pipeline.deleteTable(tableId, ts);
        }
    }


    /**
     * Search for a pipeline table using its id
     * @param pipeId
     *          Pipeline id
     * @param tableId
     *          Table identifier
     * @return
     *          PipelineTableModel object
     */
    public PipelineTableModel getTable(@Nullable Integer pipeId, Integer tableId) {
        PipelineTableModel table = null;
        if(pipeId != null) {
            PipelineModel pipeline = fPipelines.get(pipeId);
            if(pipeline != null) {
                table = pipeline.getTable(tableId);
            }
            return table;
        }

        for(PipelineModel pipeline : fPipelines.values()) {
            table = pipeline.getTable(tableId);
            if(table != null) {
                break;
            }
        }
        return table;
    }

    /**
     * Delete a pipeline
     * @param pipeId
                    Pipeline identifier
     * @param ts
     *              Timestamp
     */
    public void deletePipeline(@NonNull Integer pipeId, long ts) {
        PipelineModel pipeline = fPipelines.get(pipeId);
        if(pipeline != null) {
            pipeline.deleteAllTable(ts);
            fPipelines.remove(pipeId);
        }
    }

    /**
     * @param name
     * @param id
     * @param nbRxq
     * @param nbTxq
     */
    public NetworkDeviceModel addEthernetDevice(String name, int id, int nbRxq, int nbTxq) {
        NetworkDeviceModel dev = fDevices.get(id);
        if(dev == null) {
            dev = new NetworkDeviceModel(name, id, nbRxq, nbTxq);
            fDevices.put(id, dev);
        }
        return dev;
    }

    /**
     * @param id
     * @return
     */
    public NetworkDeviceModel getEthernetDevice(int id) {
        return fDevices.get(id);
    }
    /**
     * @param name
     * @param id
     * @param queueSize
     */
    public PortModel addPort(String name, int id, PortTypeEnum type, int queueSize) {
        PortModel port = fPorts.get(id);
        if(port == null) {
            port = new PortModel(id, name, type, queueSize);
            fPorts.put(id, port);
        }
        return port;
    }

    /**
     * Add an input port to a pipeline
     *
     * @param pipeId
     * @param index
     * @param portId
     * @param burstSize
     * @param ts
     * @return
     */
    public boolean addInputPortToPipeline(int pipeId, int index, int portId, int burstSize, long ts) {
        PipelineModel pipeline = fPipelines.get(pipeId);
        PortModel port = fPorts.get(portId);
        if(pipeline != null) {
            if(port != null) {
                pipeline.addInputPort(portId, index, port.getName(), port.getType(), port.getCapacity(), burstSize, ts);
                fPorts.remove(portId);
            }
            else {
                pipeline.addInputPort(portId, index, "no_name", PortTypeEnum.UNKNOWN, 0, burstSize, ts);
            }

            fMapPortToPipeline.put(portId, pipeline);
            return true;
        }
        return false;
    }

    /**
     * Add an output port to a pipeline
     *
     * @param pipeId
     * @param portId
     * @param burstSize
     * @return
     */
    public boolean addOutputPortToPipeline(int pipeId, int index, int portId, long ts) {
        PipelineModel pipeline = fPipelines.get(pipeId);
        PortModel port = fPorts.get(portId);
        if(pipeline != null) {
            if(port != null) {
                pipeline.addOutputPort(portId, index, port.getName(), port.getType(), port.getCapacity(), ts);
                fPorts.remove(portId);
            }
            else {
                pipeline.addOutputPort(portId, index, "no_name", PortTypeEnum.UNKNOWN, 0, ts);
            }

            fMapPortToPipeline.put(portId, pipeline);
            return true;
        }
        return false;
    }

    /**
     * @param portId
     * @return
     */
    public @Nullable PipelineModel searchPipelineByPortID(int portId) {
        return fMapPortToPipeline.get(portId);
    }
}
