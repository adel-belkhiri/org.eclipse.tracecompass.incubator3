/*******************************************************************************
 * Copyright (c) 2016 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import static org.eclipse.tracecompass.common.core.NonNullUtils.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.tracecompass.analysis.os.linux.core.tid.TidAnalysisModule;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.eventhandlers.IOpenvSwitchAnalysisEventLayout;
import org.eclipse.tracecompass.tmf.core.analysis.IAnalysisModule;
import org.eclipse.tracecompass.tmf.core.signal.TmfSignalHandler;
import org.eclipse.tracecompass.tmf.core.signal.TmfTraceClosedSignal;
import org.eclipse.tracecompass.tmf.core.statesystem.ITmfStateProvider;
import org.eclipse.tracecompass.tmf.core.statesystem.TmfStateSystemAnalysisModule;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;
import org.eclipse.tracecompass.tmf.core.trace.experiment.TmfExperiment;

/**
 * State System Module for OpenvSwitch Dpif interface
 *
 * @author Adel Belkhiri
 */
public class OpenvSwitchRevalidationAnalysisModule extends TmfStateSystemAnalysisModule {

    /** The ID of this analysis module */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.openvswitch.core.revalidation.analysis"; //$NON-NLS-1$

    @Override
    protected ITmfStateProvider createStateProvider() {
        ITmfTrace trace = checkNotNull(getTrace());

        if (trace instanceof TmfExperiment) {
            return new OvsRevalidationStateProvider((TmfExperiment) trace, new IOpenvSwitchAnalysisEventLayout(), ID);
        }

        throw new IllegalStateException();
    }

    @Override
    protected StateSystemBackendType getBackendType() {
        return StateSystemBackendType.FULL;
    }

    @Override
    protected Iterable<IAnalysisModule> getDependentAnalyses() {
        Set<IAnalysisModule> modules = new HashSet<>();

        ITmfTrace trace = getTrace();
        if (trace == null) {
            throw new IllegalStateException();
        }
        /*
         * This analysis depends on the LTTng kernel analysis, so it's added to
         * dependent modules.
         */
        Iterable<TidAnalysisModule> tidModules = TmfTraceUtils.getAnalysisModulesOfClass(trace, TidAnalysisModule.class);
        for (TidAnalysisModule tidModule : tidModules) {
            /* Only add the first one we find, if there is one */
            modules.add(tidModule);
            break;
        }
        return modules;
    }

    @Override
    @TmfSignalHandler
    public void traceClosed(TmfTraceClosedSignal signal) {
        super.traceClosed(signal);
        // Dispose of this analysis, since it is not linked to the trace it will not be
        // automatic
        if (signal.getTrace() == getTrace()) {
            dispose();
        }
    }

}
