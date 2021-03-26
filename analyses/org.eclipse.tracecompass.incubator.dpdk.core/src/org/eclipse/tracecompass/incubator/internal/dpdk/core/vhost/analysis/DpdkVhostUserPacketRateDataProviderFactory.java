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
public class DpdkVhostUserPacketRateDataProviderFactory implements IDataProviderFactory {

    /**
     * Constructor
     */
    public DpdkVhostUserPacketRateDataProviderFactory() {
    }

    private static final IDataProviderDescriptor DESCRIPTOR = new DataProviderDescriptor.Builder()
            .setId(VhostPacketRateDataProvider.ID)
            .setName("Vhost Packet Rate Data Provider") //$NON-NLS-1$
            .setDescription("Vhost packet rate data provider") //$NON-NLS-1$
            .setProviderType(ProviderType.TREE_TIME_XY)
            .build();

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (traces.size() == 1) {
            return VhostPacketRateDataProvider.create(trace);
        }
        return TmfTreeXYCompositeDataProvider.create(traces, VhostPacketRateDataProvider.PROVIDER_TITLE, VhostPacketRateDataProvider.ID);
    }

    @Override
    public Collection<IDataProviderDescriptor> getDescriptors(@NonNull ITmfTrace trace) {
        DpdkVhostAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkVhostAnalysisModule.class, DpdkVhostAnalysisModule.ID);
        return module != null ? Collections.singletonList(DESCRIPTOR) : Collections.emptyList();
    }
}
