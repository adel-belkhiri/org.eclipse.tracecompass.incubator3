package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.DpdkCoreStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis.ServiceModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class DpdkServiceComponentRegisterEventHandler extends DpdkEventHandler {

    DpdkCoreStateProvider fCoreStateProvier;


    /**
     * @param layout :
     * @param stateProvider :
     */
    public DpdkServiceComponentRegisterEventHandler(@NonNull DpdkAnalysisEventLayout layout, DpdkCoreStateProvider stateProvider) {
        super(layout);
        this.fCoreStateProvier = stateProvider;
    }

    //id=2, service_name=service_3, cb=0x5626062aced5, arg=0x0

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        Integer serviceId = content.getFieldValue(Integer.class, layout.fieldServiceId());
        String serviceName = content.getFieldValue(String.class, layout.fieldServiceName());
        Integer cb = content.getFieldValue(Integer.class, layout.fieldCallbackFunction());
        Integer arg = content.getFieldValue(Integer.class, layout.fieldArg());

        long ts = event.getTimestamp().getValue();


        if (serviceId == null || serviceName == null || cb == null || arg == null) {
            throw new IllegalArgumentException(layout.eventServiceComponentRegister() + " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        ServiceModel service = new ServiceModel(serviceId, serviceName, cb);
        service.setRegistrationTimestamp(ts);

        fCoreStateProvier.registerService(service);
    }
}
