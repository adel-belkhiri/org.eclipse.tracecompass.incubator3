package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public enum SlowPathReasonMask {
    SLOW_UNDEFINED(0),
    SLOW_CFM(1),
    SLOW_BFD(2),
    SLOW_LACP(4),
    SLOW_STP (8),
    SLOW_LLDP (16),
    SLOW_ACTION(32),
    SLOW_MATCH (64);

    /**
     *     SLOW_CFM,        "cfm",
    SLOW_BFD,        "bfd",
    SLOW_LACP,       "lacp",
    SLOW_STP,        "stp",
    SLOW_LLDP,       "lldp",
    SLOW_ACTION,     "action",
    SLOW_MATCH,      "match";
     */
    private int value;
    SlowPathReasonMask(int value){
        this.value = value;
    }

    private static final Map<Integer, SlowPathReasonMask> _map = new HashMap<>();
    static
    {
        for (SlowPathReasonMask reason : SlowPathReasonMask.values()) {
            _map.put(reason.value, reason);
        }
    }

    static String getReasons(Integer reason) {
        String s = ""; //$NON-NLS-1$
        for(Entry<Integer, SlowPathReasonMask> entry : _map.entrySet()) {
            if((entry.getKey() & reason) != 0) {
                s = s + entry.getValue().toString() + " "; //$NON-NLS-1$
            }
        }
        return s;
    }
}
