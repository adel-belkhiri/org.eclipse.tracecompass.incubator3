package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class InputPortModel extends PortModel {

    private int fQuark;
    private int fBurstSize;
    private boolean enabled;
    private int fIndex;
    private long nbRx;
    private long nbDrop;
    private long zeroPolls;
    private long nonZeroPolls;

    private ITmfStateSystemBuilder fSs;

    public InputPortModel(int portId, int index, String name, PortTypeEnum type, int burstSize, int parentQuark, ITmfStateSystemBuilder ss, long ts) {
        super(portId, name, type);
        this.fBurstSize = burstSize;
        this.fSs = ss;
        this.nbRx = 0;
        this.nbDrop = 0;
        this.enabled = false;
        this.fIndex = index;
        this.zeroPolls = 0;
        this.nonZeroPolls = 0;

        int portsQuark = this.fSs.getQuarkRelativeAndAdd(parentQuark, IDpdkPipelineModelAttributes.PORTS);
        int inputPortsQuark = this.fSs.getQuarkRelativeAndAdd(portsQuark, IDpdkPipelineModelAttributes.IN_PORTS);
        this.fQuark = this.fSs.getQuarkRelativeAndAdd(inputPortsQuark, String.valueOf(index));

        int portNameQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.IDpdkModel_PORT_NAME);
        fSs.modifyAttribute(ts, getName(), portNameQuark);

        int typeQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.IDpdkModel_PORT_TYPE);
        this.fSs.modifyAttribute(ts, type.toString(), typeQuark);

        int portStatusQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.PORT_STATUS);
        fSs.modifyAttribute(ts, "Disabled", portStatusQuark);

        int rxPktsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_RX);
        fSs.modifyAttribute(ts, 0L, rxPktsQuark);

        int dropPktsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_PKTS_DROP);
        fSs.modifyAttribute(ts, this.nbDrop, dropPktsQuark);

        int zeroPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.ZERO_POLLS);
        fSs.modifyAttribute(ts, this.zeroPolls, zeroPollsQuark);

        int nonZeroPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NON_ZERO_POLLS);
        fSs.modifyAttribute(ts, this.nonZeroPolls, nonZeroPollsQuark);
    }

    public int getBurstSize() {
        return this.fBurstSize;
    }

    public void setEnabled(long ts) {
        this.enabled = true;
        int portStatusQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.PORT_STATUS);
        fSs.modifyAttribute(ts, "Enabled", portStatusQuark);
    }

    public void setDisabled(long ts) {
        this.enabled = false;
        int portStatusQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.PORT_STATUS);
        fSs.modifyAttribute(ts, "Disabled", portStatusQuark);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void receive(int nbPkts, long zeroPollNb, long ts) {
        this.nbRx += nbPkts;
        this.nonZeroPolls += 1;
        this.zeroPolls = zeroPollNb;

        int rxPktsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_RX);
        fSs.modifyAttribute(ts, this.nbRx, rxPktsQuark);

        int zeroPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.ZERO_POLLS);
        fSs.modifyAttribute(ts, this.zeroPolls, zeroPollsQuark);

        int nonZeroPollsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NON_ZERO_POLLS);
        fSs.modifyAttribute(ts, this.nonZeroPolls, nonZeroPollsQuark);

    }

    public void dropPackets(int nbPkts, long ts) {
        this.nbDrop += nbPkts;

        int dropPktsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_PKTS_DROP);
        fSs.modifyAttribute(ts, this.nbDrop, dropPktsQuark);
    }

    public int getIndex() {
        return this.fIndex;
    }
}
