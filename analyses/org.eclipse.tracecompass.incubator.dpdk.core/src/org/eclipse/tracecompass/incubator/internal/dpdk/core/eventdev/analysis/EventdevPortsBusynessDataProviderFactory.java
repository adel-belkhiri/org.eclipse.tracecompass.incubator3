package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

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
 * Pipeline Ports Packets Rate DataProvider Factory
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("restriction")
public class EventdevPortsBusynessDataProviderFactory implements IDataProviderFactory {

    private static final IDataProviderDescriptor DESCRIPTOR = new DataProviderDescriptor.Builder()
            .setId(EventdevPortsBusynessDataProvider.ID)
            .setName("Eventdev ports busyness data provider") //$NON-NLS-1$
            .setDescription("Eventdev ports busyness data provider") //$NON-NLS-1$
            .setProviderType(ProviderType.TREE_TIME_XY)
            .build();

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (traces.size() == 1) {
            return EventdevPortsBusynessDataProvider.create(trace);
        }
        return TmfTreeXYCompositeDataProvider.create(traces, EventdevPortsBusynessDataProvider.PROVIDER_TITLE, EventdevPortsBusynessDataProvider.ID);
    }

    @Override
    public Collection<IDataProviderDescriptor> getDescriptors(@NonNull ITmfTrace trace) {
        DpdkEventDevAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkEventDevAnalysisModule.class, DpdkEventDevAnalysisModule.ID);
        return module != null ? Collections.singletonList(DESCRIPTOR) : Collections.emptyList();
    }
}
