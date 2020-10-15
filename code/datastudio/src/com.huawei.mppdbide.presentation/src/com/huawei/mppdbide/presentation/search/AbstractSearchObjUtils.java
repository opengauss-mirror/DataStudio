/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.search;

import java.util.ArrayList;

import com.huawei.mppdbide.bl.search.SearchNameMatchEnum;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractSearchObjUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
