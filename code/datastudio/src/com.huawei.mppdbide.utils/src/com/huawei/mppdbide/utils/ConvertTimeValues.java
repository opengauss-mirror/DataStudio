/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConvertTimeValues.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
