/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBConnectionValidator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DBConnectionValidator implements VerifyListener {
    /**
     * The port number provided by the user.
     */
    private Text listenerText;
    private int maxlimit;

    /**
     * Instantiates a new DB connection validator.
     *
     * @param inputField the input field
     * @param maxlimit the maxlimit
     */

    public DBConnectionValidator(Text inputField, int maxlimit) {
        listenerText = inputField;
        this.maxlimit = maxlimit;
    }

    /**
     * Verify text.
     *
     * @param e the e
     */
    @Override
    public void verifyText(VerifyEvent e) {
        final String initialValue = listenerText.getText();
        final String finalValue = initialValue.substring(0, e.start) + e.text + initialValue.substring(e.end);

        if ((e.keyCode == SWT.KEYPAD_0 || 48 == e.keyCode) && 0 == initialValue.length()) {
            e.doit = false;

        }

        if (0 != finalValue.length()) {
            try {
                // Validates the input is integer value only.
                if ((Integer.parseInt(finalValue) > maxlimit) || Integer.parseInt(finalValue) < 0) {
                    e.doit = false;
                }
            } catch (final NumberFormatException numberFormatException) {
                e.doit = false;
            }
        }

    }

    /**
     * Gets the max allowed number.
     *
     * @return the max allowed number
     */
    public int getMaxAllowedNumber() {
        return maxlimit;
    }
}
