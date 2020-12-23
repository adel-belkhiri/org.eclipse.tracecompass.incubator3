package org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.eventhandlers;

import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.eventdev.analysis.DpdkEventDevStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;

@SuppressWarnings("javadoc")
public abstract class DpdkEventHandler {

    private final DpdkEventDevAnalysisEventLayout fLayout;
    protected final DpdkEventDevStateProvider fEventdevStateProvier;

    /**
     * Constructor
     *
     * @param layout the layout of the analysis
     */
    public DpdkEventHandler(DpdkEventDevAnalysisEventLayout layout, DpdkEventDevStateProvider ss) {
        this.fLayout = layout;
        this.fEventdevStateProvier = ss;
    }

    /**
     * Get the analysis layout
     * @return the analysis layout
     */
    protected DpdkEventDevAnalysisEventLayout getLayout() {
        return fLayout;
    }

    /**
     * Handle a specific event.
     *
     * @param ss
     *      the state system to write to
     * @param event
     *      the event
     * @throws AttributeNotFoundException
     *      if the attribute is not yet create
     */
    public abstract void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException;

}

