package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis;

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
@SuppressWarnings("restriction")
public class ClassifierPerRuleLookupHitRateDataProviderFactory implements IDataProviderFactory {

    private static final IDataProviderDescriptor DESCRIPTOR = new DataProviderDescriptor.Builder()
            .setId(ClassifierPerRuleLookupHitRateDataProvider.ID)
            .setName("Classifier per-rule hit rate data provider") //$NON-NLS-1$
            .setDescription("Classifier per-rule hit rate data provider") //$NON-NLS-1$
            .setProviderType(ProviderType.TREE_TIME_XY)
            .build();

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (traces.size() == 1) {
            return ClassifierPerRuleLookupHitRateDataProvider.create(trace);
        }
        return TmfTreeXYCompositeDataProvider.create(traces, ClassifierPerRuleLookupHitRateDataProvider.PROVIDER_TITLE, ClassifierPerRuleLookupHitRateDataProvider.ID);
    }

    @Override
    public Collection<IDataProviderDescriptor> getDescriptors(@NonNull ITmfTrace trace) {
        DpdkClassifierAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkClassifierAnalysisModule.class, DpdkClassifierAnalysisModule.ID);
        return module != null ? Collections.singletonList(DESCRIPTOR) : Collections.emptyList();
    }
}
