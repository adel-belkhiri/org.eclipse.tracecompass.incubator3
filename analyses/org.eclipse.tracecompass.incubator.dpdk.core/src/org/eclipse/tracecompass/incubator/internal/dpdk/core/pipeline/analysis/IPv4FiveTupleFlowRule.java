package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import java.net.Inet4Address;

import com.google.common.net.InetAddresses;

/**
 * @author Adel Belkhiri
 *
 */

public class IPv4FiveTupleFlowRule extends FlowRuleModel {
    private final int fDstIP;
    private final int fDstIpMask;
    private final int fSrcIP;
    private final int fSrcIpMask;
    private final int fDstPort;
    private final int fDstPortMask;
    private final int fSrcPort;
    private final int fSrcPortMask;
    private final int fProto;
    private final int fProtoMask;

    @SuppressWarnings("javadoc")
    public IPv4FiveTupleFlowRule(int ruleId, int dstIp, int dstIpMask, int srcIp, int srcIpMask,
            int dstPort, int dstPortMask, int srcPort, int srcPortMask, int proto, int protoMask) {
        super(ruleId);
        this.fDstIP = dstIp;
        this.fDstIpMask = dstIpMask;
        this.fSrcIP = srcIp;
        this.fSrcIpMask = srcIpMask;
        this.fDstPort = dstPort;
        this.fDstPortMask = dstPortMask;
        this.fSrcPort = srcPort;
        this.fSrcPortMask = srcPortMask;
        this.fProto = proto;
        this.fProtoMask = protoMask;
    }


    @SuppressWarnings("nls")
    @Override
    public String toString() {
        Inet4Address maskedSrcIpAddr = InetAddresses.fromInteger(this.fSrcIP & this.fSrcIpMask);
        Inet4Address maskedDstIpAddr = InetAddresses.fromInteger(this.fDstIP & this.fDstIpMask);
        int maskedSrcPort = this.fSrcPort & this.fSrcPortMask;
        int maskedDstPort = this.fDstPort & this.fDstPortMask;
        int maskedProto = this.fProto & this.fProtoMask;

        return "@IP (From : " + maskedSrcIpAddr.toString().substring(1) + "/" + maskToPrefixLength(this.fSrcIpMask)
        + ", To : " + maskedDstIpAddr.toString().substring(1) + "/" + maskToPrefixLength(this.fDstIpMask)
        + "), Port (From : " + String.valueOf(maskedSrcPort) + ", To : " + String.valueOf(maskedDstPort)
        + "), Proto: " + String.valueOf(maskedProto);
    }

}
