package org.eclipse.tracecompass.incubator.internal.dpdk.core.table.analysis;

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
 * This data provider will return a XY model (model is wrapped in a response)
 * based on a query filter. The model is used afterwards by any viewer to draw
 * charts. Model returned is for Network I/O views
 *
 * @author  Adel Belkhiri
 */

@SuppressWarnings("restriction")
public class TableLookupHitRateDataProvider
        extends AbstractTreeCommonXDataProvider<@NonNull DpdkTableAnalysisModule, @NonNull TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link VhostPacketRateDataProvider}.
     */
    public static final String PROVIDER_TITLE = IDpdkTableModelAttributes.IDpdkModel_PER_RULE_HIT_RATE_DATAPROVIDER_TITLE;

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.dpdk.table.lookup.hit.rate.data.provider"; //$NON-NLS-1$

    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link}.
     */
    /**
     * Inline class to encapsulate all the values required to build a series.
     */
    private static final class TableBuilder {

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
        private TableBuilder(long id, int firstQuark, int secondQuark, String name, int length) {
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
    private TableLookupHitRateDataProvider(@NonNull ITmfTrace trace, @NonNull DpdkTableAnalysisModule module) {
        super(trace, module);
    }

    /**
     * Create an instance of {@link DpdkLookupHitRateDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link ClassifierPerRuleLookupHitRateDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static TableLookupHitRateDataProvider create(ITmfTrace trace) {
        DpdkTableAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkTableAnalysisModule.class, DpdkTableAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new TableLookupHitRateDataProvider(trace, module);
        }
        return null;
    }

    /**
     *
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * Get LPM rules Tree
     */

    @SuppressWarnings("nls")
    @Override
    protected TmfTreeModel<@NonNull TmfTreeDataModel> getTree(ITmfStateSystem ss, Map<String, Object> parameters,
            @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        List<TmfTreeDataModel> nodes = new ArrayList<>();

        long rootId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        nodes.add(new TmfTreeDataModel(rootId, -1, getTrace().getName()));

        /* browse the list of table types */
        for (Integer tableClassQuark : ss.getQuarks("*")) {
            String tableClassName = getQuarkValue(ss, tableClassQuark);
            long tableClassId = getId(tableClassQuark);
            nodes.add(new TmfTreeDataModel(tableClassId, rootId, tableClassName));

            /* browse the list of tables */
            for(Integer tabQuark : ss.getQuarks(tableClassQuark, "*")) {
                String tabName = getQuarkValue(ss, tabQuark);
                long tabId = getId(tabQuark);
                nodes.add(new TmfTreeDataModel(tabId, tableClassId, tabName));

                int nbHitQuark = ss.optQuarkRelative(tabQuark, IDpdkTableModelAttributes.TOT_NB_HIT);
                int nbMissQuark = ss.optQuarkRelative(tabQuark, IDpdkTableModelAttributes.TOT_NB_MISS);

                if (nbHitQuark != ITmfStateSystem.INVALID_ATTRIBUTE &&
                        nbMissQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                    long firstMetricId = getId(nbHitQuark);
                    nodes.add(new TmfTreeDataModel(firstMetricId, tabId, IDpdkTableModelAttributes.HIT_PERCENT_METRIC_LABEL));

                    long secondMetricId = getId(nbMissQuark);
                    nodes.add(new TmfTreeDataModel(secondMetricId, tabId, IDpdkTableModelAttributes.MISS_PERCENT_METRIC_LABEL));
                }
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

 //   @Override
 //   @Deprecated
 //   protected Map<String, IYModel> getYModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
 //       return Maps.uniqueIndex(getYSeriesModels(ss, fetchParameters, monitor), IYModel::getName);
 //   }

    /**
     *
     */
    @SuppressWarnings("null")
    @Override
    protected Collection<IYModel> getYSeriesModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor)
            throws StateSystemDisposedException {

        SelectionTimeQueryFilter filter = FetchParametersUtils.createSelectionTimeQuery(fetchParameters);
        if (filter == null) {
            return null;
        }

        long[] xValues = filter.getTimesRequested();
        List<TableBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (TableBuilder entry : builders) {
                entry.setPrevCount(extractCount(entry.fMeasuredQuark, states, ss),
                        extractCount(entry.fSecondMeasuredQuark, states, ss));
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

                for (TableBuilder entry : builders) {
                    long firstCount = extractCount(entry.fMeasuredQuark, states, ss);
                    long secondCount = extractCount(entry.fSecondMeasuredQuark, states, ss);

                    long observationPeriod = time - prevTime;
                    entry.updateValue(i, firstCount, secondCount, observationPeriod);
                }
            }

            prevTime = time;
        }
        return Maps.uniqueIndex(Iterables.transform(builders, TableBuilder::build) , IYModel::getName).values();
    }


    /**
     * Initiate the series builders
     *
     * @param ss
     *      ITmfStateSystem
     * @param filter
     *      SelectionTimeQueryFilter
     * @return
     *      List<TableBuilder>
     */
    private List<TableBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        int length = filter.getTimesRequested().length;
        List<TableBuilder> builders = new ArrayList<>();

        for (Entry<@NonNull Long, @NonNull Integer> entry : getSelectedEntries(filter).entrySet()) {

            int metricQuark = entry.getValue();
            String metricName = ss.getAttributeName(metricQuark);

            if(metricName.equals(IDpdkTableModelAttributes.TOT_NB_HIT) ||
                    metricName.equals(IDpdkTableModelAttributes.TOT_NB_MISS)) {
                String secondMetricName;
                int secondMetricQuark;

                long id = entry.getKey();
                int tabQuark = ss.getParentAttributeQuark(metricQuark);
                String tabName = getQuarkValue(ss, tabQuark);

                if(metricName.equals(IDpdkTableModelAttributes.TOT_NB_HIT)) {
                    secondMetricName = IDpdkTableModelAttributes.TOT_NB_MISS; //IDpdkTableModelAttributes.MISS_PERCENT_METRIC_LABEL;
                } else {
                    secondMetricName = IDpdkTableModelAttributes.TOT_NB_HIT; //IDpdkTableModelAttributes.HIT_PERCENT_METRIC_LABEL;
                }

                try {
                    secondMetricQuark = ss.getQuarkRelative(tabQuark, secondMetricName);
                } catch (AttributeNotFoundException e) {
                    e.printStackTrace();
                    return builders;
                }

                String name = getTrace().getName() + '/' + tabName + '/' + metricName;
                builders.add(new TableBuilder(id, metricQuark, secondMetricQuark, name, length));
            }
        }
        return builders;
    }


    /**
     * @param metricQuark xx
     * @param states
     *      List<ITmfStateInterval>
     * @param ss
     *      ITmfStateSystem
     * @return xx
     */
    public static long extractCount(int metricQuark, List<ITmfStateInterval> states, ITmfStateSystem ss) {

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
        return false;
    }

    @Override
    protected String getTitle() {
        return PROVIDER_TITLE;
    }
}
