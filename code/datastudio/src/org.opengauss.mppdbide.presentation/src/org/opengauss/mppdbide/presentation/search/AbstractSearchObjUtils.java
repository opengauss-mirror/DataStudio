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

package org.opengauss.mppdbide.presentation.search;

import java.util.ArrayList;

import org.opengauss.mppdbide.bl.search.SearchNameMatchEnum;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractSearchObjUtils.
 * 
 * @since 3.0.0
 */
public abstract class AbstractSearchObjUtils {

    /**
     * Gets the index by value.
     *
     * @param array the array
     * @param value the value
     * @return the index by value
     */
    public static int getIndexByValue(ArrayList<String> array, String value) {
        for (int index = 0; index < array.size(); index++) {
            if (value.equals(array.get(index))) {
                return index;
            }
        }
        return 0;
    }

    private static String getWildcardEscapedString(String str) {
        String newStr = str;
        newStr = newStr.replace("_", MPPDBIDEConstants.ESCAPE_STRING_UNDERSCORE);
        newStr = newStr.replace("%", MPPDBIDEConstants.ESCAPE_STRING_PERCENTILE);
        return newStr;
    }

    /**
     * Form queryby name match.
     *
     * @param matchIndex the match index
     * @param searchText the search text
     * @return the string
     */
    public static String formQuerybyNameMatch(SearchNameMatchEnum matchIndex, String searchText) {
        StringBuilder builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        switch (matchIndex) {
            case CONTAINS: {
                builder.append("%");
                builder.append(searchText);
                builder.append("%");
                break;
            }
            case STARTS_WITH: {
                builder.append(getWildcardEscapedString(searchText));
                builder.append("%");
                break;
            }
            case EXACT_WORD: {
                builder.append(getWildcardEscapedString(searchText));
                break;
            }
            case REGULAR_EXPRESSION: {
                builder.append(searchText);
                break;
            }
            default: {
                break;
            }
        }
        return builder.toString();

    }

}
