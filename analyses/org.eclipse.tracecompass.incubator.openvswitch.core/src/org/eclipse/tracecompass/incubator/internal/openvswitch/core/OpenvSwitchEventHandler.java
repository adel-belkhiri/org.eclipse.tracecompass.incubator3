package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.Arrays;

import org.eclipse.tracecompass.common.core.NonNullUtils;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.IOpenvSwitchAnalysisEventLayout;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

@SuppressWarnings("javadoc")
public abstract class OpenvSwitchEventHandler {

    private final IOpenvSwitchAnalysisEventLayout fLayout;

    /**
     * Constructor
     *
     * @param layout
     *            the analysis layout
     */
    public OpenvSwitchEventHandler(IOpenvSwitchAnalysisEventLayout layout) {
        fLayout = layout;
    }

    /**
     * Get the analysis layout
     *
     * @return the analysis layout
     */
    protected IOpenvSwitchAnalysisEventLayout getLayout() {
        return fLayout;
    }


    /**
     * Generates a string representation of a flow key
     * @param swFlowKey
     * @return a string
     */
    public String parseSwFlowKey(ITmfEventField swFlowKey /*, boolean userspace*/) {

        final long [] EMPTY_ETHER_ADDR = {0, 0, 0, 0, 0, 0};
        final long [] EMPTY_IP_ADDR = {0, 0, 0, 0};
        final String SEPARATOR = ", "; //$NON-NLS-1$

        String key = new String("") ; //$NON-NLS-1$
        Integer skb_mark, priority, in_port, recirc_id, tp_src, tp_dst;
        long[] eth_addr_src, eth_addr_dst;
        Integer mpls_lse = null;
        long[] nw_addr_src = null, nw_addr_dst = null;

        ITmfEventField phy = swFlowKey.getField(getLayout().fieldPhyHeader());
        skb_mark = phy.getFieldValue(Integer.class, getLayout().fieldSkbMark());
        priority = phy.getFieldValue(Integer.class, getLayout().fieldPriority());
        in_port  = phy.getFieldValue(Integer.class, getLayout().fieldInPort());
        recirc_id  = swFlowKey.getFieldValue(Integer.class, getLayout().fieldRecircId());

        ITmfEventField ethernetAddr = swFlowKey.getField(getLayout().fieldEthAddr());
        eth_addr_src  = ethernetAddr.getFieldValue(long[].class, getLayout().fieldSrc());
        eth_addr_dst  = ethernetAddr.getFieldValue(long[].class, getLayout().fieldDst());

        ITmfEventField transport = swFlowKey.getField("transport"); //$NON-NLS-1$
        tp_src  = transport.getFieldValue(Integer.class, getLayout().fieldSrc());
        tp_dst  = transport.getFieldValue(Integer.class, getLayout().fieldDst());

        String nwProtocol = NonNullUtils.checkNotNull(swFlowKey.getFieldValue(String.class, "network_header_type")); //$NON-NLS-1$
        if(nwProtocol.equals("_ipv4") == true || nwProtocol.equals("_arp")) {
            ITmfEventField nwHeader = swFlowKey.getField("network_header"); //$NON-NLS-1$
            ITmfEventField ipv4Header = nwHeader.getField(nwProtocol.substring(1));
            ITmfEventField ipv4Addr = ipv4Header.getField("addr"); //$NON-NLS-1$
            nw_addr_src  = ipv4Addr.getFieldValue(long[].class, getLayout().fieldSrc());
            nw_addr_dst  = ipv4Addr.getFieldValue(long[].class, getLayout().fieldDst());
        }
        else if(nwProtocol.equals("_mpls") == true) {
            ITmfEventField extNetHeader = swFlowKey.getField(getLayout().fieldExtNetHdr());
            ITmfEventField mplsHeader = extNetHeader.getField(nwProtocol.substring(1));
            mpls_lse = mplsHeader.getFieldValue(Integer.class, getLayout().fieldMplsTopLse());
        }

        /* Generate a string which represents the flow key */
        if(priority != null && priority != 0) {
            key = key.concat("priority=" + priority.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(skb_mark != null && skb_mark != 0) {
            key = key.concat("skb_mark=" + skb_mark.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(in_port != null && in_port != 0) {
            key = key.concat("in_port="+ in_port.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(recirc_id != null && recirc_id != 0 ) {
            key = key.concat("recirc_id=" + recirc_id.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(eth_addr_src != null && Arrays.equals(eth_addr_src, EMPTY_ETHER_ADDR) == false) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < eth_addr_src.length; i++) {
                sb.append(String.format("%02x%s", eth_addr_src[i], (i < eth_addr_src.length - 1) ? ":" : ""));
            }
            key = key.concat("eth_addr_src=" + sb.toString() + SEPARATOR);  //$NON-NLS-1$
        }
        if(eth_addr_dst != null && Arrays.equals(eth_addr_dst, EMPTY_ETHER_ADDR) == false ) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < eth_addr_dst.length; i++) {
                sb.append(String.format("%02x%s", eth_addr_dst[i], (i < eth_addr_dst.length - 1) ? ":" : ""));
            }
            key = key.concat("eth_addr_dst=" + sb.toString() + SEPARATOR); //$NON-NLS-1$
        }

        key = key.concat("proto="+ nwProtocol.substring(1) + SEPARATOR);

        if(nw_addr_src != null && Arrays.equals(nw_addr_src, EMPTY_IP_ADDR) == false) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nw_addr_src.length; i++) {
                sb.append(String.format("%s", nw_addr_src[i], (i < nw_addr_src.length - 1) ? "." : ""));
            }
            key = key.concat("nw_addr_src=" + sb.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(nw_addr_dst != null && Arrays.equals(nw_addr_dst, EMPTY_IP_ADDR) == false) {
            key = key.concat("nw_addr_dst=" + Arrays.toString(nw_addr_dst) + SEPARATOR); //$NON-NLS-1$
        }
        if(mpls_lse != null && mpls_lse != 0) {
            key = key.concat("mpls_lse="+ mpls_lse.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(tp_src != null && tp_src != 0) {
            key = key.concat("tp_src=" + tp_src.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(tp_dst!= null && tp_dst != 0) {
            key = key.concat("tp_dst=" + tp_dst.toString() + SEPARATOR); //$NON-NLS-1$
        }

        /* remove the last comma */
        key = key.replaceFirst(", $", "");
        return key;
    }

    /**
     * Handle a specific event.
     *
     * @param ss
     *            the state system to write to
     * @param event
     *            the event
     * @throws AttributeNotFoundException
     *             if the attribute is not yet create
     */
    public abstract void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException;

}
