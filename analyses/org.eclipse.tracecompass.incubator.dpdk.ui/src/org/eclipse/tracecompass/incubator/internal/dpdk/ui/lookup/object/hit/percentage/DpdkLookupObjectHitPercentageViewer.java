/**********************************************************************
 * Copyright (c) 2016 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************/

package org.eclipse.tracecompass.incubator.internal.dpdk.ui.lookup.object.hit.percentage;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis.DpdkLpmObjectHitPercentageDataProvider;
import org.eclipse.tracecompass.tmf.core.model.OutputElementStyle;
import org.eclipse.tracecompass.tmf.core.model.StyleProperties;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.linechart.TmfFilteredXYChartViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.xychart.linechart.TmfXYChartSettings;
import org.eclipse.swtchart.Chart;

/**
 * Network Activity viewer, shows read and write bandwidth used over time.
 *
 * @author Adel Belkhiri
 */
public class DpdkLookupObjectHitPercentageViewer extends TmfFilteredXYChartViewer {

    private static final int DEFAULT_SERIES_WIDTH = 2;

    /**
     * Constructor
     *
     * @param parent
     *            parent view
     * @param settings
     *            See {@link TmfXYChartSettings} to know what it contains
     */
    public DpdkLookupObjectHitPercentageViewer(@Nullable Composite parent, TmfXYChartSettings settings) {
        super(parent, settings, DpdkLpmObjectHitPercentageDataProvider.ID);
        Chart chart = getSwtChart();
        chart.getLegend().setPosition(SWT.LEFT);
    }

    @Override
    public OutputElementStyle getSeriesStyle(@NonNull Long seriesId) {
        return getPresentationProvider().getSeriesStyle(seriesId, StyleProperties.SeriesType.AREA, DEFAULT_SERIES_WIDTH);
    }
}