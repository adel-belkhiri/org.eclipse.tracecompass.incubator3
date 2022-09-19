package org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.analysis;

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
 * charts. Model returned is for Pipeline's ports packets rate views.
 *
 * @author  Adel Belkhiri
 */

@SuppressWarnings("restriction")
public class PipelinePortsPacketRateDataProvider
        extends AbstractTreeCommonXDataProvider<@NonNull DpdkPipelineAnalysisModule, @NonNull TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link PipelinePortsPacketRateDataProvider}.
     */
    public static final String PROVIDER_TITLE = IDpdkPipelineModelAttributes.IDpdkModel_PER_PORT_PACKET_RATE_DATAPROVIDER_TITLE;

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.dpdk.core.pipeline.ports.packets.rate.data.provider"; //$NON-NLS-1$


    private static final long INVALID_COUNT_VALUE = -1;


    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link}.
     */
    private static final class PipelinePortsBuilder {

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
        private PipelinePortsBuilder(long id, int firstQuark, String name, int length) {
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
    private PipelinePortsPacketRateDataProvider(@NonNull ITmfTrace trace, @NonNull DpdkPipelineAnalysisModule module) {
        super(trace, module);
    }

    /**
     * Create an instance of {@link PipelinePortsPacketRateDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link PipelinePortsPacketRateDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static PipelinePortsPacketRateDataProvider create(ITmfTrace trace) {
        DpdkPipelineAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkPipelineAnalysisModule.class, DpdkPipelineAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new PipelinePortsPacketRateDataProvider(trace, module);
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
     * Get pipelines input/output ports tree
     */

    @SuppressWarnings("nls")
    @Override
    protected TmfTreeModel<@NonNull TmfTreeDataModel> getTree(ITmfStateSystem ss, Map<String, Object> parameters,
            @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        List<TmfTreeDataModel> nodes = new ArrayList<>();

        long rootId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        nodes.add(new TmfTreeDataModel(rootId, -1, getTrace().getName()));

        /* browse the list of pipelines */
        int pipelinesQuark = ss.optQuarkAbsolute(IDpdkPipelineModelAttributes.PIPELINES);
        for (Integer pipelineQuark : ss.getQuarks(pipelinesQuark, "*")) {
            String pipelineName = getQuarkValue(ss, pipelineQuark);
            long pipelineId = getId(pipelineQuark);
            nodes.add(new TmfTreeDataModel(pipelineId, rootId, pipelineName));

            int portsQuark = ss.optQuarkRelative(pipelineQuark, IDpdkPipelineModelAttributes.PORTS);
            if (portsQuark == ITmfStateSystem.INVALID_ATTRIBUTE) {
                continue;
            }

            /* browse the list of input ports */
            int inputPortsQuark = ss.optQuarkRelative(portsQuark, IDpdkPipelineModelAttributes.IN_PORTS);
            if (inputPortsQuark == ITmfStateSystem.INVALID_ATTRIBUTE) {
                continue;
            }

            long inputPortsId = getId(inputPortsQuark);
            nodes.add(new TmfTreeDataModel(inputPortsId, pipelineId, IDpdkPipelineModelAttributes.IN_PORTS));

            for(Integer inPortQuark : ss.getQuarks(inputPortsQuark, "*")) {
                int nameQuark;
                try {
                    nameQuark = ss.getQuarkRelative(inPortQuark, IDpdkPipelineModelAttributes.IDpdkModel_PORT_NAME);
                    String portName = getQuarkValue(ss, nameQuark);
                    long portId = getId(inPortQuark);
                    nodes.add(new TmfTreeDataModel(portId, inputPortsId, portName));
                } catch (AttributeNotFoundException e) {
                    e.printStackTrace();
                }
            }

            /* browse the list of output ports */
            int outputPortsQuark = ss.optQuarkRelative(portsQuark, IDpdkPipelineModelAttributes.OUT_PORTS);
            if (outputPortsQuark == ITmfStateSystem.INVALID_ATTRIBUTE) {
                continue;
            }

            long outputPortsId = getId(outputPortsQuark);
            nodes.add(new TmfTreeDataModel(outputPortsId, pipelineId, IDpdkPipelineModelAttributes.OUT_PORTS));

            for(Integer outPortQuark : ss.getQuarks(outputPortsQuark, "*")) {
                int nameQuark;
                try {
                    nameQuark = ss.getQuarkRelative(outPortQuark, IDpdkPipelineModelAttributes.IDpdkModel_PORT_NAME);
                    String portName = getQuarkValue(ss, nameQuark);
                    long portId = getId(outPortQuark);
                    nodes.add(new TmfTreeDataModel(portId, outputPortsId, portName));
                } catch (AttributeNotFoundException e) {
                    e.printStackTrace();
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
//    }

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
        List<PipelinePortsBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            for (PipelinePortsBuilder entry : builders) {
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

                for (PipelinePortsBuilder entry : builders) {
                    long count = extractCount(entry.fMeasuredQuark, states, ss);
                    long observationPeriod = time - prevTime;
                    entry.updateValue(i, count, observationPeriod);
                }
            }

            prevTime = time;
        }
        return Maps.uniqueIndex(Iterables.transform(builders, PipelinePortsBuilder::build) , IYModel::getName).values();
    }

    /**
     *
     * @param ss
     *      ITmfStateSystem
     * @param filter
     *      SelectionTimeQueryFilter
     * @return
     *      List<PipelinePortsBuilder>
     */
    private List<PipelinePortsBuilder> initBuilders(ITmfStateSystem ss, SelectionTimeQueryFilter filter) {

        int length = filter.getTimesRequested().length;
        List<PipelinePortsBuilder> builders = new ArrayList<>();

        for (Entry<Long, Integer> entry : getSelectedEntries(filter).entrySet()) {

            int portQuark = entry.getValue();
            int portsQuark = ss.getParentAttributeQuark(portQuark);

            if(portsQuark == ITmfStateSystem.INVALID_ATTRIBUTE || portsQuark == ITmfStateSystem.ROOT_ATTRIBUTE) {
                continue;
            }

            String PortsListname = ss.getAttributeName(portsQuark);
            if(PortsListname.equals(IDpdkPipelineModelAttributes.IN_PORTS) ||
                    PortsListname.equals(IDpdkPipelineModelAttributes.OUT_PORTS)) {
                int portNameQuark;

                try {
                    portNameQuark = ss.getQuarkRelative(portQuark, IDpdkPipelineModelAttributes.IDpdkModel_PORT_NAME);
                } catch (AttributeNotFoundException | IndexOutOfBoundsException e) {
                    continue;
                }

                String portName = getQuarkValue(ss, portNameQuark);

                int portsListQuark = ss.getParentAttributeQuark(portsQuark);
                int pipelineQuark = ss.getParentAttributeQuark(portsListQuark);
                String pipelineName = ss.getAttributeName(pipelineQuark);

                String name = getTrace().getName() + '/' + pipelineName + '/' + PortsListname + '/' + portName;
                builders.add(new PipelinePortsBuilder(entry.getKey(), portQuark, name, length));
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
    public static long extractCount(int portQuark, List<ITmfStateInterval> states, ITmfStateSystem ss) {

        if (portQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {

            Object stateValue = null;
            int metricQuark;

            int portsListQuark = ss.getParentAttributeQuark(portQuark);
            String PortsListname = ss.getAttributeName(portsListQuark);

            if(PortsListname.equals(IDpdkPipelineModelAttributes.IN_PORTS)) {
                try {
                    metricQuark = ss.getQuarkRelative(portQuark, IDpdkPipelineModelAttributes.NB_RX);
                    stateValue = states.get(metricQuark).getValue();
                }
                catch (AttributeNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return INVALID_COUNT_VALUE;
                }
            } else if(PortsListname.equals(IDpdkPipelineModelAttributes.OUT_PORTS)) {
                try {
                    metricQuark = ss.getQuarkRelative(portQuark, IDpdkPipelineModelAttributes.NB_TX);
                    stateValue = states.get(metricQuark).getValue();
                }
                catch (AttributeNotFoundException | IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return INVALID_COUNT_VALUE;
                }
            }


            if((stateValue != null) && (stateValue instanceof Number)) {
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
}
