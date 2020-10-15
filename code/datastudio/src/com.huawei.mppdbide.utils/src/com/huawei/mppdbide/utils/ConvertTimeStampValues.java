/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertTimeStampValues.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
