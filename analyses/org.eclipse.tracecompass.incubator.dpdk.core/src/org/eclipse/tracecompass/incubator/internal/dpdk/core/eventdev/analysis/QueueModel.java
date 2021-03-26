package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class QueueModel {

    private final int fQuark;
    private final ITmfStateSystemBuilder fSs;

    private final int fId;
    private final QueueScheduleType fScheduleType;
    private final int fPriority;
    private long rx = 0;
    private long tx = 0;

    private final Map<@NonNull Integer /*port id*/, @NonNull PortStatModel /*port stats*/> fAttachedPorts = new HashMap<>();


    public QueueModel(int queueId, int scheduleType, int priority, int devQuark, @NonNull ITmfStateSystemBuilder ss) {
        this.fId = queueId;
        this.fScheduleType = QueueScheduleType.fromInt(scheduleType);
        this.fPriority = priority;
        this.fSs = ss;

        int queueSetQuark = fSs.getQuarkRelativeAndAdd(devQuark, IDpdkEventDevModelAttributes.QUEUES);
        this.fQuark = fSs.getQuarkRelativeAndAdd(queueSetQuark, String.valueOf(this.fId));

        int schedTypeQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.SCHEDULE_TYPE);
        fSs.modifyAttribute(0, this.fScheduleType.toString(), schedTypeQuark);

        int priorityQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.PRIORITY);
        fSs.modifyAttribute(0, this.fPriority, priorityQuark);

        int rxQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_RX);
        fSs.modifyAttribute(0, this.rx, rxQuark);

        int txQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_TX);
        fSs.modifyAttribute(0, this.tx, txQuark);
    }

    public int getId() {
        return this.fId;
    }

    public QueueScheduleType getScheduleType() {
        return this.fScheduleType;
    }

    public int getPriority() {
        return this.fPriority;
    }

    public void attachPort(int portId, int priority) {
        PortStatModel portStat = fAttachedPorts.get(portId);
        if(portStat == null) {
            portStat = new PortStatModel(portId, priority, this.fQuark, this.fSs);
            fAttachedPorts.put(portId, portStat);
        }
    }

    public void transferToPort(Integer portId, Integer flowId, long ts) {

        this.tx += 1;

        int txQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkEventDevModelAttributes.EVENT_TX);
        fSs.modifyAttribute(ts, this.tx, txQuark);

        PortStatModel portStat = fAttachedPorts.get(portId);
        if(portStat != null) {
            portStat.sendEvent(flowId, ts);
        }
    }
}
