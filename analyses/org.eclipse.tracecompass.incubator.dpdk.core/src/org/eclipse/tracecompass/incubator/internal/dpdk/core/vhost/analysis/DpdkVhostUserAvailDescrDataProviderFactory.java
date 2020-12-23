package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis;

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
public class DpdkVhostUserAvailDescrDataProviderFactory implements IDataProviderFactory {

    /**
     * Constructor
     */
    public DpdkVhostUserAvailDescrDataProviderFactory() {
    }

    private static final IDataProviderDescriptor DESCRIPTOR = new DataProviderDescriptor.Builder()
            .setId(VhostAvailableDescriptorsDataProvider.ID)
            .setName("Vhost Queues Available Descriptors Data Provider") //$NON-NLS-1$
            .setDescription("vhost queues available descriptors data provider") //$NON-NLS-1$
            .setProviderType(ProviderType.TREE_TIME_XY)
            .build();

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (traces.size() == 1) {
            return VhostAvailableDescriptorsDataProvider.create(trace);
        }
        return TmfTreeXYCompositeDataProvider.create(traces, VhostAvailableDescriptorsDataProvider.PROVIDER_TITLE, VhostAvailableDescriptorsDataProvider.ID);
    }

    @Override
    public Collection<IDataProviderDescriptor> getDescriptors(@NonNull ITmfTrace trace) {
        DpdkVhostAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkVhostAnalysisModule.class, DpdkVhostAnalysisModule.ID);
        return module != null ? Collections.singletonList(DESCRIPTOR) : Collections.emptyList();
    }
}
