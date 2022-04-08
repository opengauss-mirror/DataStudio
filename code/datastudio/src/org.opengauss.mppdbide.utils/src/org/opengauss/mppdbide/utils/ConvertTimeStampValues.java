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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertTimeStampValues.
 *
 * @since 3.0.0
 */
public class ConvertTimeStampValues extends Timestamp {

    private static final long serialVersionUID = 1L;
    private String dateFormat;
    private long timeStamp;

    /**
     * Instantiates a new convert time stamp values.
     *
     * @param time the time
     */
    public ConvertTimeStampValues(long time, String dateFormat) {
        super(time);
        this.dateFormat = dateFormat;
        this.timeStamp = time;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String date = sdf.format(new Date(timeStamp));
        return date;
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
