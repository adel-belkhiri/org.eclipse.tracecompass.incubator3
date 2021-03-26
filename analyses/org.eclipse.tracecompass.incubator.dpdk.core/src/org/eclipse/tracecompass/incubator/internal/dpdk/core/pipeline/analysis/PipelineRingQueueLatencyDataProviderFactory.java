package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

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
 * @author adel
 *
 */
@SuppressWarnings("restriction")
public class PipelineRingQueueLatencyDataProviderFactory implements IDataProviderFactory {

    /**
     * Constructor
     */
    public PipelineRingQueueLatencyDataProviderFactory() {
    }

    private static final IDataProviderDescriptor DESCRIPTOR = new DataProviderDescriptor.Builder()
            .setId(PipelineRingQueueLatencyDataProvider.ID)
            .setName("Pipeline SW Queue Latency") //$NON-NLS-1$
            .setDescription("Pipeline SW Queue Latency") //$NON-NLS-1$
            .setProviderType(ProviderType.TREE_TIME_XY)
            .build();

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (traces.size() == 1) {
            return PipelineRingQueueLatencyDataProvider.create(trace);
        }
        return TmfTreeXYCompositeDataProvider.create(traces, PipelineRingQueueLatencyDataProvider.PROVIDER_TITLE, PipelineRingQueueLatencyDataProvider.ID);
    }

    @Override
    public Collection<IDataProviderDescriptor> getDescriptors(@NonNull ITmfTrace trace) {
        DpdkPipelineAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkPipelineAnalysisModule.class, DpdkPipelineAnalysisModule.ID);
        return module != null ? Collections.singletonList(DESCRIPTOR) : Collections.emptyList();
    }
}
