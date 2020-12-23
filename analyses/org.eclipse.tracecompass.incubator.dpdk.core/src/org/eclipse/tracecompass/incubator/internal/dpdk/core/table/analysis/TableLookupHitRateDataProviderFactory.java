package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.tmf.core.model.DataProviderDescriptor;

import org.eclipse.tracecompass.internal.tmf.core.model.xy.TmfTreeXYCompositeDataProvider;
import org.eclipse.tracecompass.tmf.core.dataprovider.IDataProviderDescriptor;
import org.eclipse.tracecompass.tmf.core.dataprovider.IDataProviderFactory;
import org.eclipse.tracecompass.tmf.core.dataprovider.IDataProviderDescriptor.ProviderType;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceManager;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;

/**
 * LPM Hit/Miss Ratio DataProvider Factory
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings({"restriction", "nls"})
public class TableLookupHitRateDataProviderFactory implements IDataProviderFactory {

    private static final IDataProviderDescriptor DESCRIPTOR = new DataProviderDescriptor.Builder()
            .setId(TableLookupHitRateDataProvider.ID)
            .setName("Table lookup success rate data provider")
            .setDescription("ACL/HASH Table lookup success rate data provider")
            .setProviderType(ProviderType.TREE_TIME_XY)
            .build();

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (traces.size() == 1) {
            return TableLookupHitRateDataProvider.create(trace);
        }
        return TmfTreeXYCompositeDataProvider.create(traces, TableLookupHitRateDataProvider.PROVIDER_TITLE, TableLookupHitRateDataProvider.ID);
    }

    @Override
    public Collection<IDataProviderDescriptor> getDescriptors(@NonNull ITmfTrace trace) {
        DpdkTableAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkTableAnalysisModule.class, DpdkTableAnalysisModule.ID);
        return module != null ? Collections.singletonList(DESCRIPTOR) : Collections.emptyList();
    }
}
