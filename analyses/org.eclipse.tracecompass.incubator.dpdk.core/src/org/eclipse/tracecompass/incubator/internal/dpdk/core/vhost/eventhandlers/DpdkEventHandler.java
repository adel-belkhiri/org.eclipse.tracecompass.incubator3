package org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.eventhandlers;

import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.incubator.internal.dpdk.core.vhost.analysis.DpdkVhostStateProvider;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;

@SuppressWarnings("javadoc")
public abstract class DpdkEventHandler {

    private final DpdkVhostAnalysisEventLayout fLayout;
    protected final DpdkVhostStateProvider fVhostStateProvier;

    /**
     * Constructor
     *
     * @param layout the layout of the analysis
     */
    public DpdkEventHandler(DpdkVhostAnalysisEventLayout layout, DpdkVhostStateProvider ss) {
        fLayout = layout;
        this.fVhostStateProvier = ss;
    }

    /**
     * Get the analysis layout
     * @return the analysis layout
     */
    protected DpdkVhostAnalysisEventLayout getLayout() {
        return fLayout;
    }

    /**
     * Handle a specific event.
     *
     * @param ss the state system to write to
     * @param event the event
     * @throws AttributeNotFoundException
     *             if the attribute is not yet create
     */
    public abstract void handleEvent(ITmfStateSystemBuilder ss, ITmfEvent event) throws AttributeNotFoundException;

}

