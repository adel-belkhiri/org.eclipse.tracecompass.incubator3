package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.tracecompass.internal.tmf.core.model.filters.FetchParametersUtils;
import org.eclipse.tracecompass.internal.tmf.core.model.xy.AbstractTreeCommonXDataProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystem;
import org.eclipse.tracecompass.statesystem.core.StateSystemUtils;
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
 * LPM Hit/Miss Ratio DataProvider. This data provider will return a XY model (model is wrapped in a response)
 * based on a query filter. The model is used afterwards by any viewer to draw charts.
 *
 * @author  Adel Belkhiri
 */

@SuppressWarnings("restriction")
public class DpdkLpmObjectHitPercentageDataProvider extends AbstractTreeCommonXDataProvider<@NonNull DpdkLpmObjectLookupAnalysisModule, @NonNull TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link DpdkLpmObjectHitPercentageDataProvider}.
     */
    public static final String PROVIDER_TITLE = IDpdkLpmModelAttributes.LPM_LOOKUP_DATA_PROVIDER_TITLE;

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.dpdk.lpm.object.lookup.hit.miss.ratio.data.provider"; //$NON-NLS-1$

    /**
     * Inline class to encapsulate all the values required to build a series.
     */
    private static final class LpmTablesBuilder {

        private static final double CENT = 100;

        private final long fId;

        public final int fMeasuredQuark;
        public final int fSecondMeasuredQuark;

        private final String fName;
        private final double[] fValues;
        private long fFirstPrevCount;
        private long fSeconfPrevCount;

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
        private LpmTablesBuilder(long id, int firstQuark, int secondQuark, String name, int length) {
            fId = id;
            fMeasuredQuark = firstQuark;
            fSecondMeasuredQuark = secondQuark;
            fName = name;
            fValues = new double[length];
        }

        private void setPrevCount(long firstPrevCount, long secondPrevCount) {
            fFirstPrevCount = firstPrevCount;
            fSeconfPrevCount = secondPrevCount;
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
        private void updateValue(int pos, long firstNewCount, long secondNewCount, long deltaT) {
            /**
             * Linear interpolation to compute the disk throughput between time and the
             * previous time, from the number of sectors at each time.
             */

            long firstValue = firstNewCount - fFirstPrevCount;
            long secondValue = secondNewCount - fSeconfPrevCount;
            long sumValues = firstValue + secondValue;

            fValues[pos] = (sumValues == 0) ? 0 : (firstValue) * CENT / sumValues;

            fFirstPrevCount = firstNewCount;
            fSeconfPrevCount = secondNewCount;
        }

        public IYModel build() {
           return new YModel(fId, fName, fValues);
        }
    }

    /**
     * Constructor
     */
    private DpdkLpmObjectHitPercentageDataProvider(@NonNull ITmfTrace trace, @NonNull DpdkLpmObjectLookupAnalysisModule module) {
        super(trace, module);
    }

    /**
     * Create an instance of {@link DpdkLpmObjectHitPercentageDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link DpdkLpmObjectHitPercentageDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static DpdkLpmObjectHitPercentageDataProvider create(ITmfTrace trace) {
        DpdkLpmObjectLookupAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkLpmObjectLookupAnalysisModule.class, DpdkLpmObjectLookupAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new DpdkLpmObjectHitPercentageDataProvider(trace, module);
        }
        return null;
    }

    /**
     * Get the ID of this DataProvider
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * GetTree method
     */

    @SuppressWarnings("nls")
    @Override
    protected TmfTreeModel<@NonNull TmfTreeDataModel> getTree(ITmfStateSystem ss, Map<String, Object> parameters,
            @Nullable IProgressMonitor monitor) {
        List<TmfTreeDataModel> nodes = new ArrayList<>();

        long rootId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        nodes.add(new TmfTreeDataModel(rootId, -1, getTrace().getName()));

        /* browse LPM lookup objects */
        List<@NonNull Integer> lookupObjQuarks = new ArrayList<>() ;
        lookupObjQuarks.addAll(ss.getQuarks(IDpdkLpmModelAttributes.LPM_OBJS, "*"));

        for (Integer objQuark : lookupObjQuarks) {
            String objName = getQuarkValue(ss, objQuark);
            long tabId = getId(objQuark);
            nodes.add(new TmfTreeDataModel(tabId, rootId, objName));

            int totNbHitQuark = ss.optQuarkRelative(objQuark, IDpdkLpmModelAttributes.TOT_NB_HIT);
            if (totNbHitQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                long firstMetricId = getId(totNbHitQuark);
                nodes.add(new TmfTreeDataModel(firstMetricId, tabId, IDpdkLpmModelAttributes.HIT_PERCENT_METRIC_LABEL ));
            }

            int totNbMissQuark = ss.optQuarkRelative(objQuark, IDpdkLpmModelAttributes.TOT_NB_MISS);
            if (totNbMissQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                long secondMetricId = getId(totNbMissQuark);
                nodes.add(new TmfTreeDataModel(secondMetricId, tabId, IDpdkLpmModelAttributes.MISS_PERCENT_METRIC_LABEL));
            }
        }

        return new TmfTreeModel<>(Collections.emptyList(), nodes);
    }

    /**
     * Return the value corresponding to a quark.
     *
     * @param ss
     *      ITmfStateSystem
     * @param quark
     *      Integer
     * @return
     */
    private static String getQuarkValue(ITmfStateSystem ss, Integer quark) {
        ITmfStateInterval interval = StateSystemUtils.queryUntilNonNullValue(ss, quark, ss.getStartTime(), ss.getCurrentEndTime());
        if (interval != null) {
            return String.valueOf(interval.getValue());
        }
        return (ss.getAttributeName(quark));
    }

 //   @Override
 //   @Deprecated
 //   protected Map<@NonNull String, @NonNull IYModel> getYModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
 //       return Maps.uniqueIndex(getYSeriesModels(ss, fetchParameters, monitor), IYModel::getName);
 //   }

    /**
     *
     */
    @Override
    protected Collection<IYModel> getYSeriesModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor)
            throws StateSystemDisposedException {

        SelectionTimeQueryFilter filter = FetchParametersUtils.createSelectionTimeQuery(fetchParameters);
        if (filter == null) {
            return null;
        }

        long[] xValues = filter.getTimesRequested();
        List<LpmTablesBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<@NonNull ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (LpmTablesBuilder entry : builders) {
                entry.setPrevCount(extractCount(entry.fMeasuredQuark, states),
                        extractCount(entry.fSecondMeasuredQuark, states));
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
                List<@NonNull ITmfStateInterval> states = ss.queryFullState(time);

                for (LpmTablesBuilder entry : builders) {
                    long count1 = extractCount(entry.fMeasuredQuark, states);
                    long count2 = extractCount(entry.fSecondMeasuredQuark, states);

                    long observationPeriod = time - prevTime;
                    entry.updateValue(i, count1, count2, observationPeriod);
                }
            }
            prevTime = time;
        }
        return Maps.uniqueIndex(Iterables.transform(builders, LpmTablesBuilder::build) , IYModel::getName).values();
    }

    /**
     * Initiate the series builders
     *
     * @param ss
     *      ITmfStateSystem
     * @param filter
     *      SelectionTimeQueryFilter
     * @return
     *      List<LpmTablesBuilder>
     */
    private List<LpmTablesBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        int length = filter.getTimesRequested().length;
        List<LpmTablesBuilder> builders = new ArrayList<>();

        for (Entry<@NonNull Long, @NonNull Integer> entry : getSelectedEntries(filter).entrySet()) {

            long id = entry.getKey();
            int metricQuark = entry.getValue();
            String metricName = ss.getAttributeName(metricQuark);

            if(metricName.equals(IDpdkLpmModelAttributes.TOT_NB_HIT) ||
                    metricName.equals(IDpdkLpmModelAttributes.TOT_NB_MISS)) {
                String secondMetricName, tabName;
                int secondMetricQuark, tabQuark;

                tabQuark = ss.getParentAttributeQuark(metricQuark);
                tabName = getQuarkValue(ss, tabQuark);

                if(metricName.equals(IDpdkLpmModelAttributes.TOT_NB_HIT)) {
                    secondMetricName = IDpdkLpmModelAttributes.TOT_NB_MISS;
                } else {
                    secondMetricName = IDpdkLpmModelAttributes.TOT_NB_HIT;
                }

                try {
                    secondMetricQuark = ss.getQuarkRelative(tabQuark, secondMetricName);
                } catch (AttributeNotFoundException e) {
                    e.printStackTrace();
                    return builders;
                }

                String name = getTrace().getName() + '/' + tabName + '/' + metricName;
                builders.add(new LpmTablesBuilder(id, metricQuark, secondMetricQuark, name, length));
            }
        }
        return builders;
    }

    /**
     * Extract the value of an attribute from an ITmfStateInterval
     *
     * @param metricQuark
     *      Metric quark
     * @param states
     *      List<ITmfStateInterval>
     * @return
     *      count value
     */
    public static long extractCount(int metricQuark, List<ITmfStateInterval> states) {

        if (metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
            Object stateValue = states.get(metricQuark).getValue();

            if(stateValue instanceof Number) {
                return ((Number) stateValue).longValue();
            }
        }

        return 0L;
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
