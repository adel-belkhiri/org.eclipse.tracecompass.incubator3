package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateValueTypeException;
import org.eclipse.tracecompass.tmf.core.util.Pair;

/**
 * @author Adel Belkhiri
 *
 */
public class EmLookupObjectModel {
    private final String fName;
    private final Map<Pair<Long, Integer>, Object> fRules = new HashMap<>();
    private long totNbHit;
    private long totNbMiss;

    private final ITmfStateSystemBuilder fSs;
    private final int fQuark;

    /**
     * Constructor
     *
     * @param name
     *            EM object name
     * @param ss
     *            ITmfStateSystemBuilder
     */
    public EmLookupObjectModel(String name, ITmfStateSystemBuilder ss) {
        this.fName = name;
        this.fSs = ss;
        this.totNbHit = 0L;
        this.totNbMiss = 0L;

        /* create the Hash object tree */
        this.fQuark = fSs.getQuarkAbsoluteAndAdd(IDpdkLpmModelAttributes.EM_OBJS, this.fName);

        int nbRulesHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.NB_RULES);
        fSs.modifyAttribute(0, this.fRules.size(), nbRulesHitQuark);

        int totNbHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_HIT);
        fSs.modifyAttribute(0, this.totNbHit, totNbHitQuark);

        int totNbMissQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_MISS);
        fSs.modifyAttribute(0, this.totNbMiss, totNbMissQuark);
    }

    /**
     * @param keySig  Unused Parameter
     */
    public void lookupKey(@SuppressWarnings("unused") long keySig, boolean match, long ts) {
        if (!match) {
            this.totNbMiss += 1;
            try {
                int totNbHitQuark = fSs.getQuarkRelative(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_MISS);
                fSs.modifyAttribute(ts, this.totNbMiss, totNbHitQuark);
            } catch (AttributeNotFoundException | StateValueTypeException e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.totNbHit += 1;
                int totNbMissQuark = fSs.getQuarkRelative(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_HIT);
                fSs.modifyAttribute(ts, this.totNbHit, totNbMissQuark);
            } catch (AttributeNotFoundException | StateValueTypeException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add a rule to the hash object
     *
     * @param sig
     *      Key signature
     * @param position
     *      Key position
     * @param ts
     *      Timestamp
     */
    public void addRule(long sig, int position, long ts) {
        Pair<Long, Integer> key = new Pair<>(sig, position);
        if(!fRules.containsKey(key)) {
            fRules.put(key, null);

            int nbRulesHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.NB_RULES);
            fSs.modifyAttribute(ts, this.fRules.size(), nbRulesHitQuark);
        }
    }

    /**
     * Delete a rule from the hash object
     *
     * @param sig
     *      Key signature
     * @param position
     *      Key position
     * @param ts
     *      Timestamp
     */
    public void deleteRule(long sig, int position, long ts) {
        Pair<Long, Integer> key = new Pair<>(sig, position);
        if(fRules.containsKey(key)) {
            fRules.remove(key);

            int nbRulesHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.NB_RULES);
            fSs.modifyAttribute(ts, this.fRules.size(), nbRulesHitQuark);
        }
    }

    /**
     * Delete all rules
     * @param ts
     *      Timestamp
     */
    public void deleteAllRules(long ts) {
        fRules.clear();
        int nbRulesHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.NB_RULES);
        fSs.modifyAttribute(ts, 0, nbRulesHitQuark);
    }
}
