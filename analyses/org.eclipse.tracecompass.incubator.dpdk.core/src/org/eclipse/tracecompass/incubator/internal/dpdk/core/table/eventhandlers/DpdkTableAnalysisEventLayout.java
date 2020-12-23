package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author Adel Belkhiri
 *
 */

@SuppressWarnings({"javadoc", "nls"})
public class DpdkTableAnalysisEventLayout {


    public @NonNull String eventRteTableHashCuckooCreate() {
        return new String("librte_table_hash:rte_table_hash_cuckoo_create");
    }

    public @NonNull String eventRteTableHashCuckooFree() {
        return new String("librte_table_hash:rte_table_hash_cuckoo_free");
    }

    public @NonNull String eventRteTableHashCuckooEntryAdd() {
        return new String("librte_table_hash:rte_table_hash_cuckoo_entry_add");
    }

    public @NonNull String eventRteTableHashCuckooEntryDelete() {
        return new String("librte_table_hash:rte_table_hash_cuckoo_entry_delete");
    }

    public @NonNull String eventRteTableHashCuckooLookup() {
        return new String("librte_table_hash:rte_table_hash_cuckoo_lookup");
    }

    ///

    public @NonNull String eventRteTableAclCreate() {
        return new String("librte_table_acl:rte_table_acl_create");
    }

    public @NonNull String eventRteTableAclFree() {
        return new String("librte_table_acl:rte_table_acl_free");
    }

    public @NonNull String eventRteTableAclEntryAdd() {
        return new String("librte_table_acl:rte_table_acl_entry_add");
    }

    public @NonNull String eventRteTableAclEntryDelete() {
        return new String("librte_table_acl:rte_table_acl_entry_delete");
    }

    public @NonNull String eventRteTableAclLookup() {
        return new String("librte_table_acl:rte_table_acl_lookup");
    }

    public @NonNull String fieldTblPointer() {
        return new String("tbl");
    }

    public @NonNull String fieldName() {
        return new String("name");
    }

    public @NonNull String fieldTableType() {
        return new String("type");
    }

    public @NonNull String fieldKey() {
        return new String("key");
    }

    public @NonNull String fieldKeyFound() {
        return new String("key_found");
    }

    public @NonNull String fieldNbPacketsIn() {
        return new String("n_pkts_in");
    }

    public @NonNull String fieldNbPacketsOut() {
        return new String("n_pkts_out");
    }

    public @NonNull String fieldEntriesPtr() {
        return new String("entries_ptr");
    }

    public @NonNull String fieldEntryPtr() {
        return new String("entry_ptr");
    }

    /**
    * Pointer on the LPM data structure
    * @return
    * @since 3.0
    */
    public @NonNull String fieldLpm() {
        return new String("lpm");
    }

    public @NonNull String fieldMaxRules() {
        return new String("max_rules");
    }

    public @NonNull String fieldPriority() {
        return new String("priority");
    }

    public @NonNull String fieldRulePosition() {
        return new String("rule_pos");
    }

    public @NonNull String fieldRuleFields() {
        return new String("field_value");
    }

    public @NonNull String fieldRuleFieldMasks() {
        return new String("field_mask");
    }

    public @NonNull String fieldDefType() {
        return new String("field_def_type");
    }

    public @NonNull String fieldDefSize() {
        return new String("field_def_size");
    }
}

