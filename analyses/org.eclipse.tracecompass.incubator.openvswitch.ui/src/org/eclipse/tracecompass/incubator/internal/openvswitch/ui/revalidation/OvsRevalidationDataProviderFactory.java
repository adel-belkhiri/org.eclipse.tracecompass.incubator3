package org.eclipse.tracecompass.incubator.internal.openvswitch.ui.revalidation;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsRevalidationDataProvider;
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
public class OvsRevalidationDataProviderFactory implements IDataProviderFactory {

    /**
     * Constructor
     */
    public OvsRevalidationDataProviderFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public @Nullable ITmfTreeDataProvider<? extends ITmfTreeDataModel> createProvider(@NonNull ITmfTrace trace) {
        if (trace instanceof TmfExperiment) {
            return OvsRevalidationDataProvider.create(trace);
        }
        return null;
    }

}
