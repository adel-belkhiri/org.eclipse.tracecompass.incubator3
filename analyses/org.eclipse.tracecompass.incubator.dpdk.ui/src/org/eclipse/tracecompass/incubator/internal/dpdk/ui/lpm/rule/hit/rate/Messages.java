package org.eclipse.tracecompass.incubator.internal.dpdk.ui.lpm.rule.hit.rate;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osgi.util.NLS;

/**
 * @author adel
 *
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    private static String BUNDLE_NAME = "org.eclipse.tracecompass.incubator.internal.dpdk.ui.lpm.rule.hit.rate.messages";  //$NON-NLS-1$

    public static @Nullable String DpdkLpmHitRateViewer_Title;
    public static @Nullable String DpdkLpmHitRateViewer_XAxis;
    public static @Nullable String DpdkLpmHitRateViewer_YAxis;

    public static @Nullable String DpdkLpmHitRateTreeViewer_TabName;

    public static @Nullable String DpdkLpmHitRateView_Title;
    public static @Nullable String DpdkLpmHitRateTreeViewer_Legend;

    public static @Nullable String ParameterDialog_ModifyParameter;
    public static @Nullable String ParameterDialog_Customize;
    public static @Nullable String ParameterDialog_OptionsGroupLabel;
    public static @Nullable String ParameterDialog_AscendingOption;
    public static @Nullable String ParameterDialog_DescendingOption;
    public static @Nullable String ParameterDialog_OutOfRangeErrorMsg;
    public static @Nullable String ParameterDialog_NotNumberErrorMsg;
    public static @Nullable String ParameterDialog_MaxNumberTextLabel;
    public static @Nullable String ParameterDialog_TextRulesMaxNumberToolTip;


    static {
        // initialize resource bundle

    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
