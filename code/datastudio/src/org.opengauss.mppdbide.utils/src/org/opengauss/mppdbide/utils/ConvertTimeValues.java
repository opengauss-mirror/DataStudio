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

package org.opengauss.mppdbide.utils;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertTimeValues.
 *
 * @since 3.0.0
 */
public class ConvertTimeValues extends Time {
    private static final long serialVersionUID = 1L;
    private String timeFormat;
    private long millisecs;

    /**
     * Instantiates a new convert time values.
     *
     * @param millis the millis
     */
    public ConvertTimeValues(long millis, String timeFormat) {
        super(millis);
        this.timeFormat = timeFormat;
        this.millisecs = millis;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        String time = sdf.format(new Date(millisecs));
        return time;
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(object);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
