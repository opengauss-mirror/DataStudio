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

package com.huawei.mppdbide.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * Title: class
 * 
 * Description: The Class MathUtils.
 *
 * @since 3.0.0
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
