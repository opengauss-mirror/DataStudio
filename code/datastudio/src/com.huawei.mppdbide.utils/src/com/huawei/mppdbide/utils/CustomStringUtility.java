/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomStringUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CustomStringUtility {
    private static final String OTHER_VERSION_BUILD_INFO_REGEX = "(\\s+(Release|build|debug)\\s+\\w+){0,}";

    private static final String OPENGAUSS = "openGauss";
    private static final String OPENGAUSS_VERSION_REGEX = "(?i)(openGauss)\\s+([^\\s]+)";
    private static final String OPENGAUSS_POSTGRESQL_VERSION = "PostgreSQL 9.2.4 (GaussDB Kernel V500R001C20 build )";

    /**
     * Removes the delemeters.
     *
     * @param str the str
     * @param del the del
     * @return the string
     */
    public static String removeDelemeters(String str, String del) {
        if (str.contains(del)) {
            str = str.replaceAll(del, " ");
        }
        return str;
    }

    /**
     * Gets the server type.
     *
     * @param vrsion the vrsion
     * @return the server type
     */
    public static String[] getServerType(String vrsion) {
        String[] typeAndInfo = new String[2];
        typeAndInfo[0] = "";
        typeAndInfo[1] = "";
        if (vrsion == null) {
            return typeAndInfo;
        }

        String version = getFullServerVersionString(vrsion);
        Matcher matchOpenGauss = Pattern.compile(OPENGAUSS_VERSION_REGEX).matcher(version);
        if (matchOpenGauss.find()) {
            typeAndInfo[0] = matchOpenGauss.group(1);
        }
        if (typeAndInfo[0].length() < version.length()) {
            typeAndInfo[1] = version.substring(typeAndInfo[0].length(), version.length()).trim();
        }
        return typeAndInfo;
    }

    /**
     * Gets the full server version string.
     *
     * @param version the version
     * @return the full server version string
     */
    public static String getFullServerVersionString(String version) {
        version = removeDelemeters(version, "-");
        version = removeDelemeters(version, "_");
        Matcher matchOlap = Pattern.compile("(" + OPENGAUSS_VERSION_REGEX + ")" + OTHER_VERSION_BUILD_INFO_REGEX)
                .matcher(version);
        if (matchOlap.find()) {
            return matchOlap.group().trim();
        }

        String olap = parseServerVersion(version);
        return olap;
    }

    /**
     * Sanitize export file name.
     *
     * @param name the name
     * @return the string
     */

    public static String sanitizeExportFileName(String name) {
        if (name == null) {
            return "";
        }
        String sanitizedName = Normalizer.normalize(name, Form.NFKC);

        Pattern pattern = Pattern.compile(".*[\\\\/:*?\"<>|].*");

        if (pattern.matcher(sanitizedName).matches()) {
            sanitizedName = sanitizedName.replaceAll("[\\\\/:*?\"<>|]", "");
        }
        pattern = Pattern.compile(".*[ ].*");
        if (pattern.matcher(sanitizedName).matches()) {
            sanitizedName = sanitizedName.replaceAll("[ ]", "_");
        }

        return sanitizedName;
    }

    /**
     * Gets the formated output.
     *
     * @param s the s
     * @param seperator the seperator
     * @return the formated output
     */
    public static String getFormatedOutput(List<String> s, String seperator) {
        if (null != s) {
            int size = s.size();
            if (size > 0) {
                if (1 == size) {
                    return s.get(0);
                } else {
                    StringBuilder sb = new StringBuilder(s.get(0));
                    for (int i = 1; i < size; i++) {
                        sb.append(seperator);
                        sb.append(s.get(i));
                    }

                    return sb.toString();
                }
            }
        }
        return "";
    }

    /**
     * Convert string date format.
     *
     * @param dateString the date string
     * @param newFormat the new format
     * @return the string
     */
    public static String convertStringDateFormat(String dateString, String newFormat) {
        try {
            Date date = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MPPDBIDEConstants.DATE_FORMAT);
            date = simpleDateFormat.parse(dateString);
            String newDateString = new SimpleDateFormat(newFormat).format(date);
            return newDateString;
        } catch (ParseException e) {
            return dateString;
        }
    }

    /**
     * Gets the formatted string for alias compare.
     *
     * @param inString the in string
     * @return the formatted string for alias compare
     */
    public static String getFormattedStringForAliasCompare(String inString) {
        if (inString.startsWith("\"") && inString.endsWith("\"")) {
            return inString.substring(1, inString.length() - 1);
        } else {
            return inString.toLowerCase(Locale.ENGLISH);
        }
    }

    /**
     * Checks if is ends with dot.
     *
     * @param prefix the prefix
     * @return true, if is ends with dot
     */
    public static boolean isEndsWithDot(String prefix) {
        if (!prefix.isEmpty() && '.' == prefix.trim().charAt(prefix.length() - 1)) {
            return true;
        }
        return false;
    }

    /**
     * Parses the server version.
     *
     * @param version the version
     * @return the string
     */
    public static String parseServerVersion(String version) {
        Matcher matchOpenGauss = Pattern.compile(OPENGAUSS_VERSION_REGEX).matcher(version);
        if (matchOpenGauss.find()) {
            return matchOpenGauss.group();
        } else if (version.contains(OPENGAUSS_POSTGRESQL_VERSION)) {
            return OPENGAUSS_POSTGRESQL_VERSION;
        } else {
            return OPENGAUSS;
        }
    }

    /**
     * Sanitize excel sheet name.
     *
     * @param fileNameParam the file name param
     * @return the string
     */
    public static String sanitizeExcelSheetName(String fileNameParam) {
        if (fileNameParam == null) {
            return "";
        }
        String fileName = fileNameParam;
        Pattern pattern = Pattern.compile(".*[\\\\/:*?\"<>|\\[\\]'].*");

        if (pattern.matcher(fileName).matches()) {
            fileName = fileName.replaceAll("[\\\\/:*?\"<>|\\[\\]']", "");
        }

        pattern = Pattern.compile(".*[ ].*");
        if (pattern.matcher(fileName).matches()) {
            fileName = fileName.replaceAll("[ ]", "_");
        }

        return fileName;
    }

    /**
     * Checks if is protocol version needed.
     *
     * @param driverName the driver name
     * @return true, if is protocol version needed
     */
    public static boolean isProtocolVersionNeeded(String driverName) {
        if (driverName != null && (driverName.contains(MPPDBIDEConstants.GAUSS200V1R5DRIVER)
                || driverName.contains(MPPDBIDEConstants.GAUSS200V1R6DRIVER))) {
            return true;
        }
        return false;
    }
}
