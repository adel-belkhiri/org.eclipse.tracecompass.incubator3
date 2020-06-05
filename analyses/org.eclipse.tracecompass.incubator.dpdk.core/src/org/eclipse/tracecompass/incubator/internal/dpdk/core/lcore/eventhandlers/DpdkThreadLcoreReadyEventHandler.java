package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.DpdkCoreStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreRole;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.LogicalCoreStatus;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class DpdkThreadLcoreReadyEventHandler extends DpdkEventHandler {

    DpdkCoreStateProvider fCoreStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public DpdkThreadLcoreReadyEventHandler(@NonNull DpdkAnalysisEventLayout layout, DpdkCoreStateProvider stateProvider) {
        super(layout);
        this.fCoreStateProvier = stateProvider;
    }

    //{ lcore_id = 2, lcore_role = 0, cpuset = "2" }
    //lcore_id=3, lcore_role=0, cpuset=3

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer coreId = content.getFieldValue(Integer.class, layout.fieldLcoreId());
        Integer coreType = content.getFieldValue(Integer.class, layout.fieldCoreType());
        String  cpuSet = content.getFieldValue(String.class, layout.fieldCpuSet());
        long ts = event.getTimestamp().getValue();


        if (coreId == null || coreType == null || cpuSet == null) {
            throw new IllegalArgumentException(layout.eventThreadLcoreReady() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        LogicalCoreModel core = fCoreStateProvier.getCore(coreId);
        core.setRole(ts, LogicalCoreRole.LCORE_RTE);
        core.setStatus(ts, LogicalCoreStatus.IDLE, 0 /*function pointer*/);

        /* By default an lcore is a SLAVE until it is said otherwise */
        if(coreType == 1) {
            core.setAsMaster();
        }
    }
}
