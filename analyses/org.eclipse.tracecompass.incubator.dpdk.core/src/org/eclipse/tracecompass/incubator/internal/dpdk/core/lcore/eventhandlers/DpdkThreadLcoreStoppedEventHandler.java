package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.DpdkCoreStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreStatus;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class DpdkThreadLcoreStoppedEventHandler extends DpdkEventHandler {

    DpdkCoreStateProvider fCoreStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public DpdkThreadLcoreStoppedEventHandler(@NonNull DpdkAnalysisEventLayout layout, DpdkCoreStateProvider stateProvider) {
        super(layout);
        this.fCoreStateProvier = stateProvider;
    }

    //lcore_id=1, ret=0, state=2

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer coreId = content.getFieldValue(Integer.class, layout.fieldLcoreId());
        Integer status = content.getFieldValue(Integer.class, layout.fieldCoreStatus());
        Integer ret = content.getFieldValue(Integer.class, layout.fieldCoreRet());

        long ts = event.getTimestamp().getValue();


        if (coreId == null || status == null || ret == null) {
            throw new IllegalArgumentException(layout.eventThreadLcoreStopped() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LogicalCoreModel core = fCoreStateProvier.getCore(coreId);
        core.setStatus(ts, LogicalCoreStatus.IDLE, 0);
    }
}
