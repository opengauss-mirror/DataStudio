/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: DSUnstructuredDataConversionHelper
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, 22-Jan-2020]
 * @since 22-Jab-2020
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
