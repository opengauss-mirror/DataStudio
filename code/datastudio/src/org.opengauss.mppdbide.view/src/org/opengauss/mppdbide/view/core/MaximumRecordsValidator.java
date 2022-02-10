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

package org.opengauss.mppdbide.view.core;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.view.utils.IUserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class MaximumRecordsValidator.
 *
 * @since 3.0.0
 */
public class MaximumRecordsValidator implements VerifyListener {
    /**
     * The port number provided by the user.
     */
    private Text listenerMaxRecords;

    private static final int MIN_RECORDS_ALLOWED = 0;

    /**
     * Instantiates a new maximum records validator.
     *
     * @param maxRecords the max records
     */
    public MaximumRecordsValidator(Text maxRecords) {
        listenerMaxRecords = maxRecords;
    }

    /**
     * Verify text.
     *
     * @param event the event
     */
    @Override
    public void verifyText(VerifyEvent event) {
        final String oldMaxRecords = listenerMaxRecords.getText();
        final String newMaxRecords = oldMaxRecords.substring(0, event.start) + event.text
                + oldMaxRecords.substring(event.end);

        if (0 != newMaxRecords.length()) {
            try {
                // Validates the input is integer value only.
                if (Integer.parseInt(newMaxRecords) < MIN_RECORDS_ALLOWED) {
                    event.doit = false;
                }
                else if (Integer.parseInt(newMaxRecords) > IUserPreference.MAX_RESULTSET_FETCH_COUNT) {
                    event.doit = false;
                }
            } catch (final NumberFormatException numberFormatException) {
                event.doit = false;
            }
        }

    }

    /**
     * Gets the min allowed records.
     *
     * @return the min allowed records
     */
    public int getMinAllowedRecords() {
        return MIN_RECORDS_ALLOWED;
    }
}
