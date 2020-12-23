/**********************************************************************
 * Copyright (c) 2016 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************/

package org.eclipse.tracecompass.incubator.internal.dpdk.ui.lpm.rule.hit.rate;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmPerRuleLookupHitRateDataProvider;
import org.eclipse.tracecompass.tmf.core.dataprovider.DataProviderManager;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceManager;
import org.eclipse.tracecompass.tmf.ui.viewers.TmfViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.TmfXYChartViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.linechart.TmfXYChartSettings;
import org.eclipse.tracecompass.tmf.ui.views.xychart.TmfChartView;

/**
 * Main view to show the Network Activity
 *
 * @author Adel
 */
public class DpdkLpmRuleHitRateView extends TmfChartView {

    /** ID string */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.lpm.rule.hit.rate.view"; //$NON-NLS-1$
    private static final double RESOLUTION = 0.3;
    private static final int MAX_NB_LPM_RULES = 100;

    class NumberValidator implements IInputValidator {

        @Override
        public @Nullable String isValid(@Nullable String input) {
            try {
                int i = Integer.parseInt(input);
                if (i <= 0 || i > MAX_NB_LPM_RULES) {
                    return Messages.ParameterDialog_OutOfRangeErrorMsg;
                }

            } catch (NumberFormatException x) {
                return Messages.ParameterDialog_NotNumberErrorMsg;
            }

            return null;
        }
    }

    /**
     * Constructor
     */
    public DpdkLpmRuleHitRateView() {
        super(Messages.DpdkLpmHitRateView_Title);
    }

    @Override
    protected TmfXYChartViewer createChartViewer(@Nullable Composite parent) {
        TmfXYChartSettings settings = new TmfXYChartSettings(Messages.DpdkLpmHitRateViewer_Title, Messages.DpdkLpmHitRateViewer_XAxis, Messages.DpdkLpmHitRateViewer_YAxis, RESOLUTION);
        return new DpdkLpmRuleHitRateViewer(parent, settings);
    }

    @Override
    protected @NonNull TmfViewer createLeftChildViewer(@Nullable Composite parent) {
        return new DpdkLpmRuleHitRateTreeViewer(Objects.requireNonNull(parent));
    }


    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();

        menuManager.add(new Separator());
        menuManager.add(createSetLaneAction());
    }

    private Action createSetLaneAction() {
        return new Action(Messages.ParameterDialog_Customize, IAction.AS_PUSH_BUTTON) {
            @Override
            public void run() {
                //ITmfTrace trace = getTrace();
                Shell shell = getSite().getShell();
                ParameterDialog dialog = new ParameterDialog(shell, Messages.ParameterDialog_ModifyParameter,
                        new NumberValidator());
                dialog.setBlockOnOpen(true);
                if (dialog.open() == Window.OK) {
                    int value = dialog.getRulesMaxNumberValue();
                    boolean ascendingSortOrder = dialog.isSortingOrderAscending();
                    ITmfTrace trace = TmfTraceManager.getInstance().getActiveTrace();
                    if(trace != null) {
                        DpdkLpmPerRuleLookupHitRateDataProvider provider =
                                DataProviderManager.getInstance()
                                .getDataProvider(trace, DpdkLpmPerRuleLookupHitRateDataProvider.ID, DpdkLpmPerRuleLookupHitRateDataProvider.class);
                        if(provider != null) {
                            provider.setMaxRulesNumber(value);
                            provider.setSortingOrder(ascendingSortOrder);

                            DpdkLpmRuleHitRateTreeViewer viewer = (DpdkLpmRuleHitRateTreeViewer) getLeftChildViewer();
                            viewer.updateContent(trace.getStartTime().getValue(), trace.getEndTime().getValue(), true);
                        }
                    }
                }
            }

        };
    }
}
