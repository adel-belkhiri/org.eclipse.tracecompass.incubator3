package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowTableModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.AclFlowTableModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.DpdkEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.DpdkTableAnalysisEventLayout;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableAclCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableAclEntryAddEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableAclEntryDeleteEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableAclFreeEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableAclLookupKeyEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableHashCuckooEntryAddEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableHashCreateEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableHashCuckooEntryDeleteEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableHashFreeEventHandler;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.RteTableHashLookupKeyEventHandler;
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
public class DpdkTableStateProvider extends AbstractTmfStateProvider {

    private static final int VERSION = 1;

    /* Map events needed for this analysis with their handler functions */
    private final Map<String, DpdkEventHandler> fEventNames;

    /* Events layout */
    private final DpdkTableAnalysisEventLayout fLayout;

    private final Map<@NonNull Integer, FlowTableModel> fTables = new HashMap<>();


    /**
     * @param trace trace
     * @param layout layout
     * @param id id
     */
    protected DpdkTableStateProvider(@NonNull TmfTrace trace, DpdkTableAnalysisEventLayout layout,
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
        throw new IllegalStateException("Dpdk Table Analysis : Incompatible trace type");
    }


    /**
     * Get a new instance
     */
    @Override
    public ITmfStateProvider getNewInstance() {
        return new DpdkTableStateProvider(this.getTrace(), this.fLayout, "Dpdk Table Analysis");
    }


    /**
     * buildEventNames() : Map the events required for the analysis with their handler functions
     * @param layout :
     *
     */
    private Map<String, DpdkEventHandler> buildEventNames(DpdkTableAnalysisEventLayout layout) {

        ImmutableMap.Builder<String, DpdkEventHandler> builder = ImmutableMap.builder();


         /* Hash events */
         builder.put(layout.eventRteTableHashCuckooCreate(),  new RteTableHashCreateEventHandler(layout, this));
         builder.put(layout.eventRteTableHashCuckooFree(),  new RteTableHashFreeEventHandler(layout, this));
         builder.put(layout.eventRteTableHashCuckooEntryAdd(),  new RteTableHashCuckooEntryAddEventHandler(layout, this));
         builder.put(layout.eventRteTableHashCuckooEntryDelete(),  new RteTableHashCuckooEntryDeleteEventHandler(layout, this));
         builder.put(layout.eventRteTableHashCuckooLookup(),  new RteTableHashLookupKeyEventHandler(layout, this));

         /* LPM events */
         builder.put(layout.eventRteTableAclCreate(),  new RteTableAclCreateEventHandler(layout, this));
         builder.put(layout.eventRteTableAclFree(),  new RteTableAclFreeEventHandler(layout, this));
         builder.put(layout.eventRteTableAclEntryAdd(),  new RteTableAclEntryAddEventHandler(layout, this));
         builder.put(layout.eventRteTableAclEntryDelete(),  new RteTableAclEntryDeleteEventHandler(layout, this));
         builder.put(layout.eventRteTableAclLookup(),  new RteTableAclLookupKeyEventHandler(layout, this));
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
                Activator.getInstance().logError("Exception while building DPDK Table State System", e);
            }
        }
    }

    /**
     * @param tableName
     * @param tableId
     * @param keyType
     */
    public void addTable(String tableName, int tableId, FlowRuleKeyType keyType, long[] fieldType, long[] fieldSize) {
        if(!fTables.containsKey(tableId)) {
            ITmfStateSystemBuilder ss = NonNullUtils.checkNotNull(getStateSystemBuilder());
            if(keyType == FlowRuleKeyType.Hash_KEY) {
                int hashTablesQuark = ss.getQuarkAbsoluteAndAdd(IDpdkTableModelAttributes.IDpdkModel_HASH_TAB);
                FlowTableModel table = new FlowTableModel(tableName, tableId, hashTablesQuark, ss);
                fTables.put(tableId, table);
            } else
                if(keyType == FlowRuleKeyType.ACL_KEY) {
                    int hashTablesQuark = ss.getQuarkAbsoluteAndAdd(IDpdkTableModelAttributes.IDpdkModel_ACL_TAB);
                    AclFlowTableModel table = new AclFlowTableModel(tableName, tableId, hashTablesQuark, fieldType, fieldSize, ss);
                    fTables.put(tableId, table);
            }

        }
    }

    /**
     * @param tableName
     * @param tableId
     * @param ts xx
     */
    public void deleteTable(int tableId, long ts) {
        FlowTableModel table = fTables.get(tableId);
        if(table != null) {
            ITmfStateSystemBuilder ss = NonNullUtils.checkNotNull(getStateSystemBuilder());
            int tabQuark = table.getQuark();
            ss.removeAttribute(ts, tabQuark);
            fTables.remove(tableId);
        }
    }

    /**
     * @param tableName
     * @param tableId
     * @return
     */
    public @Nullable FlowTableModel getTable(int tableId) {
        return (fTables.get(tableId));
    }
}
