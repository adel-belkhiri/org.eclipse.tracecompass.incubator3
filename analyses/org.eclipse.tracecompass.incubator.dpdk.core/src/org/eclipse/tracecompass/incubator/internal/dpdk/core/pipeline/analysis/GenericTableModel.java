package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

/**
 * @author Adel Belkhiri
 *
 */
@SuppressWarnings("javadoc")
public class GenericTableModel {
    private final String fName;
    private final DpdkTableTypeEnum fType;
    private final Integer fId;

    private long totNbHit;
    private long totNbMiss;
    private long totNbDrop;


    /**
     * Constructor
     * @param name
     *          Table name
     * @param id
     *          Table Id (a pointer on the table data structure)
     * @param ss
     *          StateSystemBuilder
     */
    public GenericTableModel(String name, int id, DpdkTableTypeEnum type) {
        this.fName = name;
        this.fId = id;
        this.fType = type;

        this.totNbHit = 0L;
        this.totNbMiss = 0L;
        this.totNbDrop = 0L;
    }

    public String getName() {
        return this.fName;
    }

    public DpdkTableTypeEnum getType() {
        return this.fType;
    }

    public Integer getId() {
        return this.fId;
    }

    public long getNbHit() {
        return this.totNbHit;
    }


    public long getNbMiss() {
        return this.totNbMiss;
    }

    public long getNbDrop() {
        return this.totNbDrop;
    }

    public void dropPackets(int nbPkts) {
        this.totNbDrop += nbPkts;
    }
}
