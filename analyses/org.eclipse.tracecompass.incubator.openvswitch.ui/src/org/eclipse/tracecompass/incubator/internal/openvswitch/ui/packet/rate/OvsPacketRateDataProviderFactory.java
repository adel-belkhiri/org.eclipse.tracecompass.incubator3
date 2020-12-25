package org.eclipse.tracecompass.incubator.internal.openvswitch.ui.packet.rate;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsPacketRateDataProvider;
import org.eclipse.tracecompass.tmf.core.dataprovider.IDataProviderFactory;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.ITmfTreeDataProvider;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.experiment.TmfExperiment;

/**
 * @author adel
 *
 */
@SuppressWarnings("restriction")
public class OvsPacketRateDataProviderFactory implements IDataProviderFactory {

    /**
     * Constructor
     */
    public OvsPacketRateDataProviderFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        //Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        //if (traces.size() == 1) {
        //    return OvsPacketRateDataProvider.create(trace);
        //}
        //return TmfTreeXYCompositeDataProvider.create(traces, OvsPacketRateDataProvider.PROVIDER_TITLE, OvsPacketRateDataProvider.ID);

        if (trace instanceof TmfExperiment) {
            return OvsPacketRateDataProvider.create(trace);
        }
        return null;
    }

}
