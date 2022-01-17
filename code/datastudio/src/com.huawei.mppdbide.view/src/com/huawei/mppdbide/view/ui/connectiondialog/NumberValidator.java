/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
 * @since 3.0.0
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
