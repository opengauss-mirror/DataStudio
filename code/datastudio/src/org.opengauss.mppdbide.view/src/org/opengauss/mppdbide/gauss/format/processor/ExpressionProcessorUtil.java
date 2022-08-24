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

package org.opengauss.mppdbide.gauss.format.processor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;

/**
 * Title: ExpressionProcessorUtil
 *
 * @since 3.0.0
 */
public class ExpressionProcessorUtil {

    private static Set<String> currentTextSet = new HashSet<>(Arrays.asList(".", "(", ",", ")", ";", "|"));

    private static Set<String> preTextSet = new HashSet<>(Arrays.asList(".", "(", "|"));

    private static Set<String> relationoperator = new HashSet<>(Arrays.asList("<", ">", "="));

    private static Map<String, List<String>> currentPreTextMap = new HashMap<>(10);

    static {
        currentPreTextMap.put("=", Arrays.asList(":", ">", "<", "!", "="));
        currentPreTextMap.put("<", Arrays.asList("=", ">", "<"));
        currentPreTextMap.put(">", Arrays.asList("=", "<"));
        currentPreTextMap.put("!", Arrays.asList("="));
        currentPreTextMap.put(":", Arrays.asList("=", ":"));
        currentPreTextMap.put("|", Arrays.asList("|"));
        currentPreTextMap.put("@", Arrays.asList("<"));
    }

    /**
     * return true if no space appended
     * 
     * @param currenttext current text to compare
     * @param preText pretext to compare
     * @param prePreText prePretext to compare
     * @return true if no space appended
     */
    public static boolean isAppendNoPreTextSpace(String currenttext, String preText, String prePreText) {
        if (isCurrentTextNoSapce(currenttext) || isPreTextNoSapce(preText)) {
            return true;
        } else {
            List<String> crrMap = currentPreTextMap.get(currenttext);
            if (null != crrMap) {
                return crrMap.contains(preText);
            }

            // check for pre pre text
            if ("$".equals(preText)) {
                return true;
            }

            if (("-".equals(preText) || "+".equals(preText)) && NumberUtils.isNumber(currenttext)
                    && relationoperator.contains(prePreText)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCurrentTextNoSapce(String currenttext) {
        if (currentTextSet.contains(currenttext)) {
            return true;
        }
        return false;
    }

    private static boolean isPreTextNoSapce(String preText) {
        if (preTextSet.contains(preText)) {
            return true;
        }
        return false;
    }

}
