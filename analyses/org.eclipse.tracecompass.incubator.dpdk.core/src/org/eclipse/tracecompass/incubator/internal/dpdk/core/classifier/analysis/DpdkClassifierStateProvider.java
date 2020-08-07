package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.DpdkClassifierAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.DpdkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.RteFlowClassifierCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.RteFlowClassifierFreeEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.RteFlowClassifierLookupEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.RteFlowClassifyTableCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.RteFlowClassifyTableEntryAddEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers.RteFlowClassifyTableEntryDeleteEventHandler;
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
public class DpdkClassifierStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, DpdkEventHandler> fEventNames;

    /* Events layout */
    private final DpdkClassifierAnalysisEventLayout fLayout;

    private final Map<@NonNull String, FlowClassifierModel> fClassifiers = new HashMap<>();


    /**
     * @param trace trace
     * @param layout layout
     * @param id id
     */
    protected DpdkClassifierStateProvider(@NonNull TmfTrace trace, DpdkClassifierAnalysisEventLayout layout,
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
        throw new IllegalStateException("Dpdk Classifier Analysis : Incompatible trace type");
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new DpdkClassifierStateProvider(this.getTrace(), this.fLayout, "Dpdk Classifier Analysis");
    }


    /**
     * buildEventNames() : Map the events required for the analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, DpdkEventHandler> buildEventNames(DpdkClassifierAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, DpdkEventHandler> builder = ImmutableMap.builder();

         /* Classifier events */
         builder.put(layout.eventRteFlowClassifierCreate(), new RteFlowClassifierCreateEventHandler(layout, this));
         builder.put(layout.eventRteFlowClassifyTableCreate(), new RteFlowClassifyTableCreateEventHandler(layout, this));
         builder.put(layout.eventRteFlowClassifyTableEntryAdd(), new RteFlowClassifyTableEntryAddEventHandler(layout, this));
         builder.put(layout.eventRteFlowClassifyTableEntryDelete(), new RteFlowClassifyTableEntryDeleteEventHandler(layout, this));
         builder.put(layout.eventRteFlowClassifyLookup(), new RteFlowClassifierLookupEventHandler(layout, this));
         builder.put(layout.eventRteFlowClassifierFree(), new RteFlowClassifierFreeEventHandler(layout, this));

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
                Activator.getInstance().logError("Exception while building DPDK Classifier State System", e);
            }
        }
    }

    /**
     * @param clsName
     *          Classifier name
     * @return
     */
    public @NonNull FlowClassifierModel getClassifier(@NonNull String clsName) {
        FlowClassifierModel classifier = fClassifiers.get(clsName);
        if(classifier == null) {
            classifier = new FlowClassifierModel(clsName,
                    NonNullUtils.checkNotNull(getStateSystemBuilder()));
            fClassifiers.put(clsName, classifier);
        }
        return classifier;
    }

    /**
     * @param clsName xx
     * @param table xx
     */
    public void addTableToClassifier(String clsName, String tblName, int tblId) {
        FlowClassifierModel classifier = getClassifier(clsName);
        classifier.addTable(tblName, tblId);
    }

    /**
     * @param clsName xx
     * @param tableId xx
     * @param ts xx
     */
    public void removeTableFromClassifier(String clsName, Integer tableId, long ts) {
        FlowClassifierModel classifier = fClassifiers.get(clsName);
        if(classifier != null) {
            classifier.deleteTable(tableId, ts);
        }
    }


    /**
     * @param clsName
     *                  Classifier name
     * @param tableId
     *                  Table identifier
     * @return a FlowTableModel object
     */
    public FlowTableModel getTable(@Nullable String clsName, Integer tableId) {
        FlowTableModel table = null;
        if(clsName != null) {
            FlowClassifierModel classifier = fClassifiers.get(clsName);
            if(classifier != null) {
                table = classifier.getTable(tableId);
            }
            return table;
        }

        for(FlowClassifierModel classifier : fClassifiers.values()) {
            table = classifier.getTable(tableId);
            if(table != null) {
                break;
            }

        }
        return table;
    }

    /**
     * Delete a classifier
     * @param clsName
                    Classifier name
     * @param ts
     *              Timestamp
     */
    public void deleteClassifier(String clsName, long ts) {
        FlowClassifierModel classifier = fClassifiers.get(clsName);
        if(classifier != null) {
            classifier.deleteAllTable(ts);
            fClassifiers.remove(clsName);
        }
    }
}
