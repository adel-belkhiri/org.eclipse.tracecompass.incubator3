package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */

@SuppressWarnings("nls")
public class DpdkLpmAnalysisEventLayout {

    /**
     * This event Indicates the creation of an LPM table
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmCreate() {
        return new String("librte_lpm:rte_lpm_create");
    }

    /**
     * This event Indicates the freeing of an LPM table
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmFree() {
        return new String("librte_lpm:rte_lpm_free");
    }

    /**
     * This event Indicates
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmAdd() {
        return new String("librte_lpm:rte_lpm_add");
    }

    /**
     * This event Indicates the deletion of an LPM rule
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmDelete() {
        return new String("librte_lpm:rte_lpm_delete");
    }

    /**
     * This event Indicates the deletion of all LPM rules
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmDeleteAll() {
        return new String("librte_lpm:rte_lpm_delete_all");
    }

    /**
     * This event is triggered when a lookup is initiated
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmLookup() {
        return new String("librte_lpm:rte_lpm_lookup");
    }

    /**
     * This event is triggered when a bulk (x4) lookup is initiated
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmLookupx4() {
        return new String("librte_lpm:rte_lpm_lookupx4");
    }

    /**
     * This event is triggered when a bulk lookup is initiated
     *
     * @return The name of the event
     * @since 3.0
     */
    public @NonNull String eventRteLpmLookupBulk() {
        return new String("librte_lpm:rte_lpm_lookup_bulk");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldName() {
        return new String("name");
    }


    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldMaxRules() {
        return new String("max_rules");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldIPv4Addr() {
        return new String("ipv4");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldAddrLength() {
        return new String("length");
    }


    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldNextHop() {
        return new String("next_hop");
    }


    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldIps() {
        return new String("ips");
    }


    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldNextHops() {
        return new String("next_hops");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldTblEntries() {
        return new String("tbl_entries");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldNumberOfIpAddresses() {
        return new String("n");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldDefaultValue() {
        return new String("defv");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldRuleDepth() {
        return new String("rule_depth");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldDepth() {
        return new String("depth");
    }

    /**
    *
    * @return
    * @since 3.0
    */
    public @NonNull String fieldOpResult() {
        return new String("ret");
    }
}

