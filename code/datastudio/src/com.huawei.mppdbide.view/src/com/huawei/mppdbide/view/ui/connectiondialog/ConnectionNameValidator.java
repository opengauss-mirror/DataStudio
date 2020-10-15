/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.connectiondialog;

import java.util.regex.Pattern;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionNameValidator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConnectionNameValidator implements VerifyListener {
    /**
     * The connection name provided by the user.
     */
    private Object listenerName;

    /**
     * Instantiates a new connection name validator.
     *
     * @param name the name
     */
    public ConnectionNameValidator(Object name) {
        listenerName = name;
    }

    /**
     * Verify text.
     *
     * @param verifyEvent the e
     */
    @Override
    public void verifyText(VerifyEvent verifyEvent) {
        String oldNameField = null;
        if (listenerName instanceof Text) {
            Text text = (Text) listenerName;
            oldNameField = text.getText();
        } else if (listenerName instanceof StyledText) {
            StyledText text = (StyledText) listenerName;
            oldNameField = text.getText();
        }

        if (oldNameField != null) {
            final String newNameField = oldNameField.substring(0, verifyEvent.start) + verifyEvent.text
                    + oldNameField.substring(verifyEvent.end);

            if (0 != newNameField.length()) {
                final Pattern pattern = Pattern.compile(IDBConnectionValidationRegEx.REGEX_CONNECTION_NAME);
                if (pattern.matcher(verifyEvent.text).matches() || "\\".equalsIgnoreCase(verifyEvent.text)) {
                    verifyEvent.doit = false;
                }
            }
        }

    }
}
