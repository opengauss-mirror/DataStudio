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
 * @since 3.0.0
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
