package org.eclipse.tracecompass.incubator.internal.dpdk.core.classifier.analysis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;

/**
 * @author Adel Belkhiri
 *
 */
public class FlowTableModel {
    private final String fName;
    private Integer fType;
    private final Integer fId;
    private final ITmfStateSystemBuilder fSs;
    private int quark;

    private long totNbHit;
    private long totNbMiss;

    private final Map<Integer, FlowRuleModel> fRules = new HashMap<>();

    /**
     * Constructor
     * @param name
     *          Table name
     * @param id
     *          Table Id (a pointer on the table data structure)
     * @param ss
     *          StateSystemBuilder
     */
    public FlowTableModel(String name, int id, int parentQuark, ITmfStateSystemBuilder ss) {
        this.fName = name;
        this.fId = id;
        this.fSs = ss;
        this.totNbHit = 0;
        this.totNbMiss = 0;

        this.quark = fSs.getQuarkRelativeAndAdd(parentQuark, this.fName);

        int nbRulesQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.NB_RULES);
        fSs.modifyAttribute(0, 0, nbRulesQuark);

        int pktsInQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.TOT_NB_HIT);
        fSs.modifyAttribute(0, 0L, pktsInQuark);

        int pktsOutQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.TOT_NB_MISS);
        fSs.modifyAttribute(0, 0L, pktsOutQuark);

    }

    public String getName() {
        return this.fName;
    }

    public Integer getType() {
        return this.fType;
    }

    public Integer getId() {
        return this.fId;
    }

    public int getQuark() {
        return this.quark;
    }

    public int getNumberOfRules() {
        return fRules.size();
    }
    /**
     * @param pointer xx
     * @param rule xx
     */
    public void addRule(Integer key, FlowRuleModel rule, long ts) {
        if(!fRules.containsKey(key)) {
            fRules.put(key, rule);

            int nbRulesQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.NB_RULES);
            fSs.modifyAttribute(ts, fRules.size(), nbRulesQuark);

            int ruleSetQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.IDpdkModel_TABLE_RULES);
            int ruleQuark = fSs.getQuarkRelativeAndAdd(ruleSetQuark, rule.toString());

            int ruleNbHitQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkClassifierModelAttributes.NB_HIT);
            fSs.modifyAttribute(ts, 0L, ruleNbHitQuark);
        }
    }

    /**
     * Delete a rule from a "classify table"
     * @param ruleId xx
     * @param ts xx
     */
    public void removeRule(int ruleId, long ts) {
        Iterator<@NonNull Entry<Integer, FlowRuleModel>> it = fRules.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, FlowRuleModel> entry = it.next();
            FlowRuleModel rule = entry.getValue();

            if(((Number)rule.getId()).intValue() == ruleId) {
                int ruleSetQuark, ruleQuark;
                try {
                    int nbRulesQuark = fSs.getQuarkRelative(this.quark, IDpdkClassifierModelAttributes.NB_RULES);
                    fSs.modifyAttribute(ts, fRules.size() - 1, nbRulesQuark);

                    ruleSetQuark = fSs.getQuarkRelative(this.quark, IDpdkClassifierModelAttributes.IDpdkModel_TABLE_RULES);
                    ruleQuark = fSs.getQuarkRelative(ruleSetQuark, rule.toString());
                    fSs.removeAttribute(ts, ruleQuark);
                } catch (AttributeNotFoundException e) {
                    e.printStackTrace();
                }
                it.remove();
                break;
            }
        }
    }

    /**
     * @param entries
            Array of matching rules identifiers
     * @param nbPktIn
            Total number of packets on which the lookup was performed
     * @param nbPktOut
            Number of packets that matched table's rules
     * @param ts
            Timestamp
     */
    public void addLookupStats(long[] entries, int nbPktIn, int nbPktOut, long ts) {

        for(int i = 0; i < entries.length; i++) {
            FlowRuleModel rule = fRules.get((int) entries[i]);
            if(rule != null) {
                int ruleSetQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.IDpdkModel_TABLE_RULES);
                int ruleQuark = fSs.getQuarkRelativeAndAdd(ruleSetQuark, rule.toString());
                int ruleNbHitQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkClassifierModelAttributes.NB_HIT);
                fSs.modifyAttribute(ts, ++rule.nbHit, ruleNbHitQuark);
            }
        }
        this.totNbHit += nbPktOut;
        this.totNbMiss += nbPktIn - nbPktOut;

        int pktsInQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.TOT_NB_HIT);
        fSs.modifyAttribute(ts, this.totNbHit, pktsInQuark);

        int pktsOutQuark = fSs.getQuarkRelativeAndAdd(this.quark, IDpdkClassifierModelAttributes.TOT_NB_MISS);
        fSs.modifyAttribute(ts, this.totNbMiss, pktsOutQuark);
    }
}
