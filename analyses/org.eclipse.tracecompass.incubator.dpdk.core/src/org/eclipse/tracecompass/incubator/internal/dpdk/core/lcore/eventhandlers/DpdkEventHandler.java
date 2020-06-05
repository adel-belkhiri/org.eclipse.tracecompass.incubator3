package org.eclipse.tracecompass.incubator.internal.dpdk.core.lcore.eventhandlers;

import org.eclipse.tracecompass.statesystem.core.exceptions.AttributeNotFoundException;
import org.eclipse.tracecompass.statesystem.core.ITmfStateSystemBuilder;
import org.eclipse.tracecompass.tmf.core.event.ITmfEvent;

@SuppressWarnings("javadoc")
public abstract class DpdkEventHandler {

    private final DpdkAnalysisEventLayout fLayout;

    /**
     * Constructor
     *
     * @param layout the analysis layout
     */
    public DpdkEventHandler(DpdkAnalysisEventLayout layout) {
        fLayout = layout;
    }

    /**
     * Get the analysis layout
     * @return the analysis layout
     */
    protected DpdkAnalysisEventLayout getLayout() {
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

