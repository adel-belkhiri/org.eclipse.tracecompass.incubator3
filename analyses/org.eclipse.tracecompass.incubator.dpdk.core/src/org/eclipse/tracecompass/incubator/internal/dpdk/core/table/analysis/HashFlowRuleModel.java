package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis;

import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowRuleModel;

@SuppressWarnings({"javadoc"})
public class HashFlowRuleModel extends FlowRuleModel {

    private long fKey;

    public HashFlowRuleModel(int ruleId, long key) {
        super(ruleId);
        this.fKey = key;
    }

    @Override
    public String toString() {
        return String.valueOf(this.fKey);
    }

}
