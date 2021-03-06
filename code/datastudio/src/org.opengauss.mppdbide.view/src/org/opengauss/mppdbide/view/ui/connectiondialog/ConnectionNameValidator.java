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

package org.opengauss.mppdbide.view.ui.connectiondialog;

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
 * @since 3.0.0
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
