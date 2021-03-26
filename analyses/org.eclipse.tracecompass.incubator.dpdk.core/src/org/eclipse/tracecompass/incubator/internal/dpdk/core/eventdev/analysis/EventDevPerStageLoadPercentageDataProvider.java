package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
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
/**
 * This data provider will return a XY model (model is wrapped in a response)
 * based on a query filter. The model is used afterwards by any viewer to draw
 * charts. Model returned is for Network I/O views
 *
 * @author adel
 */

@SuppressWarnings("restriction")
public class EventDevPerStageLoadPercentageDataProvider extends AbstractTreeCommonXDataProvider<@NonNull DpdkEventDevAnalysisModule, @NonNull TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link EventDevPerStageLoadPercentageDataProvider}.
     */
    public static final String PROVIDER_TITLE = Objects.requireNonNull(IDpdkEventDevModelAttributes.EVENTDEV_INFLIGHT_CREDIT_DATAPROVIDER_TITLE);

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.per.stage.load.percentage.data.provider"; //$NON-NLS-1$

    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link}.
     */
    private static final class PortLoadBuilder {

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
        private PortLoadBuilder(long id, int firstQuark, int secondQuark, String name, int length) {
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

            fValues[pos] = (secondValue == 0) ? 0 : (firstValue) * CENT / secondValue;

            fFirstPrevCount = firstNewCount;
            fSeconfPrevCount = secondNewCount;
        }

        public IYModel build() {
           return new YModel(fId, fName, fValues);
        }
    }

    /**
     * Create an instance of {@link EventDevPerStageLoadPercentageDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link EventDevPerStageLoadPercentageDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static EventDevPerStageLoadPercentageDataProvider create(ITmfTrace trace) {
        DpdkEventDevAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkEventDevAnalysisModule.class, DpdkEventDevAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new EventDevPerStageLoadPercentageDataProvider(trace, module);
        }
        return null;
    }

    /**
     * Constructor
     */
    private EventDevPerStageLoadPercentageDataProvider(ITmfTrace trace, DpdkEventDevAnalysisModule module) {
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

    @SuppressWarnings("nls")
    @Override
    protected TmfTreeModel<@NonNull TmfTreeDataModel> getTree(ITmfStateSystem ss, Map<String, Object> parameters,
            @Nullable IProgressMonitor monitor) {
        List<TmfTreeDataModel> nodes = new ArrayList<>();

        long rootId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        nodes.add(new TmfTreeDataModel(rootId, -1, getTrace().getName()));

        /* browse the eventdev list */
        for (Integer devQuark : ss.getQuarks(ITmfStateSystem.ROOT_ATTRIBUTE, "*")) {
            String deviceName = "" + getQuarkValue(ss, devQuark);
            long deviceId = getId(devQuark);
            nodes.add(new TmfTreeDataModel(deviceId, rootId, deviceName));

            /* browse attached ports */
            int queuesQuark = ss.optQuarkRelative(devQuark, IDpdkEventDevModelAttributes.QUEUES);
            for(Integer queueQuark : ss.getQuarks(queuesQuark, "*")) {
                int queueMetricQuark = ss.optQuarkRelative(queueQuark, IDpdkEventDevModelAttributes.EVENT_TX);
                long queueMetricId = getId(queueMetricQuark);
                String queueName = "Stage/" + ss.getAttributeName(queueQuark);
                nodes.add(new TmfTreeDataModel(queueMetricId,deviceId, queueName));

                int attachedPortsQuark = ss.optQuarkRelative(queueQuark, IDpdkEventDevModelAttributes.ATTACHED_PORTS);

                for(Integer portQuark : ss.getQuarks(attachedPortsQuark, "*")) {
                    int portMetricQuark = ss.optQuarkRelative(portQuark, IDpdkEventDevModelAttributes.EVENT_TX);
                    long portMetricId = getId(portMetricQuark);

                    String portName = "Port/" + ss.getAttributeName(portQuark);
                    nodes.add(new TmfTreeDataModel(portMetricId, queueMetricId, portName));
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
     * @since 1.2
     */
    @Override
    protected Collection<IYModel> getYSeriesModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor)
            throws StateSystemDisposedException {

        SelectionTimeQueryFilter filter = FetchParametersUtils.createSelectionTimeQuery(fetchParameters);
        if (filter == null) {
            return null;
        }

        long[] xValues = filter.getTimesRequested();
        List<PortLoadBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (PortLoadBuilder entry : builders) {
                long value1 = extractCount(entry.fMeasuredQuark, states);
                long value2 = extractCount(entry.fSecondMeasuredQuark, states);

                entry.setPrevCount(value1, value2);
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

                for (PortLoadBuilder entry : builders) {
                    long value1 = extractCount(entry.fMeasuredQuark, states);
                    long value2 = extractCount(entry.fSecondMeasuredQuark, states);

                    if(value1 != 0 && value2 != 0) {
                        long observationPeriod = time - prevTime;
                        entry.updateValue(i, value1, value2, observationPeriod);
                    }
                }
            }
            prevTime = time;
        }

        return Maps.uniqueIndex(Iterables.transform(builders, PortLoadBuilder::build) , IYModel::getName).values();
    }

    /**
     *
     * @param ss
     * @param filter
     * @return
     */
    private List<PortLoadBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        int length = filter.getTimesRequested().length;
        List<PortLoadBuilder> builders = new ArrayList<>();

        for (Entry<Long, Integer> entry : getSelectedEntries(filter).entrySet()) {

            int portMetricQuark = entry.getValue();
            int portQuark = ss.getParentAttributeQuark(portMetricQuark);

            if(portQuark == ITmfStateSystem.INVALID_ATTRIBUTE || portQuark == ITmfStateSystem.ROOT_ATTRIBUTE) {
                continue;
            }

            int attachedPortsQuark = ss.getParentAttributeQuark(portQuark);
            String attachedPortsName = ss.getAttributeName(attachedPortsQuark);

            if(attachedPortsName.equals(IDpdkEventDevModelAttributes.ATTACHED_PORTS)) {
                long id = entry.getKey();

                int queueQuark = ss.getParentAttributeQuark(attachedPortsQuark);
                int queueMetricQuark = ss.optQuarkRelative(queueQuark, IDpdkEventDevModelAttributes.EVENT_TX);
                int queuesQuark = ss.getParentAttributeQuark(queueQuark);
                int deviceQuark = ss.getParentAttributeQuark(queuesQuark);

                String portName = ss.getAttributeName(portQuark);
                String queueName = ss.getAttributeName(queueQuark);
                String deviceName = ss.getAttributeName(deviceQuark);


                String name = getTrace().getName() + '/' + deviceName + '/' + queueName + '/' + portName;
                builders.add(new PortLoadBuilder(id, portMetricQuark, queueMetricQuark, name, length));
            }
        }
        return builders;
    }

    /**
     * @param recvSentQuark Quark
     * @param states Sates
     * @return xx
     */
    public static long extractCount(int metricQuark, List<ITmfStateInterval> states /*, ITmfStateSystem ss*/) {

        if (metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
            Object dequeueValue = states.get(metricQuark).getValue();

            if(dequeueValue instanceof Number) {
                return ((Number) dequeueValue).longValue();
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
