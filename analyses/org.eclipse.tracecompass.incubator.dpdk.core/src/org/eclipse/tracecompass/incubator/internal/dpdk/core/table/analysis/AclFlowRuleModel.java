package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis;

import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowRuleModel;

import com.google.common.net.InetAddresses;

@SuppressWarnings({"javadoc"})
public class AclFlowRuleModel extends FlowRuleModel {

    private int fPriority;
    private long fMaskFieldArray[];
    private long fFieldArray[];
    private final String fStringValue;

    public AclFlowRuleModel(int id, int priority, long[] field, long[] maskField, long[] fieldType, long[] fieldSize) {
        super(id);
        this.fPriority = priority;
        this.fFieldArray = field;
        this.fMaskFieldArray = maskField;

        fStringValue = formatRule(fieldType, fieldSize);
    }

    @Override
    public String toString() {
        return this.fStringValue;
    }

    @SuppressWarnings({"nls"})
    public String formatRule(long[] fieldType, long[] fieldSize) {

        String s = "priority (" + String.valueOf(this.fPriority) + "), fields (";
        String fieldStr = "";

        for(int i = 0; i < fieldType.length; i++) {
            long val = this.fFieldArray[i] & this.fMaskFieldArray[i];

            if(fieldSize[i] == 4) {
                fieldStr = InetAddresses.fromInteger((int) val).getHostAddress() +
                "/" + maskToPrefixLength((int) this.fMaskFieldArray[i]);

            } else {
                fieldStr = String.valueOf(val);
            }

            s = s.concat(fieldStr) + ", ";
        }

        return s.substring(0, s.length() - 2) + ")";
    }
}