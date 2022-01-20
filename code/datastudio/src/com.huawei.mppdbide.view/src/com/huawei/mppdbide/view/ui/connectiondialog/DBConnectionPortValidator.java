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
 * Description: The Class DBConnectionPortValidator.
 *
 * @since 3.0.0
 */
public class DBConnectionPortValidator implements VerifyListener {
    /**
     * The port number provided by the user.
     */
    private Text listenerPort;

    /**
     * Max allowed number for port
     */
    private static final int MAX_ALLOWED_PORT_NUMBER = 65535;

    /**
     * Instantiates a new DB connection port validator.
     *
     * @param port the port
     */
    public DBConnectionPortValidator(Text port) {
        listenerPort = port;
    }

    /**
     * Verify text.
     *
     * @param e the e
     */
    @Override
    public void verifyText(VerifyEvent e) {
        final String oldDebugHostPort = listenerPort.getText();
        final String newDebugHostPort = oldDebugHostPort.substring(0, e.start) + e.text
                + oldDebugHostPort.substring(e.end);

        if (0 != newDebugHostPort.length()) {
            try {
                // Validates the input is integer value only.
                if (Integer.parseInt(newDebugHostPort) > MAX_ALLOWED_PORT_NUMBER) {
                    e.doit = false;
                }
            } catch (final NumberFormatException numberFormatException) {
                e.doit = false;
            }
        }

    }

    /**
     * Gets the max allowed port number.
     *
     * @return the max allowed port number
     */
    public int getMaxAllowedPortNumber() {
        return MAX_ALLOWED_PORT_NUMBER;
    }
}
