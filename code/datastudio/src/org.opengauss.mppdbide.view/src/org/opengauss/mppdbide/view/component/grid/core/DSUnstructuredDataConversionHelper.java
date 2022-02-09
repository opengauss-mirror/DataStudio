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

package org.opengauss.mppdbide.view.component.grid.core;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: DSUnstructuredDataConversionHelper
 *
 * @since 3.0.0
 */
public class DSUnstructuredDataConversionHelper {

    /**
     * Bytes to hex.
     *
     * @param blobInBytes the blob in bytes
     * @return the string
     */
    public static String bytesToHex(byte[] bytes) {
        if (null == bytes) {
            return "";
        }
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (byte b : bytes) {
            sb.append(String.format(Locale.ENGLISH, "%02x", b));
        }
        return sb.toString().toUpperCase(Locale.ENGLISH);
    }

    /**
     * Hex string to byte array.
     *
     * @param str the str
     * @return the byte[]
     */
    public static byte[] hexStringToByteArray(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        int len = str.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Bytes to hex formated.
     *
     * @param byteA the byte A
     * @return the string
     */
    public static String bytesToHexFormated(byte[] bytes) {
        if (null == bytes) {
            return "";
        }
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("\\x");
        for (byte b : bytes) {
            sb.append(String.format(Locale.ENGLISH, "%02x", b));
        }
        return sb.toString().toLowerCase(Locale.ENGLISH);
    }

    /**
     * Convert string to hex.
     *
     * @param str the str
     * @return the string
     */
    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }
}
