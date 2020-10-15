/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class NumberValidator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class NumberValidator implements VerifyListener {
    /**
     * The number provided by the user.
     */
    private Text listenerSize;

    /**
     * Instantiates a new number validator.
     *
     * @param port the port
     */
    public NumberValidator(Text port) {
        listenerSize = port;
    }

    /**
     * Verify text.
     *
     * @param e the e
     */
    @Override
    public void verifyText(VerifyEvent e) {
        final String oldDebugHostPort = listenerSize.getText();
        final String newDebugHostPort = oldDebugHostPort.substring(0, e.start) + e.text
                + oldDebugHostPort.substring(e.end);

        if (0 != newDebugHostPort.length()) {
            try {
                // Validates the input is long value only.
                if (Long.parseLong(newDebugHostPort) < 0) {
                    e.doit = false;
                }
            } catch (final NumberFormatException numberFormatException) {
                e.doit = false;
            }
        }

    }
}
