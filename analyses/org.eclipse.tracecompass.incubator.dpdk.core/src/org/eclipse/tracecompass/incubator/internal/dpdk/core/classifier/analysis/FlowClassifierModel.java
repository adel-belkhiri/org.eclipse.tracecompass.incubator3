package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
public class FlowClassifierModel {
    private final String fName;
    private final int fQuark;

    private final Map<Integer, FlowTableModel> fTables = new HashMap<>();
    private final ITmfStateSystemBuilder fSs;



    /**
     * @param clsName
     *      Classifier name
     * @param ss
     *      State system builder
     */
    public FlowClassifierModel(String clsName, @NonNull ITmfStateSystemBuilder ss) {
        this.fName = clsName;
        this.fSs = ss;

        /* Create the classifier node in the ss */
        this.fQuark = fSs.getQuarkAbsoluteAndAdd(this.fName);
    }

    /**
     * @param tblName xx
     * @param tblId xx
     * @return xx
     */
    public boolean addTable(String tblName, int tblId) {
        if(!fTables.containsKey(tblId)) {
            FlowTableModel table = new FlowTableModel(tblName, tblId, this.fQuark, this.fSs);
            fTables.put(table.getId(), table);
            return true;
        }
        return false;
    }

    /**
     * @param tableId
     * @param ts
     * @return xx
     */
    public FlowTableModel deleteTable(Integer tableId, long ts) {

        FlowTableModel table = fTables.get(tableId);
        if(table != null) {
            int quark = table.getQuark();
            if(quark != 0) {
                fSs.removeAttribute(ts, quark);
            }
            fTables.remove(tableId);
        }
        return table;
    }

    /**
     * Delete all tables in the classifier
     * @param ts xx
     */
    public void deleteAllTable(long ts) {
        for(FlowTableModel table : fTables.values()) {
            int quark = table.getQuark();
            if(quark != 0) {
                fSs.removeAttribute(ts, quark);
            }
        }
        fTables.clear();
    }

    /**
     * @param tableId
     * @return
     */
    public FlowTableModel getTable(Integer tableId) {
        return fTables.get(tableId);
    }
}
