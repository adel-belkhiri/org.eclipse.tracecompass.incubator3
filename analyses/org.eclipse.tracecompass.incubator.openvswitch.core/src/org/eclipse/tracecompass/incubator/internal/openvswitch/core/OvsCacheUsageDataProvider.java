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
import java.util.Objects;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.tmf.core.model.filters.FetchParametersUtils;
import org.eclipse.tracecompass.internal.tmf.core.model.xy.AbstractTreeCommonXDataProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
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


/**
 * This data provider will return a XY model (model is wrapped in a response)
 * based on a query filter. The model is used afterwards by any viewer to draw
 * charts. Model returned is for Network I/O views
 *
 * @author adel
 */
@SuppressWarnings("restriction")
public class OvsCacheUsageDataProvider extends AbstractTreeCommonXDataProvider<OpenvSwitchFlowsAnalysisModule, TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link OvsCacheUsageDataProvider}.
     */
    public static final String PROVIDER_TITLE = Objects.requireNonNull(IOpenVSwitchModelAttributes.CACHE_USAGE_DATAPROVIDER_TITLE);

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.openvswitch.core.ovs.cache.usage.data.provider"; //$NON-NLS-1$

    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link Disk}.
     */
    private static final class OvsCacheBuilder {

        //private static final double SECONDS_PER_NANOSECOND = 1E-9;
       //private static final double RATIO = 1 / SECONDS_PER_NANOSECOND;

        private final long fId;

        /** This series' sector quark. public because final */
        private final String fName;
        private final double[] fValues;

        public final int fEmcCacheQuark;
        public final int fMegaflowCacheQuark;
        public final int fMissUpcallsQuark;

        private long fPrevEmcCount;
        private long fPrevMegaflowCount;
        private long fPrevMissUpcallsCount;

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
        private OvsCacheBuilder(long id, String name, int length, int emcQuark, int megaflowQuark, int missUpcallsQuark) {
            this.fId = id;
            this.fEmcCacheQuark = emcQuark;
            this.fMegaflowCacheQuark = megaflowQuark;
            this.fMissUpcallsQuark = missUpcallsQuark;

            this.fName = name;
            this.fValues = new double[length];

            this.fPrevEmcCount = 0;
            this.fPrevMegaflowCount = 0;
            this.fPrevMissUpcallsCount = 0;

        }

        private void setPrevCount(long prevEmcCount, long prevMegaflowCount, long prevMissUpcallsCount) {
            this.fPrevEmcCount = prevEmcCount;
            this.fPrevMegaflowCount = prevMegaflowCount;
            this.fPrevMissUpcallsCount = prevMissUpcallsCount;
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
        private void updateValue(int pos, long newEmcCount, long newMegaflowCount, long newMissUpcallsCount) {


            /* Compute the usage percentage */
            long emcCount = newEmcCount - this.fPrevEmcCount;
            long megaflowCount = newMegaflowCount - this.fPrevMegaflowCount;
            long missUpcallsCount = newMissUpcallsCount - this.fPrevMissUpcallsCount;

            try {
                if(this.fId == fEmcCacheQuark) {
                    fValues[pos] = emcCount * 100 /*percentage*/ / (emcCount + megaflowCount + missUpcallsCount);
                }
                else if (this.fId == fMegaflowCacheQuark) {
                        fValues[pos] = megaflowCount * 100 / (emcCount + megaflowCount + missUpcallsCount);
                }
            }
            catch (ArithmeticException e) { //Division by zero
                fValues[pos] = 0;
            }

            /* Setting previous values */
            this.fPrevEmcCount = newEmcCount;
            this.fPrevMegaflowCount = newMegaflowCount;
            this.fPrevMissUpcallsCount = newMissUpcallsCount;

        }

        public IYModel build() {
           return new YModel(fId, fName, fValues);
        }
    }


    /**
     * Create an instance of {@link OvsCacheUsageDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link OvsCacheUsageDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static @Nullable OvsCacheUsageDataProvider create(ITmfTrace trace) {
        OpenvSwitchFlowsAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, OpenvSwitchFlowsAnalysisModule.class, OpenvSwitchFlowsAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new OvsCacheUsageDataProvider(trace, module);
        }
        return null;
    }

    /**
     * Constructor
     */
    private OvsCacheUsageDataProvider(ITmfTrace trace, OpenvSwitchFlowsAnalysisModule module) {
        super(trace, module);
    }

    @Override
    public String getId() {
        return ID;
    }


    @Override
    protected boolean isCacheable() {
        return true;
    }

    @Override
    protected String getTitle() {
        return PROVIDER_TITLE;
    }

    /**
     *
     */

    @Override
    protected TmfTreeModel<TmfTreeDataModel>  getTree(ITmfStateSystem ss, Map<String, Object> parameters, @Nullable IProgressMonitor monitor) {
        List<TmfTreeDataModel> nodes = new ArrayList<>();
        int emcQuark, megaflowQuark;
        long rootId;

        rootId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        String traceName = Objects.requireNonNull(getTrace().getName());
        nodes.add(new TmfTreeDataModel(rootId, -1, traceName));

        /* are the quarks there in the state system ? */
        try {
            emcQuark = ss.getQuarkAbsolute(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_EMC_CACHE_HIT);
            megaflowQuark = ss.getQuarkAbsolute(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_MEGAFLOW_CACHE_HIT);
        }
        catch(AttributeNotFoundException e) {
            e.printStackTrace();
            return new TmfTreeModel<>(Collections.emptyList(), nodes);
        }

        /* create two series */
        String emcCacheName = Objects.requireNonNull(IOpenVSwitchModelAttributes.EMC_CACHE);
        String megaflowCacheName = Objects.requireNonNull(IOpenVSwitchModelAttributes.MEGAFLOW_CACHE);
        nodes.add(new TmfTreeDataModel(getId(emcQuark), rootId, emcCacheName));
        nodes.add(new TmfTreeDataModel(getId(megaflowQuark), rootId, megaflowCacheName));

        return new TmfTreeModel<>(Collections.emptyList(), nodes);
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
        long currentEnd = ss.getCurrentEndTime();

        List<OvsCacheBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            // this would return an empty map even if we did the queries.
            return Collections.emptyList();
        }

        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (OvsCacheBuilder entry : builders) {
                long prevEmcCount = extractCount(entry.fEmcCacheQuark, states);
                long prevMegaflowCount = extractCount(entry.fMegaflowCacheQuark, states);
                long prevMissUpcallsCount = extractCount(entry.fMissUpcallsQuark, states);

                entry.setPrevCount(prevEmcCount, prevMegaflowCount, prevMissUpcallsCount);
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

                for (OvsCacheBuilder entry : builders) {
                    long newEmcHitcount = extractCount(entry.fEmcCacheQuark, states);
                    long newMegaflowHitcount = extractCount(entry.fMegaflowCacheQuark, states);
                    long newMissUpcallsCount = extractCount(entry.fMissUpcallsQuark, states);

                    //long observationPeriod = time - prevTime;
                    entry.updateValue(i, newEmcHitcount, newMegaflowHitcount, newMissUpcallsCount);
                }
            }
            prevTime = time;
        }

        return Maps.uniqueIndex(Iterables.transform(builders, OvsCacheBuilder::build), IYModel::getName).values();
    }

    /**
     *
     * @param ss
     * @param filter
     * @return
     */
    private List<OvsCacheBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        List<OvsCacheBuilder> builders = new ArrayList<>();
        int emcQuark, megaflowQuark, missUpcallsQuark;

        int length = filter.getTimesRequested().length;


        /* are the quarks there in the state system ? */
        try {
            emcQuark = ss.getQuarkAbsolute(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_EMC_CACHE_HIT);
            megaflowQuark = ss.getQuarkAbsolute(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_MEGAFLOW_CACHE_HIT);
            missUpcallsQuark = ss.getQuarkAbsolute(IOpenVSwitchModelAttributes.STATISTICS, IOpenVSwitchModelAttributes.NB_MISS_UPCALLS);

        }
        catch(AttributeNotFoundException e) {
            //e.printStackTrace();
            return builders;
        }

        for (Entry<Long, Integer> entry : getSelectedEntries(filter).entrySet()) {
            //long id = entry.getKey();
            int quark = entry.getValue();

            if (ss.getAttributeName(quark).equals(IOpenVSwitchModelAttributes.NB_EMC_CACHE_HIT)) {
                String emcSerieName = getTrace().getName() + '/' + IOpenVSwitchModelAttributes.EMC_CACHE;
                builders.add(new OvsCacheBuilder(quark, emcSerieName, length, emcQuark, megaflowQuark, missUpcallsQuark));
            } else if (ss.getAttributeName(quark).equals(IOpenVSwitchModelAttributes.NB_MEGAFLOW_CACHE_HIT)) {
                String megaflowSeriename = getTrace().getName() + '/' + IOpenVSwitchModelAttributes.MEGAFLOW_CACHE;
                builders.add(new OvsCacheBuilder(quark, megaflowSeriename, length, emcQuark, megaflowQuark, missUpcallsQuark));
            }
        }

        return builders;
    }

    /**
     * @param cacheQuark xx
     * @param states Sates
     * @return xx
     */
    public static long extractCount(int cacheQuark, List<ITmfStateInterval> states) {

        // Get the initial value.
        Object stateValue = states.get(cacheQuark).getValue();
        long nbHit = (stateValue instanceof Number) ? ((Number) stateValue).longValue() : 0;

        return nbHit;
    }

}
