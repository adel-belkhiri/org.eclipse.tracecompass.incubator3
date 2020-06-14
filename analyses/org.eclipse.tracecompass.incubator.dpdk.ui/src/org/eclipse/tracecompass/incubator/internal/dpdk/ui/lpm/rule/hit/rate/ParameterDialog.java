/*******************************************************************************
 * Copyright (c) 2000, 2020 IBM Corporation and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.tracecompass.incubator.internal.dpdk.ui.lpm.rule.hit.rate;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Parameter Dialog window to customize the way LPM rules tree is shown.
 *
 * @author Adel Belkhiri
 */
@NonNullByDefault({})
class ParameterDialog extends Dialog {

    private String fTitle;
    private String fRulesMaxNumber;
    private boolean isOrderAscending;
    private IInputValidator fValidator;

    private Button fOkButton;
    private Button fSortAscendingRadionButton;
    private Button fSortDescendingRadionButton;
    private Text fRulesMaxNumberText;

    public ParameterDialog(Shell parentShell,
            String dialogTitle,
            IInputValidator validator) {
        super(parentShell);
        this.fTitle = dialogTitle;
        this.fRulesMaxNumber = "10";//$NON-NLS-1$
        this.fValidator = validator;
        this.isOrderAscending = true;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            fRulesMaxNumber = fRulesMaxNumberText.getText();
        } else {
            fRulesMaxNumber = null;
        }
        super.buttonPressed(buttonId);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (fTitle != null) {
            shell.setText(fTitle);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        fOkButton = createButton(parent, IDialogConstants.OK_ID,
                IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
        fRulesMaxNumberText.setFocus();
        if (fRulesMaxNumber != null) {
            fRulesMaxNumberText.setText(fRulesMaxNumber);
            fRulesMaxNumberText.selectAll();
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);

        Label label = new Label(composite, SWT.WRAP);
        label.setText(Messages.ParameterDialog_MaxNumberTextLabel+ " :    "); //$NON-NLS-1$

        fRulesMaxNumberText = new Text(composite, getInputTextStyle());
        fRulesMaxNumberText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL));
        fRulesMaxNumberText.addModifyListener(e -> validateInput());
        fRulesMaxNumberText.setToolTipText(Messages.ParameterDialog_TextRulesMaxNumberToolTip);

        /* Setup sorting buttons */
        Group SortingOptionsGroup = new Group(composite, SWT.SHADOW_NONE);
        SortingOptionsGroup.setText(Messages.ParameterDialog_OptionsGroupLabel);
        SortingOptionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        SortingOptionsGroup.setLayout(new GridLayout(2, false));

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.horizontalSpan = 2;
        fSortAscendingRadionButton = new Button(SortingOptionsGroup, SWT.RADIO);
        fSortAscendingRadionButton.setText(Messages.ParameterDialog_AscendingOption);
        fSortAscendingRadionButton.setSelection(true);
        fSortAscendingRadionButton.setLayoutData(gd);
        fSortAscendingRadionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                super.widgetSelected(e);
                boolean selected = ((Button) e.widget).getSelection();
                if (!selected) {
                    isOrderAscending = false;
                    return;
                }
                isOrderAscending = true;
            }
        });

        fSortDescendingRadionButton = new Button(SortingOptionsGroup, SWT.RADIO);
        fSortDescendingRadionButton.setText(Messages.ParameterDialog_DescendingOption);
        fSortDescendingRadionButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

        applyDialogFont(composite);
        return composite;
    }

    /**
     * Returns the ok button.
     *
     * @return the ok button
     */
    protected Button getOkButton() {
        return fOkButton;
    }

    /**
     * Returns the string typed into this input dialog.
     *
     * @return the input string
     */
    public int getRulesMaxNumberValue() {
        return Integer.parseInt(fRulesMaxNumber);
    }

    /**
     * Returns the string typed into this input dialog.
     *
     * @return the input string
     */
    public boolean isSortingOrderAscending() {
        return isOrderAscending;
    }

    /**
     * Validates the input.
     * <p>
     * The default implementation of this framework method delegates the request
     * to the supplied input validator object.
     * </p>
     */
    protected void validateInput() {
        String errMsg = null;
        if (fValidator != null) {
            errMsg = fValidator.isValid(fRulesMaxNumberText.getText());
        }

        Control button = getButton(IDialogConstants.OK_ID);
        if (button != null) {
            button.setEnabled(errMsg == null);
        }
    }

    /**
     * Returns the style bits that should be used for the input text field.
     * Defaults to a single line entry. Subclasses may override.
     *
     * @return the integer style bits that should be used when creating the
     *         input text
     */
    protected int getInputTextStyle() {
        return SWT.SINGLE | SWT.BORDER;
    }
}
