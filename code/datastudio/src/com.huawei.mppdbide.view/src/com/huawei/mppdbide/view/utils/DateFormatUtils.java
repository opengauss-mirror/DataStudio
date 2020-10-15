/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import java.sql.Timestamp;
import com.huawei.mppdbide.utils.ConvertTimeStampValues;
import com.huawei.mppdbide.utils.ConvertTimeValues;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;

/**
 * 
 * Title: class
 * 
 * Description: The Class DateFormatUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 13 Mar, 2020]
 * @since 13 Mar, 2020
 */
public class DateFormatUtils {
    /**
     * handle timestamp values
     * 
     * @param timestamp the time stamp
     * @return value the converted time stamp value
     */
    public static String handleTimeStampValues(Timestamp timestamp) {
        ConvertTimeStampValues value = null;
        String dateFormat = ((UserPreference) UserPreference.getInstance()).getDateTimeFormat();
        if (null != timestamp) {
            value = new ConvertTimeStampValues(timestamp.getTime(), dateFormat);
            return value.toString();
        }
        return "";
    }

    /**
     * handle time values
     * 
     * @param timestamp the time stamp
     * @return value the converted time value
     */
    public static String handleTimeValues(Timestamp timestamp) {
        ConvertTimeValues value = null;
        String timeFormat = PreferenceWrapper.getInstance().getPreferenceStore()
                .getString(MPPDBIDEConstants.TIME_FORMAT_VALUE);
        if (null != timestamp) {
            value = new ConvertTimeValues(timestamp.getTime(), timeFormat);
            return value.toString();
        }
        return "";
    }
}
