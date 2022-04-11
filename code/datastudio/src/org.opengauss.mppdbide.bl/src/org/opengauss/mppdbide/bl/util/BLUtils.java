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

package org.opengauss.mppdbide.bl.util;

import java.util.ArrayList;

/**
 * 
 * Title: class
 * 
 * Description: The Class BLUtils.
 * 
 */

public final class BLUtils implements IBLUtils {

    private String[] args;
    private String installationLocation = ".";
    private static volatile IBLUtils instance;
    private static final Object LOCK = new Object();

    private BLUtils() {

    }

    /**
     * Gets the single instance of BLUtils.
     *
     * @return single instance of BLUtils
     */
    public static IBLUtils getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new BLUtils();
                }
            }
        }

        return instance;
    }

    /**
     * Sets the platform args.
     *
     * @param arguments the new platform args
     */
    @Override
    public void setPlatformArgs(String[] arguments) {
        this.args = arguments.clone();
    }

    /**
     * Gets the platform args.
     *
     * @return getPlatformArgs
     */
    @Override
    public String[] getPlatformArgs() {
        if (args != null) {
            return args.clone();
        } else {
            return new String[0];
        }

    }

    /**
     * Gets the installation location.
     *
     * @return the installation location
     */
    @Override
    public String getInstallationLocation() {
        return installationLocation;
    }

    /**
     * Gets the un quoted identifier.
     *
     * @param str the str
     * @param quote the quote
     * @return the un quoted identifier
     */
    public static String getUnQuotedIdentifier(String str, String quote) {
        if (str.startsWith(quote) && str.endsWith(quote)) {
            return str.substring(quote.length(), str.length() - quote.length());
        }
        return str;
    }

    /**
     * Gets the all combinations of prefix.
     *
     * @param prefix the prefix
     * @return the all combinations of prefix
     */
    public static ArrayList<String> getAllCombinationsOfPrefix(String prefix) {
        ArrayList<String> prefixList = new ArrayList<String>(10);
        char[] chars = prefix.toCharArray();
        char[] permutation = null;
        int number = ((Double) (Math.pow(2, chars.length))).intValue();
        for (int index = 0; index < number; index++) {
            permutation = new char[chars.length];
            for (int jindex = 0; jindex < chars.length; jindex++) {
                permutation[jindex] = (isBitSet(index, jindex)) ? Character.toUpperCase(chars[jindex]) : chars[jindex];
            }
            prefixList.add(String.valueOf(permutation));
        }

        return prefixList;

    }

    private static boolean isBitSet(int num, int offset) {
        return (num >> offset & 1) != 0;
    }

}
