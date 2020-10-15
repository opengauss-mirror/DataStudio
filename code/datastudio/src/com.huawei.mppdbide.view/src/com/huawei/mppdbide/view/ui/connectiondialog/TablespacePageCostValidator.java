/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
