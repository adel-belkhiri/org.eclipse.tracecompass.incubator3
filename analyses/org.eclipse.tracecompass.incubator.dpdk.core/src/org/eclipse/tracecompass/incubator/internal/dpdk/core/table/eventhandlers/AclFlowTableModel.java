package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers;

import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowTableModel;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
public class AclFlowTableModel extends FlowTableModel {

    private final long[] fFieldType;
    private final long[] fFieldSize;
    /**
     * @param name
     * @param id
     * @param parentQuark
     * @param fieldType
     * @param fieldSize
     * @param ss
     */
    public AclFlowTableModel(String name, int id, int parentQuark, long[] fieldType, long[] fieldSize, ITmfStateSystemBuilder ss) {
        super(name, id, parentQuark, ss);
        this.fFieldType = fieldType;
        this.fFieldSize = fieldSize;
    }

    public long[] getFieldType() {
        return fFieldType;
    }
    public long[] getFieldSize() {
        return fFieldSize;
    }

}
