package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

/**
 * @author Adel Belkhiri
 *
 */
abstract public class FlowRuleModel {
    private final int fId;
    public long nbHit;

    public FlowRuleModel(int ruleId) {
        this.fId = ruleId;
        this.nbHit = 0;
    }

    public int getId() {return fId;}

    /**
     * Convert a mask to network prefix length
     * @param n
     *      IPv4 mask
     * @return
     *      Network prefix length
     */
    public int maskToPrefixLength(int n)
    {
        if(n == 0) {
            return 0;
        }

        int mask = n;
        int countZeroBits = 0;

        while ((mask & 0x1) == 0) {
            mask = mask >> 1;
            countZeroBits++;
        }
        return (32 - countZeroBits);
    }

    @Override
    abstract public String toString();

}
