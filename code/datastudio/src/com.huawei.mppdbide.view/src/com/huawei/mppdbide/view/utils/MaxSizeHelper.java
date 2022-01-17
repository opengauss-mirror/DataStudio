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

/**
 * 
 * Title: class
 * 
 * Description: The Class MaxSizeHelper.
 *
 * @since 3.0.0
 */
public abstract class MaxSizeHelper {
    private static final int LEN_LIMIT = 1000;
    private static final String DOTS = "...";

    /**
     * Convert max size into server formate.
     *
     * @param maxsize the maxsize
     * @return the string
     */
    public static String convertMaxSizeIntoServerFormate(String maxsize) {
        String maxSizeForServer = "";
        switch (maxsize) {
            case "KB": {
                maxSizeForServer = "K";
                break;
            }
            case "MB": {
                maxSizeForServer = "M";
                break;
            }
            case "GB": {
                maxSizeForServer = "G";
                break;
            }
            case "TB": {
                maxSizeForServer = "T";
                break;
            }
            case "PB": {
                maxSizeForServer = "P";
                break;
            }
            default: {
                break;
            }
        }
        return maxSizeForServer;
    }

    /**
     * Trim text to limit end with dots.
     *
     * @param valueParam the value param
     * @return the string
     */
    public static String trimTextToLimitEndWithDots(String valueParam) {
        String value = valueParam;
        if (value.length() > LEN_LIMIT) {
            value = value.substring(0, LEN_LIMIT - DOTS.length()) + DOTS;
        }
        return value;
    }

}
