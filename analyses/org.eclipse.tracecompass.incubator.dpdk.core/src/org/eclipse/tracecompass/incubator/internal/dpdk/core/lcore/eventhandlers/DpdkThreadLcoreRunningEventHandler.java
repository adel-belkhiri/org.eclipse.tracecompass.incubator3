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
public class DpdkThreadLcoreRunningEventHandler extends DpdkEventHandler {

    DpdkCoreStateProvider fCoreStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public DpdkThreadLcoreRunningEventHandler(@NonNull DpdkAnalysisEventLayout layout, DpdkCoreStateProvider stateProvider) {
        super(layout);
        this.fCoreStateProvier = stateProvider;
    }

    //lcore_id=1, f=0x7fe3a831569b, arg=0x0

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer coreId = content.getFieldValue(Integer.class, layout.fieldLcoreId());
        Long funcPointer = content.getFieldValue(Long.class, layout.fieldFunction());
        String  argPointer = content.getFieldValue(String.class, layout.fieldArg());
        long ts = event.getTimestamp().getValue();


        if (coreId == null || funcPointer == null || argPointer == null) {
            throw new IllegalArgumentException(layout.eventThreadLcoreRunning() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LogicalCoreModel core = fCoreStateProvier.getCore(coreId);
        core.setStatus(ts, LogicalCoreStatus.RUNNING, funcPointer);
    }
}
