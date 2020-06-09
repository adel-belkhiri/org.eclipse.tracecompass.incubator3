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
import org.eclipse.tracecompass.statesystem.core.exceptions.StateValueTypeException;
import org.eclipse.tracecompass.tmf.core.util.Pair;

import com.google.common.net.InetAddresses;

/**
 * @author  Adel Belkhiri
 *
 */
public class LpmTableModel {

    /* Bitmask used to indicate successful lookup */
    private final int RTE_LPM_LOOKUP_SUCCESS = 0x01000000;

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
        private final Pair<Inet4Address, Integer> fId;
        private final int fNextHop;
        private final int fRuleQuark;
        private long nbHit;

        public RuleModel(Inet4Address ipv4, int depth, int nextHop, int quark) {
            this.fId = new Pair<>(ipv4, depth);
            this.fNextHop = nextHop;
            this.fRuleQuark = quark;

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
     *
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
     *
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

        if(!fRules.containsKey(new Pair<>(addr, depth))) {
            int rulesQuark = fSs.getQuarkRelativeAndAdd(this.fQuark, IDpdkLpmModelAttributes.LPM_RULES);
            int ruleQuark = fSs.getQuarkRelativeAndAdd(rulesQuark, addr.getHostAddress().concat("/").concat(String.valueOf(depth))); //$NON-NLS-1$

            int nextHopQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkLpmModelAttributes.NEXT_HOP);
            fSs.modifyAttribute(ts, nextHop, nextHopQuark);

            int nbHitQuark = fSs.getQuarkRelativeAndAdd(ruleQuark, IDpdkLpmModelAttributes.NB_HIT);
            fSs.modifyAttribute(ts, 0L, nbHitQuark);

            RuleModel rule = new RuleModel(addr, depth, nextHop, ruleQuark);
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

                int nextHopQuark = fSs.getQuarkRelative(rule.fRuleQuark, IDpdkLpmModelAttributes.NEXT_HOP);
                fSs.modifyAttribute(ts, (Integer) null, nextHopQuark);

                int nbHitQuark = fSs.getQuarkRelative(rule.fRuleQuark, IDpdkLpmModelAttributes.NB_HIT);
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
            RuleModel rule = entry.getValue();

            try {

                int nextHopQuark = fSs.getQuarkRelative(rule.fRuleQuark, IDpdkLpmModelAttributes.NEXT_HOP);
                fSs.modifyAttribute(ts, (Integer) null, nextHopQuark);

                int nbHitQuark = fSs.getQuarkRelative(rule.fRuleQuark, IDpdkLpmModelAttributes.NB_HIT);
                fSs.modifyAttribute(ts,  (Long) null, nbHitQuark);
            }
            catch(AttributeNotFoundException e) {
                Activator.getInstance().logError(e.toString());
            }

            it.remove();
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

    /**
     * Transform network prefix to a mask
     */
    public static int depthToMask(int depth)
    {
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
                fSs.modifyAttribute(ts, this.totNbMiss, totNbHitQuark);
            } catch (AttributeNotFoundException |  StateValueTypeException e) {
                e.printStackTrace();
            }
        }
        else {
                Integer ifceMaskedAddress = InetAddresses.coerceToInteger(ifceAddr) & depthToMask(depth);
                Pair<Inet4Address, Integer> key = new Pair<>(InetAddresses.fromInteger(ifceMaskedAddress), depth);
                RuleModel rule = fRules.get(key);

                if(rule != null) {
                    rule.nbHit += 1;
                    this.totNbHit += 1;

                    try{
                        int nbHitQuark = fSs.getQuarkRelativeAndAdd(rule.fRuleQuark, IDpdkLpmModelAttributes.NB_HIT);
                        fSs.modifyAttribute(ts, rule.nbHit, nbHitQuark);

                        int totNbMissQuark = fSs.getQuarkRelative(this.fQuark, IDpdkLpmModelAttributes.TOT_NB_HIT);
                        fSs.modifyAttribute(ts, this.totNbHit, totNbMissQuark);
                    } catch (AttributeNotFoundException | StateValueTypeException e) {
                        e.printStackTrace();
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
     * Performs a x4 batch lookup.
     *
     * @param ips
     *      an array of 4 IP addresses
     * @param nextHops
     *      an array holding the next hop values corresponding to IP addresses
     * @param ruleDepths
     * @param defaultValue
     *      Default value to set in case the lookup results in a miss
     * @param ts
     *      Timestamp
     */
    public void ruleLookupX4(long[] ips, long[] nextHops, long[] ruleDepths, Integer defaultValue, long ts) {

        for(int i=0; i < ips.length; i++) {
            Inet4Address ipv4 = InetAddresses.fromInteger((int) ips[i]);
            int nextHop = (int) nextHops[i];
            int prefix = (int) ruleDepths[i];

            ruleLookup(ipv4, prefix, !((nextHop == defaultValue) && (prefix == 0)) , ts);
        }
    }

    /**
     * Performs a bulk lookup.
     *
     * @param ips
     *      an array of 'n' IP addresses
     * @param tblEntries
     *      The entries matching with the IP addresses in the LPM table
     * @param n
     *      Number of IP addresses
     * @param ts
     *      Timestamp
     */
    public void ruleLookupBulk(long[] ips, long[] tblEntries, Integer n, long ts) {

        for(int i=0; i < n; i++) {

            Inet4Address ipv4 = InetAddresses.fromInteger((int) ips[i]);
            int entry = (int) tblEntries[i];

            /* The first 6 bits encode the network prefix  */
            int prefix = (entry & 0xFC000000) >> 26;
            boolean successfulLookup = (entry & RTE_LPM_LOOKUP_SUCCESS ) != 0 ;

            ruleLookup(ipv4, prefix, successfulLookup && (prefix != 0), ts);
        }
    }
}
