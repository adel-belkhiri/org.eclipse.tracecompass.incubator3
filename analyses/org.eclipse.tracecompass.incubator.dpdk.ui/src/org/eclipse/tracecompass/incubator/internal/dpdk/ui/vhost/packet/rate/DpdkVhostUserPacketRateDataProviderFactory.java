package org.eclipse.tracecompass.incubator.internal.dpdk.ui.vhost.packet.rate;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.VhostPacketRateDataProvider;
import org.eclipse.tracecompass.internal.tmf.core.model.xy.TmfTreeXYCompositeDataProvider;
import org.eclipse.tracecompass.tmf.core.dataprovider.IDataProviderFactory;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceManager;

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

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (traces.size() == 1) {
            return VhostPacketRateDataProvider.create(trace);
        }
        return TmfTreeXYCompositeDataProvider.create(traces, VhostPacketRateDataProvider.PROVIDER_TITLE, VhostPacketRateDataProvider.ID);
    }

}