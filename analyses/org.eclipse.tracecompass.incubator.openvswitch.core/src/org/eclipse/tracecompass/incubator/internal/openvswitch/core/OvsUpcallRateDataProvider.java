/**********************************************************************
 * Copyright (c) 2017 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************/

package org.eclipse.tracecompass.incubator.internal.openvswitch.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.tmf.core.model.filters.FetchParametersUtils;
import org.eclipse.tracecompass.internal.tmf.core.model.xy.AbstractTreeCommonXDataProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.StateSystemUtils;
import org.eclipse.tracecompass.statesystem.core.exceptions.StateSystemDisposedException;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.tmf.core.model.YModel;
import org.eclipse.tracecompass.tmf.core.model.filters.SelectionTimeQueryFilter;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeDataModel;
import org.eclipse.tracecompass.tmf.core.model.tree.TmfTreeModel;
import org.eclipse.tracecompass.tmf.core.model.xy.IYModel;
import org.eclipse.tracecompass.tmf.core.trace.ITmfTrace;
import org.eclipse.tracecompass.tmf.core.trace.TmfTraceUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
//import com.google.common.base.Function;
/**
 * This data provider will return a XY model (model is wrapped in a response)
 * based on a query filter. The model is used afterwards by any viewer to draw
 * charts. Model returned is for Network I/O views
 *
 * @author adel
 */
@SuppressWarnings("restriction")
public class OvsUpcallRateDataProvider extends AbstractTreeCommonXDataProvider<OpenvSwitchDatapathInterfaceAnalysisModule, TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link OvsUpcallRateDataProvider}.
     */
    public static final String PROVIDER_TITLE = Objects.requireNonNull(IOpenVSwitchModelAttributes.UPCALL_RATE_DATAPROVIDER_TITLE);

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.openvswitch.core.upcall.rate.data.provider"; //$NON-NLS-1$

    /**
     * Inline class to encapsulate all the values required to build a series.
     */
    private static final class VirtualPortBuilder {

        private static final double SECONDS_PER_NANOSECOND = 1E-9;
        private static final double RATIO = 1 / SECONDS_PER_NANOSECOND;

        private final long fId;

        /** This series' upcall quark. public because final */
        public final int fMeasuredQuark;

        private final String fName;
        private final double[] fValues;
        private double fPrevCount;

        /**
         * Constructor
         *
         * @param name
         *            the series name
         * @param sectorQuark
         *            sector quark
         * @param length
         *            desired length of the series
         */
        private VirtualPortBuilder(long id, int sentRecvQuark, String name, int length) {
            fId = id;
            fMeasuredQuark = sentRecvQuark;
            fName = name;
            fValues = new double[length];
        }

        private void setPrevCount(double prevCount) {
           fPrevCount = prevCount;
        }

        /**
         * Update the value for the counter at the desired index. Use in increasing
         * order of position
         *
         * @param pos
         *            index to update
         * @param newCount
         *            new received or sent bytes
         * @param deltaT
         *            time difference to the previous value for interpolation
         */
        private void updateValue(int pos, double newCount, long deltaT) {
            /**
             * Linear interpolation to compute the disk throughput between time and the
             * previous time, from the number of sectors at each time.
             */
            fValues[pos] = (newCount - fPrevCount) * RATIO / deltaT;
            fPrevCount = newCount;
        }

        public IYModel build() {
           return new YModel(fId, fName, fValues);
        }

    }


    /**
     * Create an instance of {@link OvsUpcallRateDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link OvsUpcallRateDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static @Nullable OvsUpcallRateDataProvider create(ITmfTrace trace) {
        OpenvSwitchDatapathInterfaceAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, OpenvSwitchDatapathInterfaceAnalysisModule.class, OpenvSwitchDatapathInterfaceAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new OvsUpcallRateDataProvider(trace, module);
        }
        return null;
    }

    /**
     * Constructor
     */
    private OvsUpcallRateDataProvider(ITmfTrace trace, OpenvSwitchDatapathInterfaceAnalysisModule module) {
        super(trace, module);
    }

    /**
     *
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     *
     */

    @Override
    protected TmfTreeModel<TmfTreeDataModel>  getTree(ITmfStateSystem ss, Map<String, Object> parameters, @Nullable IProgressMonitor monitor) {
        List<TmfTreeDataModel> nodes = new ArrayList<>();

        long rootId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        nodes.add(new TmfTreeDataModel(rootId, -1, getTrace().getName()));

        /* browse vports list */
        for (Integer portQuark : ss.getQuarks(IOpenVSwitchModelAttributes.VPORTS, "*")) { //$NON-NLS-1$
            /* browse upcalls list */
            int missUpcallQuark = ss.optQuarkRelative(portQuark, IOpenVSwitchModelAttributes.NB_MISS_UPCALLS);
            int userspaceUpcallQuark = ss.optQuarkRelative(portQuark, IOpenVSwitchModelAttributes.NB_USERSPACE_UPCALLS);

            if ((missUpcallQuark != ITmfStateSystem.INVALID_ATTRIBUTE) && (userspaceUpcallQuark != ITmfStateSystem.INVALID_ATTRIBUTE)){
                String portName = getQuarkValue(ss, portQuark);
                long portId = getId(portQuark);
                nodes.add(new TmfTreeDataModel(portId, rootId, portName));

                String missUpcallSerieName = ss.getAttributeName(missUpcallQuark);
                long nbMissUpcallsId = getId(missUpcallQuark);
                nodes.add(new TmfTreeDataModel(nbMissUpcallsId, portId, missUpcallSerieName));

                long nbUserspaceUpcallsId = getId(userspaceUpcallQuark);
                String userspaceUpcallSerieName = ss.getAttributeName(userspaceUpcallQuark);
                nodes.add(new TmfTreeDataModel(nbUserspaceUpcallsId, portId, userspaceUpcallSerieName));
            }
        }
        return new TmfTreeModel<>(Collections.emptyList(), nodes);
    }

    /**
     *
     * @param ss
     * @param nicQuark
     * @return
     */
    private static String getQuarkValue(ITmfStateSystem ss, Integer nicQuark) {
        ITmfStateInterval interval = StateSystemUtils.queryUntilNonNullValue(ss, nicQuark, ss.getStartTime(), ss.getCurrentEndTime());
        if (interval != null) {
            return String.valueOf(interval.getValue());
        }
        return (ss.getAttributeName(nicQuark));
    }


    @Override
    @Deprecated
    protected @Nullable Map<String, IYModel> getYModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        return Maps.uniqueIndex(getYSeriesModels(ss, fetchParameters, monitor), IYModel::getName);
    }

    /**
     *
     */
    @Override
    protected @Nullable Collection<IYModel> getYSeriesModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor)
            throws StateSystemDisposedException {
        SelectionTimeQueryFilter filter = FetchParametersUtils.createSelectionTimeQuery(fetchParameters);
        if (filter == null) {
            return null;
        }
        long[] xValues = filter.getTimesRequested();
        List<VirtualPortBuilder> builders = initBuilders(ss, filter);

        if (builders.isEmpty()) {
            // this would return an empty map even if we did the queries.
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (VirtualPortBuilder entry : builders) {
                entry.setPrevCount(extractCount(entry.fMeasuredQuark, states));
            }
        }

        for (int i = 1; i < xValues.length; i++) {
            if (monitor != null && monitor.isCanceled()) {
                return null;
            }
            long time = xValues[i];
            if (time > currentEnd) {
                break;
            }
            else
                if (time >= ss.getStartTime()) {
                // reuse the results from the full query
                List<ITmfStateInterval> states = ss.queryFullState(time);

                for (VirtualPortBuilder entry : builders) {
                    double count = extractCount(entry.fMeasuredQuark, states);
                    long observationPeriod = time - prevTime;
                    entry.updateValue(i, count, observationPeriod);
                }
            }
            prevTime = time;
        }

        return Maps.uniqueIndex(Iterables.transform(builders, VirtualPortBuilder::build), IYModel::getName).values();
    }

    /**
     *
     * @param ss
     * @param filter
     * @return
     */
    private List<VirtualPortBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        int length = filter.getTimesRequested().length;
        List<VirtualPortBuilder> builders = new ArrayList<>();

        for (Entry<Long, Integer> entry : getSelectedEntries(filter).entrySet()) {
            long id = entry.getKey();
            int quark = entry.getValue();

            String attributeName = ss.getAttributeName(quark);
            if (attributeName.equals(IOpenVSwitchModelAttributes.NB_MISS_UPCALLS) ||
                    (attributeName.equals(IOpenVSwitchModelAttributes.NB_USERSPACE_UPCALLS))) {
                int portQuark = ss.getParentAttributeQuark(quark);
                String portName = getQuarkValue(ss, portQuark);

                String name = getTrace().getName() + '/' + portName + '/' + attributeName;
                builders.add(new VirtualPortBuilder(id, quark, name, length));
            }
        }

        return builders;
    }

    /**
     * @param recvSentQuark Quark
     * @param states Sates
     * @return xx
     */
    public static double extractCount(int recvSentQuark, List<ITmfStateInterval> states) {

        // Get the initial value.
        Object stateValue = states.get(recvSentQuark).getValue();
        double count = (stateValue instanceof Number) ? ((Number) stateValue).doubleValue() : 0.0;

        return count;
    }

    @Override
    protected boolean isCacheable() {
        return true;
    }

    @Override
    protected String getTitle() {
        return PROVIDER_TITLE;
    }
}
