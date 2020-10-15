/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.view.utils.IUserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class MaximumRecordsValidator.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
                // DTS2014111206429 start
                else if (Integer.parseInt(newMaxRecords) > IUserPreference.MAX_RESULTSET_FETCH_COUNT) {
                    event.doit = false;
                }
                // DTS2014111206429 end
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
