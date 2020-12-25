package org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers;

import java.util.Arrays;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.DatapathModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.FlowModel;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OpenvSwitchEventHandler;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsFlowsStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;
import org.eclipse.tracecompass.tmf.core.event.ITmfEventField;

/**
 * @author Adel Belkhiri
 *
 */
public class OvsProcessUpcallHandler extends OpenvSwitchEventHandler {

    OvsFlowsStateProvider fOvsFlowsSP;

    /**
     * @param layout xx
     * @param stateProvider xx
     */
    public OvsProcessUpcallHandler(@NonNull IOpenvSwitchAnalysisEventLayout layout, OvsFlowsStateProvider stateProvider) {
        super(layout);
        this.fOvsFlowsSP = stateProvider;
    }


    /**
     * Convert an Ethernet code to a protocol name
     * @param dl_type proto code
     * @return
     */
    private static String getEtherProtoName(Integer dl_type) {
        String dl_type_str = "unknown"; //$NON-NLS-1$
        if(dl_type == 0x0800) {
            dl_type_str = "ipv4"; //$NON-NLS-1$
        } else
            if(dl_type == 0x86dd) {
                dl_type_str = "ipv6"; //$NON-NLS-1$
            } else
                if(dl_type == 0x0806 || dl_type == 0x8035) {
                    dl_type_str = "arp"; //$NON-NLS-1$
                }
                else
                    if(dl_type == 0x8847) {
                        dl_type_str = "mpls"; //$NON-NLS-1$
                    }

        return dl_type_str;
    }
    @Override
    public void handleEvent(@NonNull ITmfStateSystemBuilder ss, @NonNull ITmfEvent event) throws AttributeNotFoundException {

        final long [] EMPTY_ETHER_ADDR = {0, 0, 0, 0, 0, 0};
        final long [] EMPTY_IP_ADDR = {0, 0, 0, 0};
        final String SEPARATOR = ", "; //$NON-NLS-1$
        String key = new String() ;
        IOpenvSwitchAnalysisEventLayout layout = getLayout();

        /* unpack the event */
        long ts = event.getTimestamp().getValue();
        ITmfEventField content = event.getContent();
        Integer upcall_type = content.getFieldValue(Integer.class, layout.fieldUpcallType());
        String dpName = content.getFieldValue(String.class, layout.fieldDpName());

        /* generate a flow id from these values */
        Integer priority = content.getFieldValue(Integer.class, layout.fieldPriority());
        Integer skb_mark = content.getFieldValue(Integer.class, layout.fieldSkbMark());
        Integer in_port  = content.getFieldValue(Integer.class, layout.fieldInPort());
        Integer recirc_id  = content.getFieldValue(Integer.class, layout.fieldRecircId());

        long[] eth_addr_src  = content.getFieldValue(long[].class, "dl_src");
        long[] eth_addr_dst  = content.getFieldValue(long[].class, "dl_dst");
        Integer dl_type  = content.getFieldValue(Integer.class, "dl_type");
        //Integer nw_tos  = content.getFieldValue(Integer.class, "dl_tos");
        //Integer nw_ttl  = content.getFieldValue(Integer.class, "dl_ttl");

        long[] ip_addr_src  = content.getFieldValue(long[].class, "nw_src");
        long[] ip_addr_dst  = content.getFieldValue(long[].class, "nw_dst");

        Integer mpls_lse = content.getFieldValue(Integer.class, "mpls_lse");

        Integer tp_src  = content.getFieldValue(Integer.class, "tp_src");
        Integer tp_dst  = content.getFieldValue(Integer.class, "tp_dst");



        if (upcall_type == null || dpName == null) {
            throw new IllegalArgumentException("ovs_process_upcall event does not have expected fields"); //$NON-NLS-1$ ;
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
        if(dl_type != null) {
            key = key.concat("proto="+ getEtherProtoName(dl_type) + SEPARATOR); //$NON-NLS-1$
        }
        if(ip_addr_src != null && Arrays.equals(ip_addr_src, EMPTY_IP_ADDR) == false) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ip_addr_src.length; i++) {
                sb.append(String.format("%s", ip_addr_src[i], (i < ip_addr_src.length - 1) ? "." : ""));
            }
            key = key.concat("nw_addr_src=" + sb.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(ip_addr_dst != null && Arrays.equals(ip_addr_dst, EMPTY_IP_ADDR) == false) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ip_addr_dst.length; i++) {
                sb.append(String.format("%s", ip_addr_dst[i], (i < ip_addr_dst.length - 1) ? "." : ""));
            }
            key = key.concat("nw_addr_dst=" + Arrays.toString(ip_addr_dst) + SEPARATOR); //$NON-NLS-1$
        }
        if(mpls_lse!= null && mpls_lse != 0) {
            key = key.concat("mpls_lse=" + mpls_lse.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(tp_src != null && tp_src != 0) {
            key = key.concat("tp_src=" + tp_src.toString() + SEPARATOR); //$NON-NLS-1$
        }
        if(tp_dst!= null && tp_dst != 0) {
            key = key.concat("tp_dst=" + tp_dst.toString() + SEPARATOR); //$NON-NLS-1$
        }

        key = key.replaceFirst(", $", "");
        //String ovsUfid = UUID.nameUUIDFromBytes(NonNullUtils.checkNotNull(key.getBytes())).toString();

        DatapathModel dp = fOvsFlowsSP.getDatapath(dpName);
        if(dp != null) {
            FlowModel flow = dp.getFlow(key);
            if(flow == null) {
                flow = dp.createFlow(key, null /*dpUfid*/);
            }
            flow.setMatch(ts);
        }
    }
}
