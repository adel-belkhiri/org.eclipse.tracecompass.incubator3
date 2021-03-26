/*******************************************************************************
 * Copyright (c) 2016 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.dpdk.ui.eventdev.enqueue.dequeue.rate;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;


/**
 * Provides a formatter for data sizes along with the unit of size (KG, MB, GB
 * ou TB). It receives a size in bytes and it formats a number in the closest
 * thousand's unit, with at most 3 decimals.
 *
 * @author Matthew Khouzam
 * @since 2.0
 */
@SuppressWarnings("null")
public class DataTransferSpeedWithUnitFormat extends Format {

    private static final Format INSTANCE = new DataTransferSpeedWithUnitFormat();

    private static final long serialVersionUID = 3934127385682676804L;
    private static final String K = "K"; //$NON-NLS-1$
    private static final String M = "M"; //$NON-NLS-1$
    private static final String G = "G"; //$NON-NLS-1$
    private static final String T = "T"; //$NON-NLS-1$
    private static final long KILO = 1000;
    private static final Format FORMAT = new DecimalFormat("#.###"); //$NON-NLS-1$

    /**
     * Protected constructor
     */
    protected DataTransferSpeedWithUnitFormat() {
        super();
    }

    /**
     * Returns the instance of this formatter
     *
     * @return The instance of this formatter
     */
    public static Format getInstance() {
        return INSTANCE;
    }


    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof Number) {
            Number num = (Number) obj;
            double value = num.doubleValue();
            double abs = Math.abs(value);
            if (value == 0) {
                return toAppendTo.append("0"); //$NON-NLS-1$
            }
            if (abs >= KILO * KILO * KILO * KILO) {
                return toAppendTo.append(FORMAT.format(value / (KILO * KILO * KILO * KILO))).append(' ').append(T);
            }
            if (abs >= KILO * KILO * KILO) {
                return toAppendTo.append(FORMAT.format(value / (KILO * KILO * KILO))).append(' ').append(G);
            }
            if (abs >= KILO * KILO) {
                return toAppendTo.append(FORMAT.format(value / (KILO * KILO))).append(' ').append(M);
            }
            if (abs >= KILO) {
                return toAppendTo.append(FORMAT.format(value / (KILO))).append(' ').append(K);
            }
            return toAppendTo.append(FORMAT.format(value)).append(' ');
        }
        return toAppendTo.append(obj);
    }

    /**
     * @since 2.2
     */
   @Override
   public Number parseObject(String source, ParsePosition pos) {
        Number number = NumberFormat.getInstance().parse(source, pos);
        if (number == null) {
            return null;
        }
        String unit = source.substring(pos.getIndex()).trim().toUpperCase();
        long multiplier = 1;
        if (!unit.isEmpty()) {
            if (unit.startsWith(K)) {
                multiplier = KILO;
            } else if (unit.startsWith(M)) {
                multiplier = KILO * KILO;
            } else if (unit.startsWith(G)) {
                multiplier = KILO * KILO * KILO;
            } else if (unit.startsWith(T)) {
                multiplier = KILO * KILO * KILO * KILO;
            }
        }
        if (multiplier != 1 && Double.isFinite(number.doubleValue())) {
            BigDecimal bd = new BigDecimal(number.toString());
            bd = bd.multiply(BigDecimal.valueOf(multiplier));
            if (bd.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO) &&
                    bd.abs().compareTo(new BigDecimal(Long.MAX_VALUE)) < 0) {
                return bd.longValue();
            }
            return bd.doubleValue();
        }
        return number;
    }

}
