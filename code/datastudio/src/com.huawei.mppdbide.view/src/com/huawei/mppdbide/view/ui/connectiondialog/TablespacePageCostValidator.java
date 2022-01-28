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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class TablespacePageCostValidator.
 *
 * @since 3.0.0
 */
public class TablespacePageCostValidator implements VerifyListener {
    private Text listenerPort;

    /**
     * Instantiates a new tablespace page cost validator.
     *
     * @param port the port
     */
    public TablespacePageCostValidator(Text port) {
        listenerPort = port;
    }

    /**
     * Verify text.
     *
     * @param verifyEvent the e
     */
    @Override
    public void verifyText(VerifyEvent verifyEvent) {
        final String oldCost = listenerPort.getText();
        final String newCost = oldCost.substring(0, verifyEvent.start) + verifyEvent.text
                + oldCost.substring(verifyEvent.end);
        Pattern pattern = Pattern.compile("\\d+|\\d*\\.\\d*");

        Matcher matcher = pattern.matcher(newCost);
        if (!(matcher.matches() || newCost.isEmpty())) {
            verifyEvent.doit = false;
        }
    }

}
