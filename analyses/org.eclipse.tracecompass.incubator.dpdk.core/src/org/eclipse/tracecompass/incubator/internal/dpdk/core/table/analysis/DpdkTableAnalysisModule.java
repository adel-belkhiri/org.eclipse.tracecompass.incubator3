/*******************************************************************************
 * Copyright (c) 2016 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import org.eclipse.tracecompass.incubator.internal.dpdk.core.table.eventhandlers.DpdkTableAnalysisEventLayout;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceClosedSignal;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTrace;

/**
 * Analysis Module for DPDK Tables (Hash, ACL and ..)
 *
 * @author Adel Belkhiri
 */
public class DpdkTableAnalysisModule extends TmfStateSystemAnalysisModule {

    /** The ID of this analysis module */
    public static final String ID = "org.eclipse.tracecompass.incubator.dpdk.tables.analysis"; //$NON-NLS-1$

    @Override
    protected ITmfStateProvider createStateProvider() {
        ITmfTrace trace = checkNotNull(getTrace());

        if (trace instanceof TmfTrace) {
            return new DpdkTableStateProvider((TmfTrace)trace, new DpdkTableAnalysisEventLayout(), ID);
        }

        throw new IllegalStateException();
    }

    @Override
    protected StateSystemBackendType getBackendType() {
        return StateSystemBackendType.FULL;
    }

    @Override
    @TmfSignalHandler
    public void traceClosed(TmfTraceClosedSignal signal) {
        super.traceClosed(signal);
        if (signal.getTrace() == getTrace()) {
            dispose();
        }
    }

}
