package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class PipelineTableModel extends GenericTableModel {
    private final int fIndex;
    PipelineAction defaultAction;
    private int defaultFwdPortIndex;
    private int childTableIndex;
    private final Map<Integer, InputPortModel> fAttachedInputPorts = new HashMap<>();

    private long pktsFwdToChildTable;
    private final ITmfStateSystemBuilder fSs;
    private int quark;


    /**
     * Constructor
     * @param name
     *          Table name
     * @param id
     *          Table Id (a pointer on the table data structure)
     * @param ss
     *          StateSystemBuilder
     */
    public PipelineTableModel(String name, int id, DpdkTableTypeEnum type, int index, int parentQuark,
            ITmfStateSystemBuilder ss, long creationTimestamp) {
        super(name, id, type);
        this.fIndex = index;
        this.fSs = ss;
        this.defaultFwdPortIndex = -1;
        this.childTableIndex = -1;
        this.pktsFwdToChildTable = 0L;
        this.defaultAction = PipelineAction.ACTION_DROP;

        int tableSetQuark = fSs.getQuarkRelativeAndAdd(parentQuark, IDpdkPipelineModelAttributes.IDpdkModel_TABLES);
        this.quark = fSs.getQuarkRelativeAndAdd(tableSetQuark, String.valueOf(index));

        int tableNameQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.IDpdkModel_TAB_NAME);
        fSs.modifyAttribute(creationTimestamp, getName(), tableNameQuark);

        int defaultActionQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.DEFAULT_ACTION);
        fSs.modifyAttribute(creationTimestamp, this.defaultAction.toString(), defaultActionQuark);


        int nbDropQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.NB_PKTS_DROP);
        fSs.modifyAttribute(creationTimestamp, 0L, nbDropQuark);

    }

    public int getQuark() {
        return this.quark;
    }

    public void setDefaultAction(PipelineAction action, int next, long ts) {
        this.defaultAction = action;
        if(action == PipelineAction.ACTION_PORT ) {
            this.defaultFwdPortIndex = next;
        } else if (action == PipelineAction.ACTION_TABLE) {
            this.childTableIndex = next;

            int childTableQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.CHILD_TABLE);
            fSs.modifyAttribute(ts, this.childTableIndex, childTableQuark);

            int fwdChildTableQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.FWD_CHILD_TABLE);
            fSs.modifyAttribute(ts, this.pktsFwdToChildTable, fwdChildTableQuark);
        }

        int defaultActionQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.DEFAULT_ACTION);
        fSs.modifyAttribute(ts, this.defaultAction.toString(), defaultActionQuark);
    }

    public void setChildTableID(int next, long ts) {
        this.childTableIndex = next;

        int childTableQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.CHILD_TABLE);
        fSs.modifyAttribute(ts, this.childTableIndex, childTableQuark);

        int fwdChildTableQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.FWD_CHILD_TABLE);
        fSs.modifyAttribute(ts, this.pktsFwdToChildTable, fwdChildTableQuark);
    }

    public int getIndex() {
        return this.fIndex;
    }

    public int getDefaultFwdPortIndex() {
        return this.defaultFwdPortIndex;
    }

    public int getChildTableIndex() {
        return this.childTableIndex;
    }

    public PipelineAction getDefaultAction() {
        return this.defaultAction;
    }

   /**
    * Attach an input port to a pipeline table
    * @param portIdx
    * @param ts unused paramter
    */
    public void attachInputPort(InputPortModel port, long ts) {
       fAttachedInputPorts.put(port.getId(), port);

       int inputPortsQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.IN_PORTS);
       fSs.getQuarkRelativeAndAdd(inputPortsQuark, String.valueOf(port.getIndex()));
   }

    /**
     * Indicates if an input port is attached to this pipeline table
     * @param portIdx
     *      Port index
     * @return
     *      True or False
     */
     public boolean isAttachedPort(int portIdx) {
        return fAttachedInputPorts.containsKey(portIdx);
    }

     public void forwardToChildTable(int nbPkts, long ts) {
         this.pktsFwdToChildTable += nbPkts;

         int fwdChildTableQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.FWD_CHILD_TABLE);
         fSs.modifyAttribute(ts, this.pktsFwdToChildTable, fwdChildTableQuark);
     }

     public void dropPackets(int nbPkts, long ts) {
         super.dropPackets(nbPkts);

         int tableDropPktsQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkPipelineModelAttributes.NB_PKTS_DROP);
         fSs.modifyAttribute(ts, this.getNbDrop(), tableDropPktsQuark);
     }
}
