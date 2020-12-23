package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class OutputPortModel extends PortModel {

    private int fQuark;
    private ITmfStateSystemBuilder fSs;
    private long nbTx;
    private long nbDrop;
    private final int fIndex;


    public OutputPortModel(int portId, int index, String name, PortTypeEnum type, int queueSize, int parentQuark, ITmfStateSystemBuilder ss, long ts) {
        super(portId, name, type, queueSize);
        this.fSs = ss;
        this.nbTx = 0;
        this.nbDrop = 0;
        this.fIndex = index;

        int portsQuark = this.fSs.getQuarkRelativeAndAdd(parentQuark, IDpdkPipelineModelAttributes.PORTS);
        int inputPortsQuark = this.fSs.getQuarkRelativeAndAdd(portsQuark, IDpdkPipelineModelAttributes.OUT_PORTS);
        this.fQuark = this.fSs.getQuarkRelativeAndAdd(inputPortsQuark, String.valueOf(index));

        int portNameQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.IDpdkModel_PORT_NAME);
        fSs.modifyAttribute(ts, getName(), portNameQuark);

        int typeQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.IDpdkModel_PORT_TYPE);
        this.fSs.modifyAttribute(ts, type.toString(), typeQuark);

        int txPktsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_TX);
        this.fSs.modifyAttribute(ts, 0L, txPktsQuark);

        int dropPktsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_PKTS_DROP);
        fSs.modifyAttribute(ts, this.nbDrop, dropPktsQuark);
    }

    public void send(int nbPkts, long ts) {
        this.nbTx += nbPkts;

        int txPktsQuark = this.fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkPipelineModelAttributes.NB_TX);
        fSs.modifyAttribute(ts, this.nbTx, txPktsQuark);
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
