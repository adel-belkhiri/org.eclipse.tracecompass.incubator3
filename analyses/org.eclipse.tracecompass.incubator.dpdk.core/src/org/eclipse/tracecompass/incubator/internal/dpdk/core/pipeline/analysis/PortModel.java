package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;


/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class PortModel {

    private final int fId;
    private final int fQueueSize;
    private final String fName;
    PortTypeEnum fType;


    public PortModel(int portId, String name, PortTypeEnum type, int queueSize) {
        this.fId = portId;
        this.fQueueSize = queueSize;
        this.fName = name;
        this.fType = type;
    }

    public int getId() {
        return this.fId;
    }

    public int getCapacity() {
        return this.fQueueSize;
    }

    public String getName() {
        return this.fName;
    }

    public PortTypeEnum getType() {
        return this.fType;
    }
}
