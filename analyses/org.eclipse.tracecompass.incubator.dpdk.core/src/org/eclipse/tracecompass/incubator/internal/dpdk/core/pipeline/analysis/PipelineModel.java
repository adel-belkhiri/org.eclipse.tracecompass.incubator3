package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;

/**
 * @author Adel Belkhiri
 *
 */
public class PipelineModel {
    private final String fName;
    private final int fQuark;

    private final Map<Integer, PipelineTableModel> fTables = new HashMap<>();

    private final Map<Integer, InputPortModel> fInPorts = new HashMap<>();
    private final Map<Integer, OutputPortModel> fOutPorts = new HashMap<>();

    private final ITmfStateSystemBuilder fSs;



    /**
     * @param clsName
     *      Classifier name
     * @param ss
     *      State system builder
     */
    public PipelineModel(String clsName, @NonNull ITmfStateSystemBuilder ss) {
        this.fName = clsName;
        this.fSs = ss;

        /* Create the pipeline node in the ss */
        this.fQuark = fSs.getQuarkAbsoluteAndAdd(IDpdkPipelineModelAttributes.PIPELINES, this.fName);
    }

    /**
     * @param tblName xx
     * @param tblId xx
     * @param tblIndex xx
     * @param type xx
     * @return xx
     */
    public boolean addTable(String tblName, int tblId, int tblIndex, DpdkTableTypeEnum type, long ts) {
        if(!fTables.containsKey(tblId)) {
            PipelineTableModel table = new PipelineTableModel(tblName, tblId, type, tblIndex,
                    this.fQuark, this.fSs, ts);
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
    public PipelineTableModel deleteTable(Integer tableId, long ts) {

        PipelineTableModel table = fTables.get(tableId);
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
     * Delete all pipeline tables
     * @param ts xx
     */
    public void deleteAllTable(long ts) {
        for(PipelineTableModel table : fTables.values()) {
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
    public PipelineTableModel getTable(Integer tableId) {
        return fTables.get(tableId);
    }

    /**
     * Add an input port to the pipeline
     */
    @SuppressWarnings("javadoc")
    public void addInputPort(int portId, int index, String name, PortTypeEnum type, int burstSize, long ts) {
        InputPortModel port = fInPorts.get(portId);
        if(port == null) {
            port = new InputPortModel(portId, index, name, type, burstSize, this.fQuark, this.fSs, ts);
            fInPorts.put(portId, port);
        }
    }

    /**
     * Add an output port to the pipeline
     * @param portId
     */
    @SuppressWarnings("javadoc")
    public void addOutputPort(int portId, int index, String name, PortTypeEnum type, long ts) {
        OutputPortModel port = fOutPorts.get(portId);
        if(port == null) {
            port = new OutputPortModel(portId, index, name, type, this.fQuark, this.fSs, ts);
            fOutPorts.put(portId, port);
        }
    }

    /**
     * @param portId
     * @return
     */
    public InputPortModel getInputPort(int portId) {
        return fInPorts.get(portId);
    }


    /**
     * @param index
     * @return
     */
    public InputPortModel searchInputPortByIndex(int index) {
        for(InputPortModel port : fInPorts.values()) {
            if(port.getIndex() == index) {
                return port;
            }
        }
        return null;
    }

    /**
     * @param index
     * @return
     */
    public OutputPortModel searchOutputPortByIndex(int index) {
        for(OutputPortModel port : fOutPorts.values()) {
            if(port.getIndex() == index) {
                return port;
            }
        }
        return null;
    }

    /**
     * @param index
     * @return
     */
    public PipelineTableModel searchTableByIndex(int index) {
        for(PipelineTableModel table : fTables.values()) {
            if(table.getIndex() == index) {
                return table;
            }
        }
        return null;
    }

    /**
     * @param portId
     * @return
     */
    public OutputPortModel getOutputPort(int portId) {
        return fOutPorts.get(portId);
    }

    @SuppressWarnings("javadoc")
    public void receivePackets(int portId, int nbPkts, long zeroPolls, long ts) {
        InputPortModel port = fInPorts.get(portId);
        if(port != null && nbPkts > 0) {
            port.receive(nbPkts, zeroPolls, ts);
        }
    }

    @SuppressWarnings("javadoc")
    public void sendPackets(int portId, int nbPkts, long ts) {
        OutputPortModel port = fOutPorts.get(portId);
        if(port != null) {
            port.send(nbPkts, ts);
        }
    }
}
