package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
public class NetworkDeviceModel {
    private final String fDevName;
    private final String fIfceName;

    private final long[] fHwAddr;

    private final Map<Integer, VirtualDeviceModel> fAttachedDevices = new HashMap<>();
    private final ITmfStateSystemBuilder fSs;
    private final int fQuark;

    /**
     * @param dev Device name
     * @param name Interface name
     * @param mac Mac address
     * @param ss TmfStateSystemBuilder
     */
    public NetworkDeviceModel(String dev, String name, long[] mac, ITmfStateSystemBuilder ss) {
        this.fDevName = dev;
        this.fIfceName = name;
        this.fHwAddr = mac;
        this.fSs = ss;

        /* create a Device subtree within the state system */
        this.fQuark = fSs.getQuarkAbsoluteAndAdd(IDpdkVhostModelAttributes.DEVICES, this.fDevName);

        int ifaceNameQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkVhostModelAttributes.IFACE_NAME);
        fSs.modifyAttribute(0, this.fIfceName, ifaceNameQuark);

        int hwAddrQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkVhostModelAttributes.HW_ADDR);
        fSs.modifyAttribute(0, getMacAddrStr(), hwAddrQuark);
    }

    /**
     * Convert the mac address to String format
     * @return
     */
    @SuppressWarnings("nls")
    private String getMacAddrStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.fHwAddr[0]).append(":");
        sb.append(this.fHwAddr[1]).append(":");
        sb.append(this.fHwAddr[2]).append(":");
        sb.append(this.fHwAddr[3]).append(":");
        sb.append(this.fHwAddr[4]).append(":");
        sb.append(this.fHwAddr[5]);

        return sb.toString();
    }

    /**
     * @param vid vid
     * @param connfd connection fd
     */
    public void attachVirtDevice(int vid, int connfd) {
        fAttachedDevices.put(vid, new VirtualDeviceModel(vid, connfd, this.fSs, this.fQuark));
    }

    /**
     * Check whether the vid is attached to this device or not
     * @param vid attached device id
     * @return true/false
     */
    public boolean isVidAttached(int vid) {
        return fAttachedDevices.containsKey(vid);
    }

    /**
     * @return the interface name
     */
    public String getIfceName() {
        return fIfceName;
    }

    /**
     * @param id
     * @return
     */
    public VirtualDeviceModel getVid(int id) {
        return fAttachedDevices.get(id);
    }
}
