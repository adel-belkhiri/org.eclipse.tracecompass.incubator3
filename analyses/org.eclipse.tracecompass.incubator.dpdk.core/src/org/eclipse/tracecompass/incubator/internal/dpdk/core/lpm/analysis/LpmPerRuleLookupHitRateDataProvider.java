package org.eclipse.tracecompass.incubator.internal.dpdk.core.lpm.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
public class LpmPerRuleLookupHitRateDataProvider extends AbstractTreeCommonXDataProvider<@NonNull DpdkLpmAnalysisModule, @NonNull TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link VhostPacketRateDataProvider}.
     */
    public static final String PROVIDER_TITLE = IDpdkLpmModelAttributes.LPM_LOOKUP_DATA_PROVIDER_TITLE;

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.dpdk.lpm.per.rule.lookup.hit.rate.data.provider"; //$NON-NLS-1$


    private static final long INVALID_COUNT_VALUE = -1;

    private int maxRulesNumber = 10;
    private boolean ascendingSortingOrder = true;

    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link}.
     */
    private static final class LpmTablesBuilder {

        private static final double SECONDS_PER_NANOSECOND = 1E-9;
        private static final double RATIO = 1 / SECONDS_PER_NANOSECOND;

        private final long fId;

        public final int fMeasuredQuark;

        private final String fName;
        private final double[] fValues;
        private long fPrevCount;

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
        private LpmTablesBuilder(long id, int firstQuark, String name, int length) {
            fId = id;
            fMeasuredQuark = firstQuark;
            fName = name;
            fValues = new double[length];
        }

        private void setPrevCount(long prevCount) {
            this.fPrevCount = prevCount;
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
        private void updateValue(int pos, long newCount, long deltaT) {
            /**
             * Linear interpolation to compute the disk throughput between time and the
             * previous time, from the number of sectors at each time.
             */
            if(newCount != INVALID_COUNT_VALUE) {
                fValues[pos] = (newCount - fPrevCount)  * RATIO / deltaT ;
                this.fPrevCount = newCount;
            }
            else {
                fValues[pos] = 0;
                this.fPrevCount = 0;
            }
        }

        public IYModel build() {
           return new YModel(fId, fName, fValues);
        }
    }

    /**
     * Constructor
     */
    private LpmPerRuleLookupHitRateDataProvider(@NonNull ITmfTrace trace, @NonNull DpdkLpmAnalysisModule module) {
        super(trace, module);
    }

    /**
     * Create an instance of {@link LpmPerRuleLookupHitRateDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link LpmPerRuleLookupHitRateDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static LpmPerRuleLookupHitRateDataProvider create(ITmfTrace trace) {
        DpdkLpmAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkLpmAnalysisModule.class, DpdkLpmAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new LpmPerRuleLookupHitRateDataProvider(trace, module);
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

        /* browse the lpm tables */
        for (Integer tabQuark : ss.getQuarks(IDpdkLpmModelAttributes.LPM_TABS, "*")) {
            String tabName = getQuarkValue(ss, tabQuark);
            long tabId = getId(tabQuark);
            nodes.add(new TmfTreeDataModel(tabId, rootId, tabName));

            int rulesQuark = ss.optQuarkRelative(tabQuark, IDpdkLpmModelAttributes.LPM_RULES);

            if (rulesQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {

                /* browse the rules */
                final Map<Integer, Long> lpmRulesMap = new HashMap<>();
                for (Integer ruleQuark : ss.getQuarks(rulesQuark, "*")) {

                    int nbHitQuark = ss.optQuarkRelative(ruleQuark, IDpdkLpmModelAttributes.NB_HIT);
                    if (nbHitQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {

                        @Nullable Object stateValue = null;

                        long endTime = ss.getCurrentEndTime();
                        long startTime = ss.getStartTime();

                        while (startTime < endTime) {
                            ITmfStateInterval currentInterval = ss.querySingleState(endTime, nbHitQuark);
                            stateValue = currentInterval.getValue();

                            if (stateValue != null) {
                                break;
                            }
                            endTime = currentInterval.getStartTime() - 1;
                        }

                        if((stateValue != null) && (stateValue instanceof Number)) {
                                long countValue = ((Number) stateValue).longValue();
                                lpmRulesMap.put(ruleQuark, countValue);
                         }

                    }
                }

                Comparator<Entry<Integer, Long>> comparator = this.ascendingSortingOrder ?
                        (Map.Entry.<Integer, Long> comparingByValue().reversed()) :
                            Map.Entry.<Integer, Long> comparingByValue();

                final Map<Integer, Long> sortedLpmRulesMap = lpmRulesMap.entrySet()
                        .stream()
                        .sorted(comparator)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1,
                                LinkedHashMap::new));

                int rulesCount = 0;
                for(Entry<Integer, Long> entry : sortedLpmRulesMap.entrySet()) {

                    int ruleQuark = entry.getKey();
                    long ruleId = getId(ruleQuark);
                    String ruleName = getQuarkValue(ss, ruleQuark);
                    nodes.add(new TmfTreeDataModel(ruleId, tabId, ruleName));

                    rulesCount ++;
                    if(rulesCount >= this.maxRulesNumber) {
                        break;
                    }
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

    @Override
    @Deprecated
    protected Map<String, IYModel> getYModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        return Maps.uniqueIndex(getYSeriesModels(ss, fetchParameters, monitor), IYModel::getName);
    }

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
        List<LpmTablesBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (LpmTablesBuilder entry : builders) {
                entry.setPrevCount(extractCount(entry.fMeasuredQuark, states, ss));
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

                for (LpmTablesBuilder entry : builders) {
                    long count = extractCount(entry.fMeasuredQuark, states, ss);
                    long observationPeriod = time - prevTime;
                    entry.updateValue(i, count, observationPeriod);
                }
            }

            prevTime = time;
        }
        return Maps.uniqueIndex(Iterables.transform(builders, LpmTablesBuilder::build) , IYModel::getName).values();
    }

    /**
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

        for (Entry<Long, Integer> entry : getSelectedEntries(filter).entrySet()) {

            int ruleQuark = entry.getValue();
            String ruleName = ss.getAttributeName(ruleQuark);

            int setRulesQuark = ss.getParentAttributeQuark(ruleQuark);
            String setRulesMetricName = ss.getAttributeName(setRulesQuark);

            if(setRulesMetricName.equals(IDpdkLpmModelAttributes.LPM_RULES)) {
                long id = entry.getKey();

                int tabQuark = ss.getParentAttributeQuark(setRulesQuark);
                String tabName = getQuarkValue(ss, tabQuark);

                String name = getTrace().getName() + '/' + tabName + '/' + ruleName;
                builders.add(new LpmTablesBuilder(id, ruleQuark, name, length));
            }
        }
        return builders;
    }

    /**
     * @param ruleQuark
     *      Rule Quark
     * @param states
     *      List<ITmfStateInterval>
     * @param ss
     *      ITmfStateSystem
     * @return xx
     */
    public static long extractCount(int ruleQuark, List<ITmfStateInterval> states, ITmfStateSystem ss) {

        if (ruleQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {

            Object stateValue;
            try {
                int metricQuark = ss.getQuarkRelative(ruleQuark, IDpdkLpmModelAttributes.NB_HIT);
                stateValue = states.get(metricQuark).getValue();
            } catch (AttributeNotFoundException | IndexOutOfBoundsException e) {
                e.printStackTrace();
                return -1;
            }

            if(stateValue instanceof Number) {
                return ((Number) stateValue).longValue();
            }
        }

        return INVALID_COUNT_VALUE;
    }

    @Override
    protected boolean isCacheable() {
        return false;
    }

    @Override
    protected String getTitle() {
        return PROVIDER_TITLE;
    }

    /**
     * @param value
     *      Maximum number of LPM rules to show in the view
     */
    public void setMaxRulesNumber(int value) {
        assert(value > 0 && value <= 100);
        this.maxRulesNumber = value;
    }

    /**
     * @param ascending sorting order
     */
    public void setSortingOrder(boolean ascending) {
        this.ascendingSortingOrder = ascending;
    }
}
