package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.DpdkClassifierStateProvider;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.FlowTableModel;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis.IPv4FiveTupleFlowRule;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class RteFlowClassifyTableEntryAddEventHandler extends DpdkEventHandler {

    /**
     * @param layout
     *      DpdkLpmAnalysisEventLayout
     * @param stateProvider
     *      DpdkLpmStateProvider
     */
    public RteFlowClassifyTableEntryAddEventHandler(@NonNull DpdkClassifierAnalysisEventLayout layout, DpdkClassifierStateProvider stateProvider) {
        super(layout, stateProvider);
    }

    @Override
    public void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException {
        DpdkClassifierAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        ITmfEventField content = event.getContent();
        long ts = event.getTimestamp().getValue();

        String clsName = content.getFieldValue(String.class, layout.fieldClsName());
        Integer tblId = content.getFieldValue(Integer.class, layout.fieldTblPointer());

        Integer ruleId = content.getFieldValue(Integer.class, layout.fieldRuleId());
        Integer entryPtr = content.getFieldValue(Integer.class, layout.fieldEntryPtr());
        Integer keyFound = content.getFieldValue(Integer.class, layout.fieldKeyFound());

        Integer dstIp = content.getFieldValue(Integer.class, layout.fieldDstIP());
        Integer dstIpMask = content.getFieldValue(Integer.class, layout.fieldDstIpMask());

        Integer srcIp = content.getFieldValue(Integer.class, layout.fieldSrcIP());
        Integer srcIpMask = content.getFieldValue(Integer.class, layout.fieldSrcIpMask());

        Integer dstPort = content.getFieldValue(Integer.class, layout.fieldDstPort());
        Integer dstPortMask = content.getFieldValue(Integer.class, layout.fieldDstPortMask());

        Integer srcPort = content.getFieldValue(Integer.class, layout.fieldSrcPort());
        Integer srcPortMask = content.getFieldValue(Integer.class, layout.fieldSrcPortMask());

        Integer proto = content.getFieldValue(Integer.class, layout.fieldProto());
        Integer protoMask = content.getFieldValue(Integer.class, layout.fieldProtoMask());

        if (clsName == null || tblId == null || ruleId == null || entryPtr == null
                || keyFound == null || dstIp == null || dstIpMask == null || srcIp == null
                || srcIpMask == null || dstPort == null || dstPortMask == null || srcPort == null
                || srcPortMask == null || proto == null || protoMask == null) {
            throw new IllegalArgumentException(layout.eventRteFlowClassifyTableEntryAdd() +
                    " event does not have expected fields"); //$NON-NLS-1$ ;
        }

        if(keyFound == 0) {
            FlowTableModel table = fClassifierStateProvier.getTable(clsName, tblId);
            if(table != null) {
                IPv4FiveTupleFlowRule rule = new IPv4FiveTupleFlowRule(
                        ruleId, dstIp, dstIpMask, srcIp, srcIpMask, dstPort, dstPortMask,
                        srcPort, srcPortMask, proto, protoMask);

                table.addRule(entryPtr, rule, ts);
            }
        }

    }

}
