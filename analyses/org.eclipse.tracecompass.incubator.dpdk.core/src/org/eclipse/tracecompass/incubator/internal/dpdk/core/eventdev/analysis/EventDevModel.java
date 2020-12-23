package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
public class EventDevModel {
    private final String fName;
    private final int fQuark;

    private final int fDevId;
    private final int fBackend;
    private final int fServiceId;
    private final int fCreditQuanta;
    private final int fSchedQuanta;

    private int nbEventsLimit;

    private final Map<Integer, PortModel> fPorts = new HashMap<>();
    private final Map<Integer, QueueModel> fQueues = new HashMap<>();

    private final ITmfStateSystemBuilder fSs;



    /**
     * @param eventdevName
     *      Eventdev name
     * @param devId
     * @param backend
     * @param serviceId
     * @param creditQuanta
     * @param schedQuanta
     * @param ss
     *      State system builder
     */
    public EventDevModel(String eventdevName, int devId, int backend,int serviceId, int creditQuanta,
            int schedQuanta, @NonNull ITmfStateSystemBuilder ss) {
        this.fName = eventdevName;
        this.fDevId = devId;
        this.fServiceId = serviceId;
        this.fCreditQuanta = creditQuanta;
        this.fSchedQuanta = schedQuanta;
        this.fBackend = backend;
        this.nbEventsLimit = 0;

        /* Create the eventdev node in the ss */
        this.fSs = ss;
        this.fQuark = fSs.getQuarkAbsoluteAndAdd(this.fName);

        int devIdQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.DEV_ID);
        fSs.modifyAttribute(0, this.fDevId, devIdQuark);

        int creditQuantaQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.CREDIT_QUANTA);
        fSs.modifyAttribute(0, this.fCreditQuanta, creditQuantaQuark);

        int schedQuantaQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.SCHED_QUANTA);
        fSs.modifyAttribute(0, this.fSchedQuanta, schedQuantaQuark);

        int serviceIdQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.SERVICE_ID);
        fSs.modifyAttribute(0, this.fServiceId, serviceIdQuark);
    }


    public int getBackendId() {
        return this.fBackend;
    }

    public int getNbEventsLimit() {
        return this.nbEventsLimit;
    }

    public void setNbEventsLimit(int limit) {
        this.nbEventsLimit = limit;
    }

    /**
     * Add a port to EventDev device
     *
     * @param portId
     * @param newEventThreshold
     * @param enqueueDepth
     * @param dequeueDepth
     * @param ringRx
     * @param ringCq
     * @return
     */
    public PortModel addPort(int portId, int newEventThreshold, int enqueueDepth, int dequeueDepth,
            RingModel ringRx, RingModel ringCq) {
        PortModel port = fPorts.get(portId);

        if(port == null) {
            port = new PortModel(portId, newEventThreshold, enqueueDepth, dequeueDepth, ringRx, ringCq, this.fQuark, this.fSs);
            fPorts.put(portId, port);
        }

        return port;
    }


    /**
     * @param queueId
     * @param schedType
     * @param priority
     * @param index
     * @return
     */
    public QueueModel addQueue(Integer queueId, Integer schedType, Integer priority) {
        QueueModel queue = fQueues.get(queueId);

        if (queue == null) {
            queue = new QueueModel(queueId, schedType, priority, this.fQuark, this.fSs);
            fQueues.put(queueId, queue);
        }
        return queue;
    }

    /**
     * @param portId
     * @return
     */
    public PortModel getPort(int portId) {
        return fPorts.get(portId);
    }
}
