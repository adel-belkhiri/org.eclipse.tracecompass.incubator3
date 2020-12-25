package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

/**
 * @author Adel Belkhiri
 *
 */

public class UpcallModel {

    private final int fId;
    private final int fInPort;
    private UpcallStatus status;
    private UpcallType type;
    private Integer slowReason;


    /**
     * UpcallModel class constructor
     * @param upcallId : ID of the upcall
     * @param inPort : port which sent this upcall
     * @param sockNumber : socket id
     * @param type : Upcall type
     */
    public UpcallModel(int upcallId, int inPort){
        this.fId = upcallId;
        this.fInPort = inPort;
        this.status = UpcallStatus.UPCALL_UNKNOWN;
        this.type = UpcallType.BAD_UPCALL;
        this.slowReason = SlowPathReasonMask.SLOW_UNDEFINED.ordinal();
    }

    /**
     * @return xx
     */
    public int getId() {
        return this.fId;
    }

    /**
     * @return xx
     */
    public String getType() {
        if(this.type == UpcallType.SLOW_PATH_UPCALL) {
            return SlowPathReasonMask.getReasons(this.slowReason);
        }
        return this.type.toString();
    }

    /**
     * @param type xx
     * @param slowReason xxx
     */
    public void setType(UpcallType type, Integer slowReason) {
        this.type = type;
        if(type == UpcallType.SLOW_PATH_UPCALL) {
            this.slowReason = slowReason;
        }
    }

    /**
     * @return xx
     */
    public int getInPort() {
        return this.fInPort;
    }

    /**
     * @return xx
     */
    public UpcallStatus getStatus() {
        return status;
    }


    /**
     * @param s Upcall status
     */
    public void setStatus(UpcallStatus s) {
        this.status = s;
    }

}
