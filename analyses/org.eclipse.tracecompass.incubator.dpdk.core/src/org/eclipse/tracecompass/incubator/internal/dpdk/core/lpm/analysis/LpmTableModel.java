package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import java.net.Inet4Address;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.tmf.core.util.Pair;

import com.google.common.net.InetAddresses;

/**
 * @author adel
 *
 */
public class LpmTableModel {

    private final String fName;
    private Long totNbHit;
    private Long totNbMiss;

    private final ITmfStateSystemBuilder fSs;
    private final int fQuark;

    private final Map<Pair<Inet4Address, Integer>, RuleModel> fRules = new HashMap<>();

    /**
     *  An internal class representing an LPM rule.
     *  A rule is composed of two parts : the matching part (key) and the action part (forwarding port)
     */
    private class RuleModel {
        public final Pair<Inet4Address, Integer> fId;
        public final Integer fNextHop;
        public Long nbHit;

        public RuleModel(Inet4Address ipv4, int depth, int nextHop) {
            this.fId = new Pair<>(ipv4, depth);
            this.fNextHop = nextHop;

            this.nbHit = 0L;
        }

        public Pair<Inet4Address, Integer> getId() {
            return this.fId;
        }

    }

    String getName() {
        return this.fName;
    }

    /**
     * Constructor of class LpmTableModel
     * @param name
     *      Name of the LPM table
     * @param ss
     *      TmfStateSystemBuilder
     */
    public LpmTableModel(String name, ITmfStateSystemBuilder ss) {
        this.fName = name;
        this.totNbHit = 0L;
        this.totNbMiss = 0L;

        this.fSs = ss;

        /* create the LPM tables tree within the state system */
        this.fQuark = fSs.getQuarkAbsoluteAndAdd(IDpdkLpmModelAttributes.LPM_TABS, this.fName);
        int totNbHitQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_HIT);
        fSs.modifyAttribute(0, this.totNbHit, totNbHitQuark);

        int totNbMissQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_MISS);
        fSs.modifyAttribute(0, this.totNbMiss, totNbMissQuark);
    }

    /**
     * Add a rule to the LPM table
     * @param addr
     *      First part of the rule key : IPv4 address
     * @param depth
     *      Second part of the rule key : Mask depth
     * @param nextHop
     *      Action part of the rule : Next Hop
     * @param ts
     *      Timestamp
     * @return
     *      success or failure
     */
    public boolean addRule (Inet4Address addr, int depth, int nextHop, long ts) {
        RuleModel rule = new RuleModel(addr, depth, nextHop);
        if(!fRules.containsKey(rule.getId())) {
            int rulesQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.LPM_RULES);
            int ruleQuark = fSs.getQuarkRelativeAndAdd(rulesQuark, addr.getHostAddress().concat("/").concat(String.valueOf(depth))); //$NON-NLS-1$

            int nextHopQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkLpmModelAttributes.NEXT_HOP);
            fSs.modifyAttribute(ts, nextHop, nextHopQuark);

            int nbHitQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkLpmModelAttributes.NB_HIT);
            fSs.modifyAttribute(ts, 0L, nbHitQuark);

            fRules.put(rule.getId(), rule);
            return true;
        }
        return false;
    }

    /**
     * Delete a specific rule identified by <addr, depth> from the LPM table
     * @param addr
     *      IPv4 address
     * @param depth
     *      Depth of the Mask
     * @param ts
     *      Timestamp
     */
    public void deleteRule (Inet4Address addr, int depth, long ts) {
        RuleModel rule = fRules.get(new Pair<>(addr, depth));
        if(rule != null) {
            try {

                int rulesQuark = fSs.getQuarkRelative(this.fQuark, IDpdkLpmModelAttributes.LPM_RULES);
                int ruleQuark = fSs.getQuarkRelative(rulesQuark, addr.getHostAddress().concat("/").concat(String.valueOf(depth)));

                int nextHopQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkLpmModelAttributes.NEXT_HOP);
                fSs.modifyAttribute(ts, (Integer) null, nextHopQuark);

                int nbHitQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkLpmModelAttributes.NB_HIT);
                fSs.modifyAttribute(ts,  (Long) null, nbHitQuark);
            }
            catch(AttributeNotFoundException e) {
                Activator.getInstance().logError(e.toString());
            }

            fRules.remove(rule.getId());
        }
    }

    /**
     * Removes all the rules from the table
     * @param ts
     *      Timestamp
     */
    public void deleteAllRules(long ts) {
        Iterator<@NonNull Entry<Pair<Inet4Address, Integer>, RuleModel>> it = fRules.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Pair<Inet4Address, Integer>, RuleModel> entry = it.next();
            Pair<Inet4Address, Integer> ruleId = entry.getKey();
            deleteRule(ruleId.getFirst(), ruleId.getSecond(), ts);
        }

        fRules.clear();
    }

    /**
     * @param addr
     *      First part of the rule key
     * @param depth
     *      Second part of the rule key
     * @return
     *      Associated "next hop"
     */
    public @Nullable Integer getRuleNextHop(Inet4Address addr, int depth) {
        RuleModel rule = fRules.get(new Pair<>(addr, depth));
        if(rule != null) {
            return rule.fNextHop;
        }
        return null;
    }

    public static int depthToMask(int depth)
    {
        /*
         * Transform network prefix to a mask
         */
        return 0xffffffff << (32 - depth);
    }

    /**
     * This method performs a rule lookup
     * @param ifceAddr
     *       Interface address
     * @param depth
     *       Depth of the rule mask that was found
     * @param match
     *       Does the lookup ended successfully or not ?
     * @param ts
     *       Timestamp
     */
    public void ruleLookup (Inet4Address ifceAddr, int depth, boolean match, long ts) {
        if(!match) {
            this.totNbMiss += 1;
            int totNbHitQuark;
            try {
                totNbHitQuark = fSs.getQuarkRelative(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_MISS);
            } catch (AttributeNotFoundException e) {
                e.printStackTrace();
                return;
            }
            fSs.modifyAttribute(ts, this.totNbMiss, totNbHitQuark);
        }
        else {
                Integer ifceMaskedAddress = InetAddresses.coerceToInteger(ifceAddr) & depthToMask(depth);
                Pair<Inet4Address, Integer> key = new Pair<>(InetAddresses.fromInteger(ifceMaskedAddress), depth);
                RuleModel rule = fRules.get(key);

                if(rule != null) {
                    rule.nbHit += 1;
                    this.totNbHit += 1;

                    try{
                        int rulesQuark = fSs.getQuarkRelative(this.fQuark, IDpdkLpmModelAttributes.LPM_RULES);
                        int ruleQuark = fSs.getQuarkRelative(rulesQuark, key.getFirst().getHostAddress().concat("/").concat(String.valueOf(depth))); //$NON-NLS-1$

                        int nbHitQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkLpmModelAttributes.NB_HIT);
                        fSs.modifyAttribute(ts, rule.nbHit, nbHitQuark);

                        int totNbMissQuark = fSs.getQuarkRelative(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_HIT);
                        fSs.modifyAttribute(ts, this.totNbHit, totNbMissQuark);
                    } catch (AttributeNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                else {
                    Activator.getInstance().logError("Matching rule not found : ip = " + ifceAddr.toString() +
                            ", masked "+ InetAddresses.fromInteger(ifceMaskedAddress).toString() +
                            ", depth = " +  String.valueOf(depth));
                }
        }
    }

    /**
     * @param ips
     * @param nextHops
     * @param ruleDepths
     * @param defaultValue
     * @param ts
     */
    public void ruleLookupX4(long[] ips, long[] nextHops, long[] ruleDepths, Integer defaultValue, long ts) {

        for(int i=0; i<ips.length; i++) {
            Inet4Address ipv4 = InetAddresses.fromInteger((int) ips[i]);
            int nextHop = (int) nextHops[i];
            int prefix = (int) ruleDepths[i];

            ruleLookup(ipv4, prefix, !((nextHop == defaultValue) && (prefix == 0)) , ts);
        }
    }
}
