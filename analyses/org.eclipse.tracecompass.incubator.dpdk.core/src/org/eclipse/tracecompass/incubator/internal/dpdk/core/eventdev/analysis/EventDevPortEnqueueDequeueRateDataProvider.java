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
public class EventDevPortEnqueueDequeueRateDataProvider extends AbstractTreeCommonXDataProvider<@NonNull DpdkEventDevAnalysisModule, @NonNull TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link EventDevPortEnqueueDequeueRateDataProvider}.
     */
    public static final String PROVIDER_TITLE = Objects.requireNonNull(IDpdkEventDevModelAttributes.EVENTDEV_PORT_ENQUEUE_DEQUEUE_DATAPROVIDER_TITLE);

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.enqueue.dequeue.data.provider"; //$NON-NLS-1$

    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link}.
     */
    private static final class PortRateBuilder {

        private static final double SECONDS_PER_NANOSECOND = 1E-9;
        private static final double RATIO = 1 / SECONDS_PER_NANOSECOND;

        private final long fId;

        /** This series' sector quark. public because final */
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
        private PortRateBuilder(long id, int sentRecvQuark, String name, int length) {
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
            fValues[pos] = (newCount - fPrevCount)  * RATIO / deltaT;
            fPrevCount = newCount;
        }

        public IYModel build() {
           return new YModel(fId, fName, fValues);
        }
    }


    /**
     * Create an instance of {@link EventDevPortEnqueueDequeueRateDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link EventDevPortEnqueueDequeueRateDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static EventDevPortEnqueueDequeueRateDataProvider create(ITmfTrace trace) {
        DpdkEventDevAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkEventDevAnalysisModule.class, DpdkEventDevAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new EventDevPortEnqueueDequeueRateDataProvider(trace, module);
        }
        return null;
    }

    /**
     * Constructor
     */
    private EventDevPortEnqueueDequeueRateDataProvider(ITmfTrace trace, DpdkEventDevAnalysisModule module) {
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
            String deviceName = "" + ss.getAttributeName(devQuark);
            long deviceId = getId(devQuark);
            nodes.add(new TmfTreeDataModel(deviceId, rootId, deviceName));

            /* browse attached ports */
            int portsQuark = ss.optQuarkRelative(devQuark, IDpdkEventDevModelAttributes.PORTS);
            if(portsQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                for(Integer portQuark : ss.getQuarks(portsQuark, "*")) {
                    String metricName = "";
                    long metricId;
                    int metricQuark;

                    String portName = "Port/" + ss.getAttributeName(portQuark);
                    long portId = getId(portQuark);

                    nodes.add(new TmfTreeDataModel(portId, deviceId, portName));

                    metricQuark = ss.optQuarkRelative(portQuark, IDpdkEventDevModelAttributes.EVENT_RX);
                    if(metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                        metricName = Objects.requireNonNull(IDpdkEventDevModelAttributes.EVENT_RX);
                        metricId = getId(metricQuark);
                        nodes.add(new TmfTreeDataModel(metricId, portId, metricName));
                    }

                    metricQuark = ss.optQuarkRelative(portQuark, IDpdkEventDevModelAttributes.EVENT_TX);
                    if(metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                        metricName = Objects.requireNonNull(IDpdkEventDevModelAttributes.EVENT_TX);
                        metricId = getId(metricQuark);
                        nodes.add(new TmfTreeDataModel(metricId, portId, metricName));
                    }
                }
            }
        }
        return new TmfTreeModel<>(Collections.emptyList(), nodes);
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
        List<PortRateBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (PortRateBuilder entry : builders) {
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

                for (PortRateBuilder entry : builders) {
                    double count = extractCount(entry.fMeasuredQuark, states);
                    if(count != 0) {
                        long observationPeriod = time - prevTime;
                        entry.updateValue(i, count, observationPeriod);
                    }
                }
            }
            prevTime = time;
        }

        return Maps.uniqueIndex(Iterables.transform(builders, PortRateBuilder::build) , IYModel::getName).values();
    }

    /**
     *
     * @param ss
     * @param filter
     * @return
     */
    private List<PortRateBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        int length = filter.getTimesRequested().length;
        List<PortRateBuilder> builders = new ArrayList<>();

        for (Entry<Long, Integer> entry : getSelectedEntries(filter).entrySet()) {

            int metricQuark = entry.getValue();
            String metricName = ss.getAttributeName(metricQuark);

            if ((metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) &&
                    (metricName.equals(IDpdkEventDevModelAttributes.EVENT_RX) ||
                            (metricName.equals(IDpdkEventDevModelAttributes.EVENT_TX)))) {

                long id = entry.getKey();

                int portQuark = ss.getParentAttributeQuark(metricQuark);
                String portName = "Port/" + ss.getAttributeName(portQuark);

                int portsQuark = ss.getParentAttributeQuark(portQuark);
                int devQuark = ss.getParentAttributeQuark(portsQuark);
                String devName = ss.getAttributeName(devQuark);

                String name = getTrace().getName() + '/' + devName + '/' + portName + '/' + metricName;
                builders.add(new PortRateBuilder(id, metricQuark, name, length));
            }
        }
        return builders;
    }

    /**
     * @param recvSentQuark Quark
     * @param states Sates
     * @return xx
     */
    public static double extractCount(int metricQuark, List<ITmfStateInterval> states) {

        if (metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
            Object nbEventsValue = states.get(metricQuark).getValue();

            if(nbEventsValue instanceof Number) {
                return ((Number) nbEventsValue).doubleValue();
            }
        }

        return 0.0;
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
