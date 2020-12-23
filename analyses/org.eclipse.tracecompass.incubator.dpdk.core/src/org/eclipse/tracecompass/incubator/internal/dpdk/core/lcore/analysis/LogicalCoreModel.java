package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;


/**
 * @author Adel Belkhiri
 *
 */
public class LogicalCoreModel {

    private final Map<Integer /*service id*/, ServiceModel> fServices = new HashMap<>();


    private final int fCoreId;
    private final int fCoreQuark;

    private LogicalCoreStatus status;
    private long execFunction;
    private LogicalCoreRole role;
    private boolean isMaster;

    private final ITmfStateSystemBuilder fSs;


    /**
     * A datapath is instancitaed by is a collection of virtual ports.
     * @param id xx
     * @param ss xx
     */
    public LogicalCoreModel(int id, ITmfStateSystemBuilder ss) {
        this.fCoreId = id;
        this.fSs = ss;

        this.isMaster = false;
        this.status = LogicalCoreStatus.OFF;
        this.role = LogicalCoreRole.LCORE_OFF;
        this.execFunction = 0;

        /* create a Datapaths subtree within the state system */
        this.fCoreQuark = fSs.getQuarkAbsoluteAndAdd(IDpdkModelAttributes.LCORES, String.valueOf(this.fCoreId));

        int lcoreModeQuark = fSs.getQuarkRelativeAndAdd(this.fCoreQuark, IDpdkModelAttributes.LCORE_ROLE);
        fSs.modifyAttribute(0, this.role.toString(), lcoreModeQuark);

        int lcoreStatusQuark = fSs.getQuarkRelativeAndAdd(this.fCoreQuark, IDpdkModelAttributes.LCORE_STATUS);
        fSs.modifyAttribute(0, this.status.toString(), lcoreStatusQuark);

        fSs.getQuarkRelativeAndAdd(this.fCoreQuark, IDpdkModelAttributes.LCORE_FUNCTION);
    }


    /**
     * @return id
     */
    public int getId() {
        return this.fCoreId;
    }

    /**
     */
    public void setAsMaster() {
        this.isMaster = true;
    }

    /**
     * @return if this core is master or slave
     */
    public boolean isMaster() {
        return this.isMaster;
    }

    /**
     * get a service
     * @param id identifier of a service
     * @return a ServiceModel object
     */
    public @Nullable ServiceModel getService(int id) {
         return fServices.get(id);
    }


    /**
     * @param service
     *          Service to add
     */
    public void mapService(ServiceModel service, long ts) {

        if(!fServices.containsKey(service.getId())) {

            int lcoreServicesQuark = fSs.getQuarkRelativeAndAdd(this.fCoreQuark, IDpdkModelAttributes.SERVICES);
            int serviceQuark = fSs.getQuarkRelativeAndAdd(lcoreServicesQuark, String.valueOf(service.getId()));

            int serviceNameQuark = fSs.getQuarkRelativeAndAdd(serviceQuark, IDpdkModelAttributes.SERVICE_NAME);
            fSs.modifyAttribute(ts, service.getName(), serviceNameQuark);

            int serviceStatusQuark = fSs.getQuarkRelativeAndAdd(serviceQuark, IDpdkModelAttributes.SERVICE_STATUS);
            fSs.modifyAttribute(ts, ServiceStatus.PENDING.toString(), serviceStatusQuark);

            try {
                ServiceModel newServiceInstance = service.clone();
                newServiceInstance.setQuark(serviceQuark);
                newServiceInstance.setStatus(ServiceStatus.PENDING);
                fServices.put(newServiceInstance.getId(), newServiceInstance);
            }
            catch (CloneNotSupportedException e) {
                e.printStackTrace();

            }
        }

    }


    /**
     * @param service
     *          Service to add
     */
    public void unmapService(int id, long ts) {

        ServiceModel service = fServices.get(id);
        if(service != null) {

            int serviceQuark = service.getQuark();

            int serviceNameQuark = fSs.getQuarkRelativeAndAdd(serviceQuark, IDpdkModelAttributes.SERVICE_NAME);
            fSs.modifyAttribute(ts, (String)null, serviceNameQuark);

            int serviceStatusQuark = fSs.getQuarkRelativeAndAdd(serviceQuark, IDpdkModelAttributes.SERVICE_STATUS);
            fSs.modifyAttribute(ts, (String)null, serviceStatusQuark);

            /* This service is no longer active .. we remove it then */
            fServices.remove(id);
        }

    }
    /**
     * @param id xx
     * @param newStatus xx
     * @param ts xx
     * @return xx
     */
    public boolean updateServiceStatus(int id, ServiceStatus newStatus, long ts) {
        ServiceModel service = fServices.get(id);

        if(service != null) {
            int statusQuark;

            service.setStatus(newStatus);
            try {
                statusQuark = fSs.getQuarkRelative(service.getQuark(), IDpdkModelAttributes.SERVICE_STATUS);
                fSs.modifyAttribute(ts, newStatus.toString(), statusQuark);
                return true;
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    /**
     * @param ts
     *          Timestamp
     * @param newStatus
     *          The new lcore status
     */
    public void setStatus(long ts, LogicalCoreStatus newStatus, long f) {
        if(newStatus == this.status) {
            return ;
        }

        this.status = newStatus;
        try {
            int lcoreStatusQuark = fSs.getQuarkRelative(this.fCoreQuark, IDpdkModelAttributes.LCORE_STATUS);
            fSs.modifyAttribute(ts, this.status.toString(), lcoreStatusQuark);

            int lcoreFuncQuark = fSs.getQuarkRelative(this.fCoreQuark, IDpdkModelAttributes.LCORE_FUNCTION);
            if(newStatus == LogicalCoreStatus.RUNNING) {
                this.execFunction = f;
                fSs.modifyAttribute(ts, Long.toHexString(this.execFunction), lcoreFuncQuark);
            }
            else {
                fSs.modifyAttribute(ts, (String) null, lcoreFuncQuark);
            }
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ts
     *          Timestamp
     * @param newRole
     *          The new lcore role
     */
    public void setRole(long ts, LogicalCoreRole newRole) {
        if(newRole == this.role) {
            return;
        }

        /* modify the role of this core */
        this.role = newRole;

        /* modify its status */
        if(newRole == LogicalCoreRole.LCORE_RTE /*|| newRole == LogicalCoreRole.LCORE_SERVICE*/) {
            this.status = LogicalCoreStatus.DISABLED;
        } else if (newRole == LogicalCoreRole.LCORE_OFF){
            this.status = LogicalCoreStatus.OFF;
        }

        try {
            int lcoreRoleQuark, lcoreStatusQuark;

            lcoreRoleQuark= fSs.getQuarkRelative(this.fCoreQuark, IDpdkModelAttributes.LCORE_ROLE);
            fSs.modifyAttribute(ts, this.role.toString(), lcoreRoleQuark);

            lcoreStatusQuark = fSs.getQuarkRelative(this.fCoreQuark, IDpdkModelAttributes.LCORE_STATUS);
            fSs.modifyAttribute(ts, this.status.toString(), lcoreStatusQuark);

        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
        }
    }

    public LogicalCoreRole getRole() {
        return this.role;
    }
}
