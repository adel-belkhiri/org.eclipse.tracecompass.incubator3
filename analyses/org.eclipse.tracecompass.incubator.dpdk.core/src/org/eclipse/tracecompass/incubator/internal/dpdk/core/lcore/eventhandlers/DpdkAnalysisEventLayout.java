package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */

@SuppressWarnings("nls")
public class DpdkAnalysisEventLayout {

    /**
     * This event Indicates a change in the lcore operating mode
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventLcoreRoleChange() {
        return new String("librte_eal:lcore_role_change");
    }

    /**
    * This event indicates that the lcore is ready to run tasks/services
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventThreadLcoreReady() {
        return new String("librte_eal:thread_lcore_ready");
    }

    /**
    * This event indiates that the lcore is executing a task
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventThreadLcoreRunning() {
        return new String("librte_eal:thread_lcore_running");
    }


    /**
    * This event indiates that the lcore is waiting for a task to execute
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventThreadLcoreWaiting() {
        return new String("librte_eal:thread_lcore_waiting");
    }


    /**
    * This event indiates that a service lcore is ready to execute services
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventServiceLcoreReady() {
        return new String("librte_eal:service_lcore_ready");
    }

    /**
    * This event indiates that a service lcore is ready to execute services
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventServiceLcoreStop() {
        return new String("librte_eal:service_lcore_stop");
    }

    /**
    * This event indiates the start of execution of a service
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventServiceRunBegin() {
        return new String("librte_eal:service_run_begin");
    }


    /**
    *  This event indiates the end of execution of a service
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventServiceRunEnd() {
        return new String("librte_eal:service_run_end");
    }


    /**
    * This event indiates the mapping between a service and an lcore
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventServiceMapLcore() {
        return new String("librte_eal:service_map_lcore");
    }


    /**
    * This event indiates the registration of a service
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventServiceComponentRegister() {
        return new String("librte_eal:service_component_register");
    }

    /**
    * This event indiates the registration of a service
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventServiceComponentUnregister() {
        return new String("librte_eal:service_component_unregister");
    }

    /**
    * This event indiates that a task was launched to be executed on a "thread lcore"
    *
    * @return The name of the event
    * @since 3.0
    */
    public @NonNull String eventThreadRemoteLaunch() {
        return new String("librte_eal:thread_remote_launch");
    }


    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldLcoreId() {
        return new String("lcore_id");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldCoreRole() {
        return new String("lcore_role");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldCoreType() {
        return new String("master");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldCoreStatus() {
        return new String("status");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldCoreRet() {
        return new String("ret");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldFunction() {
        return new String("f");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldCallbackFunction() {
        return new String("cb");
    }


    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldArg() {
        return new String("arg");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldServiceEnabled() {
        return new String("enabled");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldServiceId() {
        return new String("id");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldCpuSet() {
        return new String("cpuset");
    }

    /**
    *
    * @return The name of the field
    * @since 3.0
    */
    public @NonNull String fieldServiceName() {
        return new String("service_name");
    }

}

