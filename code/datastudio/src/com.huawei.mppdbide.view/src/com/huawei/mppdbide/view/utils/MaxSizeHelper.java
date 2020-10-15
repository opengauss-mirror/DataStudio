/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

/**
 * 
 * Title: class
 * 
 * Description: The Class MaxSizeHelper.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
