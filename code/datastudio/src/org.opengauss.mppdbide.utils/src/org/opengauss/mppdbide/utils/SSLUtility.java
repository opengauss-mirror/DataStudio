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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Title: class
 * 
 * Description: The Class SSLUtility.
 *
 * @since 3.0.0
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
