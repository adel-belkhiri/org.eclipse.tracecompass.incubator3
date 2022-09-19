package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis;

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
public class EventdevPortsBusynessDataProvider
        extends AbstractTreeCommonXDataProvider<@NonNull DpdkEventDevAnalysisModule, @NonNull TmfTreeDataModel> {

    /**
     * Title used to create XY models for the {@link PipelinePortsBusynessDataProvider}.
     */
    public static final String PROVIDER_TITLE = IDpdkEventDevModelAttributes.EVENTDEV_PORT_BUSYNESS_PERCENTAGE_DATAPROVIDER_TITLE;

    /**
     * Extension point ID.
     */
    public static final String ID = "org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.ports.busyness.data.provider"; //$NON-NLS-1$


    /**
     * Inline class to encapsulate all the values required to build a series. Allows
     * for reuse of full query results to be faster than {@link}.
     */
    private static final class PipelinePortsBuilder {

        private static final double CENT = 100;

        private final long fId;

        public final int fZeroPollQuark;
        public final int fTotPollQuark;

        private final String fName;
        private final double[] fValues;

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
        private PipelinePortsBuilder(long id, int firstQuark, int secondQuark, String name, int length) {
            fId = id;
            fZeroPollQuark = firstQuark;
            fTotPollQuark = secondQuark;
            fName = name;

            fValues = new double[length];
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
        private void updateValue(int pos, long zeroPolls, long totPolls, long deltaT) {

            if(totPolls == 0) {
                fValues[pos] = 0;
            } else {
                double value = ((totPolls - zeroPolls)  * CENT) / totPolls;
                fValues[pos] = value;
            }

            //if((zeroPolls != fPrevCountZeroPolls) || (nonZeroPolls != fPrevCountNonZeroPolls)) {
               // fPrevCountZeroPolls = zeroPolls;
               // fPrevCountTotPolls = totPolls;
            //}
        }

        public IYModel build() {
           return new YModel(fId, fName, fValues);
        }
    }

    /**
     * Constructor
     */
    private EventdevPortsBusynessDataProvider(@NonNull ITmfTrace trace, @NonNull DpdkEventDevAnalysisModule module) {
        super(trace, module);
    }

    /**
     * Create an instance of {@link PipelinePortsBusynessDataProvider}. Returns a null instance if
     * the analysis module is not found.
     *
     * @param trace
     *            A trace on which we are interested to fetch a model
     * @return A {@link PipelinePortsBusynessDataProvider} instance. If analysis module is not
     *         found, it returns null
     */
    public static EventdevPortsBusynessDataProvider create(ITmfTrace trace) {
        DpdkEventDevAnalysisModule module = TmfTraceUtils.getAnalysisModuleOfClass(trace, DpdkEventDevAnalysisModule.class, DpdkEventDevAnalysisModule.ID);
        if (module != null) {
            module.schedule();
            return new EventdevPortsBusynessDataProvider(trace, module);
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
     * Get pipelines input ports tree
     */

    @SuppressWarnings("nls")
    @Override
    protected TmfTreeModel<@NonNull TmfTreeDataModel> getTree(ITmfStateSystem ss, Map<String, Object> parameters,
            @Nullable IProgressMonitor monitor) throws StateSystemDisposedException {
        List<TmfTreeDataModel> nodes = new ArrayList<>();

        long rootId = getId(ITmfStateSystem.ROOT_ATTRIBUTE);
        nodes.add(new TmfTreeDataModel(rootId, -1, getTrace().getName()));

        /* browse the list of eventdev */
        for (Integer eventdevQuark : ss.getQuarks(ITmfStateSystem.ROOT_ATTRIBUTE, "*")) {
            String eventdevName = getQuarkValue(ss, eventdevQuark);
            long eventdevId = getId(eventdevQuark);
            nodes.add(new TmfTreeDataModel(eventdevId, rootId, eventdevName));

            int portsQuark = ss.optQuarkRelative(eventdevQuark, IDpdkEventDevModelAttributes.PORTS);
            if (portsQuark == ITmfStateSystem.INVALID_ATTRIBUTE) {
                continue;
            }

            for(Integer portQuark : ss.getQuarks(portsQuark, "*")) {
                String portName = "Port/" + ss.getAttributeName(portQuark);
                long portId = getId(portQuark);
                nodes.add(new TmfTreeDataModel(portId, eventdevId, portName));
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
        List<PipelinePortsBuilder> builders = initBuilders(ss, filter);
        if (builders.isEmpty()) {
            return Collections.emptyList();
        }

        long currentEnd = ss.getCurrentEndTime();
        long prevTime = filter.getStart();
        if (prevTime >= ss.getStartTime() && prevTime <= currentEnd) {
            // reuse the results from the full query
            //List<ITmfStateInterval> states = ss.queryFullState(prevTime);

            //for (PipelinePortsBuilder entry : builders) {
                //long zeroPolls = extractCount(entry.fZeroPollQuark, states);
                //long totPolls = extractCount(entry.fTotPollQuark, states);
                //entry.setPrevCount(zeroPolls, totPolls);
            //}
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
                    long zeroPolls = extractCount(entry.fZeroPollQuark, states);
                    long nonZeroPolls = extractCount(entry.fTotPollQuark, states);

                    long observationPeriod = time - prevTime;
                    entry.updateValue(i, zeroPolls, nonZeroPolls, observationPeriod);
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

            String PortsName = ss.getAttributeName(portsQuark);
            if(PortsName.equals(IDpdkEventDevModelAttributes.PORTS)) {
                int zeroPollQuark, totPollQuark;

                try {
                    zeroPollQuark = ss.getQuarkRelative(portQuark, IDpdkEventDevModelAttributes.ZERO_POLLS);
                    totPollQuark = ss.getQuarkRelative(portQuark, IDpdkEventDevModelAttributes.TOT_POLLS);
                } catch (AttributeNotFoundException | IndexOutOfBoundsException e) {
                    continue;
                }

                String portName = ss.getAttributeName(portQuark);
                int eventdevQuark = ss.getParentAttributeQuark(portsQuark);
                String eventdevName = ss.getAttributeName(eventdevQuark);

                String name = getTrace().getName() + '/' + eventdevName + '/' + portName;
                builders.add(new PipelinePortsBuilder(entry.getKey(), zeroPollQuark, totPollQuark, name, length));
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
    public static long extractCount(int metricQuark, List<ITmfStateInterval> states) {

        if (metricQuark != ITmfStateSystem.INVALID_ATTRIBUTE) {
            Object occupancyValue = states.get(metricQuark).getValue();

            if(occupancyValue instanceof Number) {
                return ((Number) occupancyValue).longValue();
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
