package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

/**
 * @author adel
 *
 */
public class ServiceModel implements Cloneable {

    private final int fId;
    private final String fName;
    private final int fCallbackPointer;
    private ServiceStatus status;
    private long registrationTimestamp;
    private long activationTimestamp;

    private int quark;


    /**
     * Constructor
     * @param id xx
     * @param name xx
     * @param cb xx
     */
    public ServiceModel(int id, String name, int cb) {
        this.fId = id;
        this.fName = name;
        this.fCallbackPointer = cb;
        this.quark = -1; //invalid quark

        this.status = ServiceStatus.REGISTRED;
        this.registrationTimestamp = 0;
        this.activationTimestamp = 0;
    }

    @Override
    public ServiceModel clone() throws CloneNotSupportedException {
    return (ServiceModel) super.clone();
    }

    /**
     * @return the ID of the service
     */
    public int getId() {
        return this.fId;
    }

    /**
     * @return
     *      the service name
     */
    public String getName() {
        return this.fName;
    }

    /**
     * @param status xx
     */
    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    /**
     * @return
     *      the service name
     */
    public int getQuark() {
        return this.quark;
    }

    /**
     * @param xx
     *      the service name
     */
    public void setQuark(int quark) {
        this.quark = quark;
    }
    /**
     * @return
     */
    public ServiceStatus getStatus() {
        return this.status;
    }

    /**
     * @return
     *      registration timestamp
     */
    public long getRegistrationTimestamp() {
        return this.registrationTimestamp;
    }

    /**
     * @return
     *      activation timestamp
     */
    public long getActivationTimestamp() {
        return this.activationTimestamp;
    }

    /**
     *
     * @param ts
     *   registration timestamp
     */
    public void setRegistrationTimestamp(long ts) {
        this.registrationTimestamp = ts;
    }

    /**
    *
    * @param ts
    *   activation timestamp
    */
   public void setActivationTimestamp(long ts) {
       this.activationTimestamp = ts;
   }

    /**
     * @return
     *      registration timestamp
     */
    public long getCallbackPointer() {
        return this.fCallbackPointer;
    }
}
