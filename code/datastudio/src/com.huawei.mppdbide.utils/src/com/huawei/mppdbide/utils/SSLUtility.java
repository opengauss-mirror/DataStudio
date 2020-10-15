/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: class
 * 
 * Description: The Class SSLUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class SSLUtility {
    private static Map<String, Boolean> sslConnectionMap = new HashMap<String, Boolean>(
            MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    /**
     * Instantiates a new SSL utility.
     */
    private SSLUtility() {

    }

    /**
     * Put SSL login status.
     *
     * @param key the key
     * @param value the value
     */
    public static void putSSLLoginStatus(String key, Boolean value) {
        sslConnectionMap.put(key, value);
    }

    /**
     * Gets the status.
     *
     * @param key the key
     * @return the status
     */
    public static boolean getStatus(String key) {
        if (!sslConnectionMap.containsKey(key)) {
            return false;
        }
        return sslConnectionMap.get(key);
    }

    /**
     * Removes the SSL login status.
     *
     * @param key the key
     * @return true, if successful
     */
    public static boolean removeSSLLoginStatus(String key) {

        if (sslConnectionMap.containsKey(key)) {
            return sslConnectionMap.remove(key);
        }
        return false;
    }

}
