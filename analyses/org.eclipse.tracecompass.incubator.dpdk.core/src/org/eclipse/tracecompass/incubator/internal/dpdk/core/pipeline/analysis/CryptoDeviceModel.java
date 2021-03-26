package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;


/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class CryptoDeviceModel {
    private String fName;
    private int fId;
    private int fNbQueuePairs;

    public CryptoDeviceModel(String name, int id, int nbQpairs) {
        this.fName = name;
        this.fId = id;
        this.fNbQueuePairs = nbQpairs;
    }

    public String getName() {
        return fName;
    }

    public int getId() {
        return fId;
    }

    public int getNbQueuePairs() {
        return fNbQueuePairs;
    }
}
