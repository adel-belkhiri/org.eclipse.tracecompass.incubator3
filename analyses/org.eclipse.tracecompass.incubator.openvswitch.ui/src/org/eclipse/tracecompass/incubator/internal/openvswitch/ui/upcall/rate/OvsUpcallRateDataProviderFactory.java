package org.eclipse.tracecompass.incubator.internal.openvswitch.ui.upcall.rate;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsUpcallRateDataProvider;
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
public class OvsUpcallRateDataProviderFactory implements IDataProviderFactory {

    /**
     * Constructor
     */
    public OvsUpcallRateDataProviderFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        if (trace instanceof TmfExperiment) {
            return OvsUpcallRateDataProvider.create(trace);
        }
        return null;
    }

}
