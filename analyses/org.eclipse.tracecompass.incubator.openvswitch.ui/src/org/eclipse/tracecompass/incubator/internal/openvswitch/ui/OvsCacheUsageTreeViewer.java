/*******************************************************************************
 * Copyright (c) 2017, 2018 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.openvswitch.ui;

import java.util.Comparator;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.tracecompass.incubator.internal.openvswitch.core.OvsCacheUsageDataProvider;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeDataModel;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.AbstractSelectTreeViewer;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.ITmfTreeColumnDataProvider;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.TmfGenericTreeEntry;
import org.eclipse.tracecompass.tmf.ui.viewers.tree.TmfTreeColumnData;
import com.google.common.collect.ImmutableList;

/**
 * Disk IO Activity Tree Viewer
 *
 * @author adel
 */
public class OvsCacheUsageTreeViewer extends AbstractSelectTreeViewer {

    private final class NetworkTreeLabelProvider extends TreeLabelProvider {

        @Override
        public @Nullable Image getColumnImage(@Nullable Object element, int columnIndex) {
            if (columnIndex == 1 && element instanceof TmfGenericTreeEntry && isChecked(element)) {
                TmfGenericTreeEntry<TmfTreeDataModel> entry = (TmfGenericTreeEntry<TmfTreeDataModel>) element;
                if (!entry.hasChildren()) {
                    // ensures that only leaf nodes return images
                    return getLegendImage(getFullPath(entry));
                }
            }
            return null;
        }
    }

    /**
     * Constructor
     *
     * @param parent
     *            Parent composite
     */
    public OvsCacheUsageTreeViewer(Composite parent) {
        super(parent, 1, OvsCacheUsageDataProvider.ID);
        setLabelProvider(new NetworkTreeLabelProvider());
    }

    @Override
    protected ITmfTreeColumnDataProvider getColumnDataProvider() {
        return () -> {
            return ImmutableList.of(
                    createColumn(Messages.OvsCacheUsageTreeViewer_CacheName, Comparator.comparing(TmfGenericTreeEntry::getName)),
                    new TmfTreeColumnData(Messages.OvsCacheUsageTreeViewer_Legend));
        };
    }

}