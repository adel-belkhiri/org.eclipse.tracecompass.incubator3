package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

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

    //private long nbRxEvt;
    //private long nbTxEvt;


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
}
