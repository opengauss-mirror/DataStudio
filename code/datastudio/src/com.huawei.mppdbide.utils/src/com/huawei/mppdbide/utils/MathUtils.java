/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * Title: class
 * 
 * Description: The Class MathUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class MathUtils {

    /**
     * Round double values.
     *
     * @param value the value
     * @param placesParam the places param
     * @return the double
     */
    public static double roundDoubleValues(double value, int placesParam) {
        int places = placesParam;
        if (places < 0) {
            places = 0;
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
