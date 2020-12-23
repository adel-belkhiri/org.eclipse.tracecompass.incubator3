package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;


/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class NetworkDeviceModel {
    private String fName;
    private int fId;
    private int fNbRxQueues;
    private int fNbTxQueues;

    public NetworkDeviceModel(String name, int id, int nbRxq, int nbTxq) {
        this.fName = name;
        this.fId = id;
        this.fNbRxQueues = nbRxq;
        this.fNbTxQueues = nbTxq;
    }

    public String getName() {
        return fName;
    }

    public int getId() {
        return fId;
    }

    public int getNbRxQueues() {
        return fNbRxQueues;
    }

    public int getNbTxQueues() {
        return fNbTxQueues;
    }


}
