package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class FlowModel {

    public final int fQuark;
    public final int fId;
    public int nbPkts;


    public FlowModel(int id, int quark) {
        this.fId = id;
        this.fQuark = quark;
        this.nbPkts = 0;
    }
}
