package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;


/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class PortModel {

    private final int fId;
    private final String fName;
    PortTypeEnum fType;


    public PortModel(int portId, String name, PortTypeEnum type) {
        this.fId = portId;
        this.fName = name;
        this.fType = type;
    }

    public int getId() {
        return this.fId;
    }

    public String getName() {
        return this.fName;
    }

    public PortTypeEnum getType() {
        return this.fType;
    }
}
