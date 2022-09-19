package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis;

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
public class VhostPacketRateDataProvider
    extends AbstractTreeCommonXDataProvider<@NonNull DpdkVhostAnalysisModule, @NonNull TmfTreeDataModel> {

    public static final int INVALID_ATTRIBUTE = -2;
    /**
     * Title used to create XY models for the {@link VhostPacketRateDataProvider}.
     */
    public static final String PROVIDER_TITLE = Objects.requireNonNull(IDpdkVhostModelAttributes.DATA_PROVIDER_TITLE);

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.dpdk.vhost.packet.rate.data.provider"; //$NON-NLS-1$

    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link}.
     */
    private static final class VirtQueueBuilder {

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
        private VirtQueueBuilder(long id, int sentRecvQuark, String name, int length) {
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
     * Create an instance of {@link VhostPacketRateDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link VhostPacketRateDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static VhostPacketRateDataProvider create(ITmfTrace trace) {
        DpdkVhostAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkVhostAnalysisModule.class, DpdkVhostAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new VhostPacketRateDataProvider(trace, module);
        }
        return null;
    }

    /**
     * Constructor
     */
    private VhostPacketRateDataProvider(ITmfTrace trace, DpdkVhostAnalysisModule module) {
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

        /* browse the device set : net_vhost0, etc. */
        for (Integer devQuark : ss.getQuarks(IDpdkVhostModelAttributes.DEVICES, "*")) {
            String deviceName = getQuarkValue(ss, devQuark);
            long deviceId = getId(devQuark);
            nodes.add(new TmfTreeDataModel(deviceId, rootId, deviceName));

            /* browse the vid set : vid(0), etc. */
            int vidsQuark = ss.optQuarkRelative(devQuark, IDpdkVhostModelAttributes.VIDS);
            if(vidsQuark == INVALID_ATTRIBUTE ) {
                continue;
            }

            for(Integer vidQuark : ss.getQuarks(vidsQuark, "*")) {
                /*Show the connection fd instead of vid */
                int connfdQuark = ss.optQuarkRelative(vidQuark, IDpdkVhostModelAttributes.CONNFD);
                String connfdName = "fd-"+ getQuarkValue(ss, connfdQuark);
                long connfdId = getId(connfdQuark);

                nodes.add(new TmfTreeDataModel(connfdId, deviceId, connfdName));

                /* browse the RX queues set */
                int rxQueuesQuark = ss.optQuarkRelative(vidQuark, IDpdkVhostModelAttributes.RX_QUEUES);
                if (rxQueuesQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                    long rxQueuesId = getId(rxQueuesQuark);
                    nodes.add(new TmfTreeDataModel(rxQueuesId, connfdId, "RX"));

                    for(Integer rxQueueQuark : ss.getQuarks(rxQueuesQuark, "*")) {
                        String queueName = getQuarkValue(ss, rxQueueQuark);
                        long queueId = getId(rxQueueQuark);
                        nodes.add(new TmfTreeDataModel(queueId, rxQueuesId, queueName));
                    }
                }

                /* browse the TX queues set */
                int txQueuesQuark = ss.optQuarkRelative(vidQuark, IDpdkVhostModelAttributes.TX_QUEUES);
                if (txQueuesQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                    long txQueuesId = getId(txQueuesQuark);
                    nodes.add(new TmfTreeDataModel(txQueuesId, connfdId, "TX"));

                    for(Integer txQueueQuark : ss.getQuarks(txQueuesQuark, "*")) {
                        String queueName = getQuarkValue(ss, txQueueQuark);
                        long queueId = getId(txQueueQuark);
                        nodes.add(new TmfTreeDataModel(queueId, txQueuesId, queueName));
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

//    @Override
//    @Deprecated
//    protected Map<String, IYModel> getYModels(ITmfStateSystem ss, Map<String, Object> fetchParameters, @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
//        return Maps.uniqueIndex(getYSeriesModels(ss, fetchParameters, monitor), IYModel::getName);
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
        List<VirtQueueBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (VirtQueueBuilder entry : builders) {
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

                for (VirtQueueBuilder entry : builders) {
                    double count = extractCount(entry.fMeasuredQuark, states);
                    if(count != 0) {
                        long observationPeriod = time - prevTime;
                        entry.updateValue(i, count, observationPeriod);
                    }
                }
            }
            prevTime = time;
        }
        return Maps.uniqueIndex(Iterables.transform(builders, VirtQueueBuilder::build) , IYModel::getName).values();
    }

    /**
     *
     * @param ss
     * @param filter
     * @return
     */
    private List<VirtQueueBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        int metricQuark, rxMetricQuark, txMetricQuark;
        int length = filter.getTimesRequested().length;
        List<VirtQueueBuilder> builders = new ArrayList<>();

        for (Entry<Long, Integer> entry : getSelectedEntries(filter).entrySet()) {

            int quark = entry.getValue();

            rxMetricQuark = ss.optQuarkRelative(quark, IDpdkVhostModelAttributes.NB_MBUF_DEQUEUE);
            txMetricQuark = ss.optQuarkRelative(quark, IDpdkVhostModelAttributes.NB_MBUF_ENQUEUE);

            if(rxMetricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                metricQuark = rxMetricQuark;
            } else {
                metricQuark = txMetricQuark;
            }

            if (metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
                long id = entry.getKey();
                int queueQuark = ss.getParentAttributeQuark(metricQuark);
                String queueName = getQuarkValue(ss, queueQuark);

                int queuesTypeQuark = ss.getParentAttributeQuark(queueQuark);
                String queueTypeName = getQuarkValue(ss, queuesTypeQuark);

                int vidQuark = ss.getParentAttributeQuark(queuesTypeQuark);
                int connfdQuark = ss.optQuarkRelative(vidQuark, IDpdkVhostModelAttributes.CONNFD);
                String connfdName = "fd-"+ getQuarkValue(ss, connfdQuark);

                int devQuark = ss.getParentAttributeQuark(ss.getParentAttributeQuark(vidQuark));
                String devName = getQuarkValue(ss, devQuark);

                String name = getTrace().getName() + '/' + devName + '/' + connfdName + '/' + queueTypeName +  '/' + queueName;

                builders.add(new VirtQueueBuilder(id, metricQuark, name, length));
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

        // Get the initial value.

        if (metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
            Object stateValue = states.get(metricQuark).getValue();

            if(stateValue instanceof Number) {
                return ((Number) stateValue).doubleValue();
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
