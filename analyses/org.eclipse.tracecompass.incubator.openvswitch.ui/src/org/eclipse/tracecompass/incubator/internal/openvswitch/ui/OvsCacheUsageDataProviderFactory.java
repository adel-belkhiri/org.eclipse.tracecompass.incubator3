package org.eclipse.tracecompass.incubator.internal.openvswitch.ui;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsCacheUsageDataProvider;
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
public class OvsCacheUsageDataProviderFactory implements IDataProviderFactory {

    public OvsCacheUsageDataProviderFactory() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        //Collection<ITmfTrace> traces = TmfTraceManager.getTraceSet(trace);
        if (trace instanceof TmfExperiment) {
            return OvsCacheUsageDataProvider.create(trace);
        }
        //return TmfTreeXYCompositeDataProvider.create(traces, OvsCacheUsageDataProvider.PROVIDER_TITLE, OvsCacheUsageDataProvider.ID);
        return null;
    }

}
